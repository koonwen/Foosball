package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foosball.database.BasicDatabaseListener;
import com.example.foosball.database.Database;
import com.example.foosball.database.GameStatusListener;
import com.example.foosball.utils.Utils;

import java.util.ArrayList;

/**
 * Represents the game lobby screen.
 */
public class LobbyActivity extends FullScreenActivity {
    private static final String TAG = "LobbyActivity";

    /**
     * Stores the player 1 - 2 text views in an array list
     *
     * @return array list of the text views in activity_lobby.xml
     */
    private ArrayList<TextView> getPlayerTextViews() {

        ArrayList<TextView> playerTextViews = new ArrayList<>();
        playerTextViews.add(findViewById(R.id.player1Text));
        playerTextViews.add(findViewById(R.id.player2Text));

        return playerTextViews;
    }

    /**
     * Initialises the logic of the buttons and pulls data from the db upon creation of the
     * lobby activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Sets the start game button as hidden by default
        final Button startGameButton = findViewById(R.id.startGame);
        startGameButton.setVisibility(View.GONE);

        final String gameCode = Utils.getGameCode(getApplicationContext());
        final TextView gameCodeText = findViewById(R.id.codeID);
        gameCodeText.setText(gameCode);
        final String placeholderPlayerName =
                getResources().getString(R.string.placeholder_player_name);

        final Database database = Database.getInstance();

        // Starts the game status listener to constantly update the player names and game status.
        // Updates the names of the players and sets the visibility of the start game button
        // depending on whether the current player is player1 "i.e. host".
        database.startGameStatusListener(new GameStatusListener() {
            @Override
            public void onSuccess(ArrayList<String> playerNames, Boolean twoPlayers,
                                  Boolean gameStarted, Boolean gameEnded) {
                Log.d(TAG, "Game status update - Players: " + playerNames + ", gameStarted: "
                        + gameStarted + ", gameEnded: " + gameEnded);

                ArrayList<TextView> playerTextViews = getPlayerTextViews();
                assert playerTextViews.size() == playerNames.size();

                for (int i = 0; i < playerTextViews.size(); i++) {
                    final TextView playerTextView = playerTextViews.get(i);
                    final String playerName = playerNames.get(i);
                    if (playerName == null) {
                        playerTextView.setText(placeholderPlayerName);
                    } else {
                        playerTextView.setText(playerName);
                    }
                }

                // Automatically start game once host has started game
                if (gameStarted) {
                    Log.i(TAG, "Starting game... moving to game screen");
                    final Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    database.stopGameStatusListener();
                    startActivity(intent);
                }

                // Make start button visible for the host if there are two players
                if (Utils.isGameHost(getApplicationContext()) && twoPlayers) {
                    startGameButton.setVisibility(View.VISIBLE);
                } else {
                    startGameButton.setVisibility(View.GONE);
                }
                // TODO: Remove line before production. Uncomment for testing game without needing
                // a second player.
                // startGameButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onConnectionError() {
                Log.e(TAG, "Connection error while listening to game status");
            }
        });

        final int playerId = Utils.getPlayerId(getApplicationContext());
        final Button returnMenu = findViewById(R.id.returnMenu);

        // Removes the current player when back button is clicked and starts the main menu activity.
        returnMenu.setOnClickListener(view -> {
            Log.i(TAG, "Trying to exit lobby");
            database.removePlayer(playerId, new BasicDatabaseListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "Moving back to main menu screen");
                    final Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onConnectionError() {
                    Log.e(TAG, "Connection error while trying to exit lobby");
                }
            });
        });

        // Updates the status of the game on the db and starts the game activity when start game
        // button is clicked.
        startGameButton.setOnClickListener(view -> {
            Log.i(TAG, "Trying to start game");
            database.updateStartGame(new BasicDatabaseListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "Successfully started game");
                }

                @Override
                public void onConnectionError() {
                    Log.e(TAG, "Connection error while trying to start game");
                }
            });
        });
    }
}

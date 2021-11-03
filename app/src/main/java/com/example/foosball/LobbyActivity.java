package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foosball.database.BasicDatabaseListener;
import com.example.foosball.database.Database;
import com.example.foosball.database.GameStatusListener;

import java.util.ArrayList;

/**
 * Initialises the lobby screen
 */

public class LobbyActivity extends FullScreenActivity {

    /**
     * Stores the player 1 - 4 text views in an array list
     *
     * @return array list of the text views in activity_lobby.xml
     */
    private ArrayList<TextView> getPlayerTextViews() {

        ArrayList<TextView> playerTextViews = new ArrayList<>();
        playerTextViews.add(findViewById(R.id.player1Text));
        playerTextViews.add(findViewById(R.id.player2Text));
        playerTextViews.add(findViewById(R.id.player3Text));
        playerTextViews.add(findViewById(R.id.player4Text));

        return playerTextViews;
    }

    /**
     * Initialises the logic of the buttons and pulls data from the db upon creation of the
     * lobby activity
     *
     * @param savedInstanceState
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
        final String placeholderPlayerName =
                getResources().getString(R.string.placeholder_player_name);
        gameCodeText.setText(gameCode);

        final Database database = Database.getInstance();
        /**
         * Starts the game status listener to constantly update the player names and status of the game
         */
        database.startGameStatusListener(new GameStatusListener() {

            /**
             * Method is called when the game status listener successfully fetches data from the db
             *
             * Updates the names of the players and sets the visibility of the start game button
             * depending on whether the current player is player1 "i.e. host"
             *
             * @param playerNames List of player names.
             * @param evenPlayers Whether there is an even number of players.
             * @param gameStarted Whether the game has started.
             * @param gameEnded Whether the game has ended.
             */
            @Override
            public void onSuccess(ArrayList<String> playerNames, Boolean evenPlayers,
                                  Boolean gameStarted, Boolean gameEnded) {
                ArrayList<TextView> playerTextViews = getPlayerTextViews();

                for (int i = 0; i < playerTextViews.size(); i++) {
                    final TextView playerTextView = playerTextViews.get(i);
                    final String playerName = playerNames.get(i);
                    if (playerName == null) {
                        playerTextView.setText(placeholderPlayerName);
                    } else {
                        playerTextView.setText(playerName);
                    }
                }

                // If there are 2 or 4 players and host, then make start button visible
                if (Utils.isGameHost(getApplicationContext()) && evenPlayers
                        && !gameStarted && !gameEnded) {
                    startGameButton.setVisibility(View.VISIBLE);
                } else {
                    startGameButton.setVisibility(View.GONE);
                }

                // Automatically start game for rest of players once host has started game
                if (!Utils.isGameHost(getApplicationContext()) && gameStarted && !gameEnded) {
                    final Intent intent = new Intent(getApplicationContext(),
                            GameActivity.class);
                    database.stopGameStatusListener();
                    startActivity(intent);
                }
            }

            @Override
            public void onConnectionError() {
            }
        });


        final int playerId = Utils.getPlayerId(getApplicationContext());

        final Button returnMenu = findViewById(R.id.returnMenu);

        /**
         * Removes the current player when back button is clicked and starts the main menu activity
         */
        returnMenu.setOnClickListener(view ->
                database.removePlayer(playerId, new BasicDatabaseListener() {
                    @Override
                    public void onSuccess() {
                        final Intent intent = new Intent(getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onConnectionError() {
                    }
                })
        );

        /**
         * Updates the status of the game on the db and starts the game activity when start game
         * button is clicked
         */
        startGameButton.setOnClickListener(view -> {
            database.updateStartGame(new BasicDatabaseListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onConnectionError() {
                }
            });
            database.stopGameStatusListener();
            final Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        });

    }
}

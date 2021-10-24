package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foosball.database.BasicDatabaseListener;
import com.example.foosball.database.Database;
import com.example.foosball.database.GameStatusListener;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LobbyActivity extends FullScreenActivity {
    private ArrayList<TextView> getPlayerTextViews() {

        ArrayList<TextView> playerTextViews = new ArrayList<>();
        playerTextViews.add(findViewById(R.id.player1Text));
        playerTextViews.add(findViewById(R.id.player2Text));
        playerTextViews.add(findViewById(R.id.player3Text));
        playerTextViews.add(findViewById(R.id.player4Text));

        return playerTextViews;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        final Button startGameButton = findViewById(R.id.startGame);
        startGameButton.setVisibility(View.GONE);

        final String gameCode = Utils.getGameCode(getApplicationContext());
        final TextView gameCodeText = findViewById(R.id.codeID);
        final String placeholderPlayerName =
                getResources().getString(R.string.placeholder_player_name);
        gameCodeText.setText(gameCode);

        Database.startGameStatusListener(gameCode, new GameStatusListener() {
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
                    Database.stopGameStatusListener(gameCode);
                    startActivity(intent);
                }
            }

            @Override
            public void onConnectionError() {
            }
        });


        final int playerId = Utils.getPlayerId(getApplicationContext());

        final Button returnMenu = findViewById(R.id.returnMenu);
        returnMenu.setOnClickListener(view ->
                Database.removePlayer(gameCode, playerId, new BasicDatabaseListener() {
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

        startGameButton.setOnClickListener(view -> {
            Database.updateStartGame(gameCode, new BasicDatabaseListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onConnectionError() {
                }
            });
            Database.stopGameStatusListener(gameCode);
            final Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        });

    }
}

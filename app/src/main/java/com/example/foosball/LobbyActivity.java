package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foosball.database.Database;
import com.example.foosball.database.OnBasicDatabaseOperation;
import com.example.foosball.database.OnGetGameStatusOperation;

import java.util.ArrayList;

public class LobbyActivity extends FullScreenActivity {


    private ArrayList<TextView> getPlayerTextViews() {

        ArrayList<TextView> playerTextViews = new ArrayList<TextView>();
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
        final String currentPlayerName = Utils.getPlayerName(getApplicationContext());

        final String gameCode = Utils.getGameCode(getApplicationContext());
        final TextView gameCodeText = findViewById(R.id.codeID);
        gameCodeText.setText(gameCode);

        Database.startGameStatusListener(gameCode, new OnGetGameStatusOperation() {
            @Override
            public void onSuccess(ArrayList<String> playerNames, Boolean evenPlayers,
                                  Boolean gameStarted, Boolean gameEnded) {
                ArrayList<TextView> playerTextViews = getPlayerTextViews();

                int i = 0;
                for (String playerName : playerNames) {
                    TextView playerTextView = playerTextViews.get(i);
                    playerTextView.setText(playerName);
                    i++;
                }

                System.out.print(currentPlayerName);

                final String player1Name = playerTextViews.get(0).toString();

                // If there are 2 or 4 players and host, then make start button visible
                if (currentPlayerName == player1Name) {
                    startGameButton.setVisibility(View.VISIBLE);
                } else if (evenPlayers && gameStarted && !gameEnded) {
                    startGameButton.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onConnectionError() {

            }
        });


        final int playerId = Utils.getPlayerId(getApplicationContext());

        final Button returnMenu = findViewById(R.id.returnMenu);
        returnMenu.setOnClickListener(view ->
                Database.removePlayer(gameCode, playerId, new OnBasicDatabaseOperation() {
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
            final Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        });

    }
}

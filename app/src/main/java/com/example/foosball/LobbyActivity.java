package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.foosball.database.Database;
import com.example.foosball.database.OnBasicDatabaseOperation;
import com.example.foosball.database.OnGetPlayerNamesOperation;

import java.util.ArrayList;

public class LobbyActivity extends FullScreenActivity {


    private ArrayList<TextView> getPlayerTextViews(){

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

        final String gameCode = Utils.getGameCode(getApplicationContext());
        final TextView gameCodeText = findViewById(R.id.codeID);
        gameCodeText.setText(gameCode);

        // Pull names from db, and populate names in lobby
        Database.getPlayerNames(gameCode, new OnGetPlayerNamesOperation() {
            @Override
            public void onSuccess(ArrayList<String> playerNames) {

                ArrayList<TextView> playerTextViews = getPlayerTextViews();

                int i = 0;
                for (String playerName : playerNames) {
                    TextView playerTextView = playerTextViews.get(i);
                    playerTextView.setText(playerName);
                    i ++;
                }
            }

            @Override
            public void onConnectionError() {
                //TODO: Decide appropriate error when pulling player names fail
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
    }
}

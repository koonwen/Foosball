package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.foosball.database.Database;
import com.example.foosball.database.OnBasicDatabaseOperation;

public class LobbyActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        final String playerName = Utils.getPlayerName(getApplicationContext());
        final TextView player1Text = findViewById(R.id.player1Text);
        player1Text.setText(playerName);

        final String gameCode = Utils.getGameCode(getApplicationContext());
        final TextView gameCodeText = findViewById(R.id.codeID);
        gameCodeText.setText(gameCode);

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

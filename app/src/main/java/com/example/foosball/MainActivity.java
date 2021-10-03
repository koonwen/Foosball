package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends FullScreenActivity {

    private Button createGame;
    private Button joinGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createGame = findViewById(R.id.createGame);
        joinGame = findViewById(R.id.joinGame);
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText EditTextplayerName = findViewById(R.id.playerName);
                String playerName = EditTextplayerName.getText().toString();

                if (playerName.trim().isEmpty()) {
                    EditTextplayerName.setError("Please fill in name");
                    EditTextplayerName.requestFocus();
                } else {
                    Utils.setPlayerName(getApplicationContext(), playerName);
                    Database.createGame(playerName);
                    Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                    startActivity(intent);
                }

            }
        });
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText EditTextgameCode = findViewById(R.id.gameCode);
                String gameCode = EditTextgameCode.getText().toString();

                // TODO: Validation of gameCode with valid game codes when implementing networking
                if (gameCode.trim().isEmpty()) {
                    EditTextgameCode.setError("Please fill in code");
                    EditTextgameCode.requestFocus();
;                } else {
                    Utils.setGameCode(getApplicationContext(), gameCode);
                    Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}

package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends FullScreenActivity {

    private Button createGame;
    private Button joinGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createGame = findViewById(R.id.createGame);
        joinGame = findViewById(R.id.joinGame);
        createGame.setOnClickListener(view -> {
            EditText EditTextplayerName = findViewById(R.id.playerName);
            String playerName = EditTextplayerName.getText().toString();

            if (playerName.trim().isEmpty()) {
                EditTextplayerName.setError("Please fill in name");
                EditTextplayerName.requestFocus();
            } else {
                Utils.setPlayerName(getApplicationContext(), playerName);
                Database.createGame(playerName, new OnDatabaseOperation() {

                    @Override
                    public void onConnectionError() {
                        Toast.makeText(getApplicationContext(),
                                "Connection error. Please try again",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                        startActivity(intent);
                    }
                });
            }

        });
        joinGame.setOnClickListener(view -> {

            EditText EditTextplayerName = findViewById(R.id.playerName);
            String playerName = EditTextplayerName.getText().toString();

            EditText EditTextgameCode = findViewById(R.id.gameCode);
            String gameCode = EditTextgameCode.getText().toString();

            if (playerName.trim().isEmpty()) {
                EditTextplayerName.setError("Please fill in name");
                EditTextplayerName.requestFocus();
                // TODO: Need to validate whether code is a valid format (i.e. 6 characters)
            } else if (gameCode.trim().isEmpty()) {
                EditTextgameCode.setError("Please fill in code");
                EditTextgameCode.requestFocus();
            } else {
                Database.joinGame(playerName, gameCode, new OnDatabaseOperation() {
                    @Override
                    void onConnectionError() {
                        Toast.makeText(getApplicationContext(),
                                "Connection error. Please try again",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    void onInputError(String errorMessage) {
                        Toast.makeText(getApplicationContext(),
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    void onSuccess() {
                        Utils.setGameCode(getApplicationContext(), gameCode);
                        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}

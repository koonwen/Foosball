package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foosball.database.Database;
import com.example.foosball.database.OnCreateGameOperation;
import com.example.foosball.database.OnDatabaseOperation;
import com.example.foosball.database.OnJoinGameOperation;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends FullScreenActivity {

    private Button createGame;
    private Button joinGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createGame = findViewById(R.id.createGame);
        joinGame = findViewById(R.id.joinGame);

        final EditText editTextGameCode = findViewById(R.id.gameCode);
        final InputFilter[] filters = {
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(Utils.NUM_CHARS_GAME_CODE)
        };
        editTextGameCode.setFilters(filters);

        createGame.setOnClickListener(view -> {
            String playerName = getPlayerName();
            if (playerName == null) {
                return;
            }
            Database.createGame(playerName, new OnCreateGameOperation() {
                @Override
                public void onConnectionError() {
                    displayConnectionError();
                }

                @Override
                public void onSuccess(String gameCode) {
                    goToLobbyScreen(gameCode);
                }
            });
        });

        joinGame.setOnClickListener(view -> {
            String playerName = getPlayerName();
            if (playerName == null) {
                return;
            }
            String gameCode = getGameCode();
            if (gameCode == null) {
                return;
            }

            Database.joinGame(playerName, gameCode, new OnJoinGameOperation() {
                @Override
                public void onConnectionError() {
                    displayConnectionError();
                }

                @Override
                public void onLobbyFullError() {
                    displaySnackbar("Lobby is currently full. Please try again later.");
                }

                @Override
                public void onGameDoesNotExistError() {
                    editTextGameCode.setError("Invalid game code.");
                    editTextGameCode.requestFocus();
                }

                @Override
                public void onGameAlreadyStartedError() {
                    editTextGameCode.setError("Game has already started.");
                    editTextGameCode.requestFocus();
                }

                @Override
                public void onSuccess() {
                    goToLobbyScreen(gameCode);
                }
            });
        });
    }

    private String getPlayerName() {
        EditText editTextPlayerName = findViewById(R.id.playerName);
        String playerName = editTextPlayerName.getText().toString().trim();

        if (playerName.isEmpty()) {
            editTextPlayerName.setError("Please fill in name");
            editTextPlayerName.requestFocus();
            return null;
        }

        Utils.setPlayerName(getApplicationContext(), playerName);
        return playerName;
    }

    private String getGameCode() {
        final EditText editTextGameCode = findViewById(R.id.gameCode);
        final String gameCode = editTextGameCode.getText().toString().trim();

        if (gameCode.isEmpty()) {
            editTextGameCode.setError("Please fill in code");
            editTextGameCode.requestFocus();
            return null;
        }

        return gameCode;
    }

    private void displayConnectionError() {
        displaySnackbar("Connection error. Please try again");
    }

    private void displaySnackbar(String message) {
        View root = findViewById(R.id.root);
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }

    private void goToLobbyScreen(String gameCode) {
        Utils.setGameCode(getApplicationContext(), gameCode);
        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        startActivity(intent);
    }
}

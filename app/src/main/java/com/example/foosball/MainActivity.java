package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.foosball.database.CreateGameListener;
import com.example.foosball.database.Database;
import com.example.foosball.database.JoinGameListener;
import com.google.android.material.snackbar.Snackbar;

/**
 * Initialises the main menu activity
 */
public class MainActivity extends FullScreenActivity {

    /**
     * Initialises text views, create and start game buttons when main menu activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button createGame = findViewById(R.id.createGame);
        final Button joinGame = findViewById(R.id.joinGame);

        final EditText editTextGameCode = findViewById(R.id.gameCode);
        final InputFilter[] filters = {
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(Utils.NUM_CHARS_GAME_CODE)
        };
        editTextGameCode.setFilters(filters);

        /**
         * Saves the entered player name, creates a new game on the db with the relevant game code
         * and starts the lobby activity
         * First checks whether the user has entered text in the player name field.
         */
        createGame.setOnClickListener(view -> {
            final String playerName = getPlayerName();
            if (playerName == null) {
                return;
            }

            /**
             * Connects to the db and starts the lobby screen upon successful connection
             */
            Database.createGame(playerName, new CreateGameListener() {
                @Override
                public void onConnectionError() {
                    displayConnectionError();
                }

                @Override
                public void onSuccess(String gameCode) {
                    Utils.setPlayerId(getApplicationContext(), Utils.HOST_PLAYER_ID);
                    goToLobbyScreen(gameCode);
                }
            });
        });

        /**
         * Checks the if the game code and player names fields are filled in.
         * If they are, queries the db for the entry with the corresponding game code, and returns
         * errors if there is connection error, lobby is full, game does not exist, or the
         * game has already started.
         *
         * If not, starts the lobby screen activity.
         */
        joinGame.setOnClickListener(view -> {
            editTextGameCode.setError(null);
            final String playerName = getPlayerName();
            if (playerName == null) {
                return;
            }
            final String gameCode = getGameCode();
            if (gameCode == null) {
                return;
            }

            Database.joinGame(playerName, gameCode, new JoinGameListener() {
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
                public void onSuccess(int playerId) {
                    Utils.setPlayerId(getApplicationContext(), playerId);
                    goToLobbyScreen(gameCode);
                }
            });
        });
    }

    /**
     * Returns the entered player name as a string, if not prompts user to enter player name.
     * @return string player name
     */
    private String getPlayerName() {
        final EditText editTextPlayerName = findViewById(R.id.playerName);
        final String playerName = editTextPlayerName.getText().toString().trim();

        if (playerName.isEmpty()) {
            editTextPlayerName.setError("Please fill in name");
            editTextPlayerName.requestFocus();
            return null;
        }

        Utils.setPlayerName(getApplicationContext(), playerName);
        return playerName;
    }

    /**
     * Returns the game code as a string, if not prompts user to enter game code.
     * @return game code as a string
     */
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

    /**
     * Displays UI error message to user if unable to connect to db
     */
    private void displayConnectionError() {
        displaySnackbar("Connection error. Please try again");
    }

    /**
     * Creates snack bar notification bar on UI with given error message.
     * @param message String error message
     */
    private void displaySnackbar(String message) {
        final View root = findViewById(R.id.root);
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Starts the lobby activity with the provided game code.
     * @param gameCode Game code
     */
    private void goToLobbyScreen(String gameCode) {
        Utils.setGameCode(getApplicationContext(), gameCode);
        final Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        startActivity(intent);
    }
}

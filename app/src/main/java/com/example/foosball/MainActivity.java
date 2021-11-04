package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.foosball.database.CreateGameListener;
import com.example.foosball.database.Database;
import com.example.foosball.database.JoinGameListener;
import com.example.foosball.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

/**
 * Represents the main menu screen.
 */
public class MainActivity extends FullScreenActivity {
    private static final String TAG = "MainActivity";

    /**
     * Initialises main activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button createGame = findViewById(R.id.createGame);
        final Button joinGame = findViewById(R.id.joinGame);
        final EditText editTextGameCode = findViewById(R.id.gameCode);

        // Ensure that game code edit text only accepts all caps characters and limits number of
        // characters based on the required number for the game code.
        final InputFilter[] filters = {
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(Utils.NUM_CHARS_GAME_CODE)
        };
        editTextGameCode.setFilters(filters);

        final Database database = Database.getInstance();

        // Saves the entered player name, creates a new game on the db with the relevant game code
        // and starts the lobby activity.
        // First checks whether the user has entered text in the player name field.
        createGame.setOnClickListener(view -> {
            final String playerName;
            try {
                playerName = getPlayerName();
            } catch (EmptyValueException e) {
                return;
            }
            Log.i(TAG, "Trying to create game...");

            // Connects to the db and starts the lobby screen upon successful connection
            database.createGame(playerName, new CreateGameListener() {
                @Override
                public void onConnectionError() {
                    Log.e(TAG, "Connection error while creating game.");
                    displayConnectionError();
                }

                @Override
                public void onSuccess(String gameCode) {
                    Log.i(TAG, "Successfully created game: " + gameCode);
                    Utils.setPlayerId(getApplicationContext(), Utils.HOST_PLAYER_ID);
                    goToLobbyScreen(gameCode);
                }
            });
        });

        // Checks if the game code and player names fields are filled in.
        // If they are, queries the db for the entry with the corresponding game code, and returns
        // errors if there is connection error, lobby is full, game does not exist, or the
        // game has already started. If not, starts the lobby screen activity.
        joinGame.setOnClickListener(view -> {
            final String playerName;
            final String gameCode;

            try {
                playerName = getPlayerName();
            } catch (EmptyValueException e) {
                return;
            }
            try {
                gameCode = getGameCode();
            } catch (EmptyValueException e) {
                return;
            }
            Log.i(TAG, "Trying to join game: " + gameCode);

            database.joinGame(playerName, gameCode, new JoinGameListener() {
                @Override
                public void onConnectionError() {
                    Log.e(TAG, "Connection error while joining game.");
                    displayConnectionError();
                }

                @Override
                public void onLobbyFullError() {
                    Log.i(TAG, "Lobby full: " + gameCode);
                    displaySnackbar("Lobby is currently full. Please try again later.");
                }

                @Override
                public void onGameDoesNotExistError() {
                    Log.i(TAG, "Invalid game code: " + gameCode);
                    editTextGameCode.setError("Invalid game code.");
                    editTextGameCode.requestFocus();
                }

                @Override
                public void onGameAlreadyStartedError() {
                    Log.i(TAG, "Game has started: " + gameCode);
                    editTextGameCode.setError("Game has already started.");
                    editTextGameCode.requestFocus();
                }

                @Override
                public void onSuccess(int playerId) {
                    Log.i(TAG, "Successfully joined game: " + gameCode
                            + ", playerId: " + playerId);
                    Utils.setPlayerId(getApplicationContext(), playerId);
                    goToLobbyScreen(gameCode);
                }
            });
        });
    }

    /**
     * Returns the entered player name as a string, if not prompts user to enter player name.
     *
     * @return Player's name.
     * @throws EmptyValueException If user did not type in a name.
     */
    private @NonNull String getPlayerName() throws EmptyValueException {
        final EditText editTextPlayerName = findViewById(R.id.playerName);
        final String playerName = editTextPlayerName.getText().toString().trim();

        if (playerName.isEmpty()) {
            editTextPlayerName.setError("Please fill in name");
            editTextPlayerName.requestFocus();
            throw new EmptyValueException();
        }

        return playerName;
    }

    /**
     * Returns the game code as a string, if not prompts user to enter game code.
     *
     * @return Game code.
     * @throws EmptyValueException If user did not type in game code.
     */
    private @NonNull String getGameCode() throws EmptyValueException {
        final EditText editTextGameCode = findViewById(R.id.gameCode);
        final String gameCode = editTextGameCode.getText().toString().trim();

        if (gameCode.isEmpty()) {
            editTextGameCode.setError("Please fill in code");
            editTextGameCode.requestFocus();
            throw new EmptyValueException();
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
     *
     * @param message String error message
     */
    private void displaySnackbar(String message) {
        final View root = findViewById(R.id.root);
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Starts the lobby activity with the provided game code.
     *
     * @param gameCode Game code
     */
    private void goToLobbyScreen(String gameCode) {
        Log.i(TAG, "Moving to lobby screen");
        Utils.setGameCode(getApplicationContext(), gameCode);
        final Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        startActivity(intent);
    }
}

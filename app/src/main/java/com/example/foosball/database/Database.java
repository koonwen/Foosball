package com.example.foosball.database;

import android.util.Log;

import com.example.foosball.MainActivity;
import com.example.foosball.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    private static final String TAG = "Database";
    private static final int[] PLAYER_IDS = {1, 2, 3, 4};
    private static final String DATABASE_PATH = "games";
    private static final String KEY_HAS_GAME_STARTED = "hasGameStarted";
    private static final String KEY_HAS_GAME_ENDED = "hasGameEnded";
    private static final String KEY_FORMAT_PLAYER = "player%1$s";

    // TODO: Figure out Firebase Database rules (currently there is no authentication)
    public static void createGame(String playerName, OnCreateGameOperation onCreateGameOperation) {
        final String gameCode = Utils.generateGameCode();

        Log.d(TAG, "Creating game and storing in firebase realtime database");
        final DatabaseReference ref = getGameReference(gameCode);
        handleFailure(ref.get(), onCreateGameOperation).addOnSuccessListener(res -> {
            // Check if gameCode already exists in the database
            if (res.exists()) {
                Log.d(TAG, "Game code " + gameCode + " already exists");
                Object hasGameEnded = res.child(KEY_HAS_GAME_ENDED).getValue();

                // If gameCode exists, check if the game has ended
                if ((hasGameEnded == null) || !(boolean) hasGameEnded) {
                    Log.d(TAG, "Game has not ended. Trying a new game code");
                    // If game has not ended, generate a new gameCode and restart
                    createGame(playerName, onCreateGameOperation);
                    return;
                }
                Log.d(TAG, "Game has ended. Deleting and creating a new document");
                // If game ended, then delete that document
                // and create a new one with the same gameCode
                ref.removeValue().addOnCompleteListener(removeTask ->
                        setUpNewGame(playerName, onCreateGameOperation, ref, gameCode));
                return;
            }
            // If gameCode does no exist, then use it
            Log.d(TAG, "Creating game with game code " + gameCode);
            setUpNewGame(playerName, onCreateGameOperation, ref, gameCode);
        });
    }

    private static void setUpNewGame(String playerName, OnCreateGameOperation onCreateGameOperation,
                                     DatabaseReference ref, String gameCode) {
        ref.child(getPlayerKey(MainActivity.HOST_PLAYER_ID)).setValue(playerName);
        ref.child(KEY_HAS_GAME_STARTED).setValue(false);
        ref.child(KEY_HAS_GAME_ENDED).setValue(false);
        onCreateGameOperation.onSuccess(gameCode);
    }

    public static void joinGame(String playerName, String gameCode,
                                OnJoinGameOperation onJoinGameOperation) {
        final DatabaseReference ref = getGameReference(gameCode);
        handleFailure(ref.get(), onJoinGameOperation).addOnSuccessListener(res -> {
            // Checks if gamecode exists by accessing result from the task
            // Also check if game has neither started nor ended
            final Object hasGameStartedObj = res.child(KEY_HAS_GAME_STARTED).getValue();
            final Object hasGameEndedObj = res.child(KEY_HAS_GAME_ENDED).getValue();

            final boolean gameExists = res.exists();
            final boolean hasGameStarted =
                    hasGameStartedObj == null || (boolean) hasGameStartedObj;
            final boolean hasGameEnded =
                    hasGameEndedObj == null || (boolean) hasGameEndedObj;

            if (!gameExists || hasGameEnded) {
                Log.d(TAG, "Game does not exist. Please check game code.");
                onJoinGameOperation.onGameDoesNotExistError();
                return;
            }
            if (hasGameStarted) {
                Log.d(TAG, "Game has already started.");
                onJoinGameOperation.onGameAlreadyStartedError();
                return;
            }
            // Checks whether keys of playernames exists, if not insert current player
            // Else if all playerkeys already exist, then lobby is already full
            for (int playerId : PLAYER_IDS) {
                final String playerKey = getPlayerKey(playerId);
                if (!res.child(playerKey).exists()) {
                    ref.child(playerKey).setValue(playerName);
                    onJoinGameOperation.onSuccess(playerId);
                    return;
                }
            }
            Log.d(TAG, "Lobby is full. Please try again later.");
            onJoinGameOperation.onLobbyFullError();
        });
    }

    public static void removePlayer(String gameCode, int playerId,
                                    OnBasicDatabaseOperation onBasicDatabaseOperation) {
        final DatabaseReference ref = getGameReference(gameCode);
        handleFailure(ref.get(), onBasicDatabaseOperation).addOnSuccessListener(res -> {
            ref.child(getPlayerKey(playerId)).removeValue();
            onBasicDatabaseOperation.onSuccess();
        });
    }

    private static DatabaseReference getGameReference(String gameCode) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(DATABASE_PATH).child(gameCode);
    }

    private static Task<DataSnapshot> handleFailure(Task<DataSnapshot> task,
                                                    OnDatabaseOperation onDatabaseOperation) {
        return task.addOnFailureListener(e -> {
            Log.e(TAG, "Error getting data", e);
            onDatabaseOperation.onConnectionError();
        });
    }

    private static String getPlayerKey(int playerId) {
        return String.format(KEY_FORMAT_PLAYER, playerId);
    }
}


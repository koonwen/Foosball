package com.example.foosball.database;

import android.content.Context;
import android.util.Log;

import com.example.foosball.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Database {
    public static final String TAG = "Database";

    // TODO: Figure out Firebase Database rules (currently there is no authentication)
    public static void createGame(String playerName, OnCreateGameOperation onCreateGameOperation) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String gameCode = Utils.generateGameCode();

        Log.d(TAG, "Creating game and storing in firebase realtime database");
        DatabaseReference ref = database.getReference("games").child(gameCode);
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                onCreateGameOperation.onConnectionError();
            } else {
                // Check if gameCode already exists in the database
                DataSnapshot res = Objects.requireNonNull(task.getResult());
                if (res.exists()) {
                    Log.d(TAG, "Game code " + gameCode + " already exists");
                    Object hasGameEnded = res.child("hasGameEnded").getValue();

                    // If gameCode exists, check if the game has ended
                    if ((hasGameEnded == null) || !(boolean) hasGameEnded) {
                        Log.d(TAG, "Game has not ended. Trying a new game code");
                        // If game has not ended, generate a new gameCode and restart
                        createGame(playerName, onCreateGameOperation);
                    } else {
                        Log.d(TAG, "Game has ended. Deleting and creating a new document");
                        // If game ended, then delete that document
                        // and create a new one with the same gameCode
                        ref.removeValue();
                        setUpNewGame(playerName, onCreateGameOperation, ref, gameCode);
                    }
                } else {
                    // If gameCode does no exist, then use it
                    Log.d(TAG, "Creating game with game code " + gameCode);
                    setUpNewGame(playerName, onCreateGameOperation, ref, gameCode);
                }
            }
        });
    }

    private static void setUpNewGame(String playerName, OnCreateGameOperation onCreateGameOperation,
                                     DatabaseReference ref, String gameCode) {
        ref.child("player1").setValue(playerName);
        ref.child("hasGameStarted").setValue(false);
        ref.child("hasGameEnded").setValue(false);
        onCreateGameOperation.onSuccess(gameCode);
    }

    public static void joinGame(String playerName, String gameCode,
                                OnJoinGameOperation onJoinGameOperation) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("games").child(gameCode);
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                onJoinGameOperation.onConnectionError();
            } else {
                // Checks if gamecode exists by accessing result from the task
                // Also check if game has neither started nor ended
                DataSnapshot res = Objects.requireNonNull(task.getResult());
                Object hasGameStartedObj = res.child("hasGameStarted").getValue();
                Object hasGameEndedObj = res.child("hasGameEnded").getValue();

                boolean gameExists = res.exists();
                boolean hasGameStarted = hasGameStartedObj == null || (boolean) hasGameStartedObj;
                boolean hasGameEnded = hasGameEndedObj == null || (boolean) hasGameEndedObj;

                if (gameExists && !hasGameStarted && !hasGameEnded) {
                    String[] playerKeys = {"player1", "player2", "player3", "player4"};

                    // Checks whether keys of playernames exists, if not insert current player
                    // Else if all playerkeys already exist, then lobby is already full
                    for (String playerKey : playerKeys) {
                        if (!res.child(playerKey).exists()) {
                            ref.child(playerKey).setValue(playerName);
                            onJoinGameOperation.onSuccess();
                            return;
                        }
                    }
                    Log.d(TAG, "Lobby is full. Please try again later.");
                    onJoinGameOperation.onLobbyFullError();
                } else if (gameExists && hasGameStarted && !hasGameEnded) {
                    Log.d(TAG, "Game has already started.");
                    onJoinGameOperation.onGameAlreadyStartedError();
                } else {
                    Log.d(TAG, "Game does not exist. Please check game code.");
                    onJoinGameOperation.onGameDoesNotExistError();
                }
            }
        });
    }
}


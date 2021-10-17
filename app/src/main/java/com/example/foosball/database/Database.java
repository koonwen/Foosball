package com.example.foosball.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.foosball.MainActivity;
import com.example.foosball.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    public static void updateBallCoords(String gameCode, int x, int y) {
        final DatabaseReference ref = getGameReference(gameCode);
        final ArrayList<Integer> coords = new ArrayList<>();
        coords.add(x);
        coords.add(y);
        ref.child("ballCoords").setValue(coords);
    }

    public static void getBallCoords(String gameCode,
                                     OnGetBallCoordsOperation onGetBallCoordsOperation) {
        final DatabaseReference ref = getGameReference(gameCode);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<?> coord = (ArrayList<?>) dataSnapshot.getValue();
                assert coord != null;
                final int x = ((Long) coord.get(0)).intValue();
                final int y = ((Long) coord.get(1)).intValue();
                onGetBallCoordsOperation.onSuccess(x, y);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.child("ballCoords").addValueEventListener(postListener);
    }

    private static DatabaseReference getGameReference(String gameCode) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(DATABASE_PATH).child(gameCode);
    }

    public static void startGameStatusListener(String gameCode,
                                                OnGetGameStatusOperation onGetGameStatusOperation) {
        final DatabaseReference ref = getGameReference(gameCode);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> playerNames = new ArrayList<>();
                for (int playerId : PLAYER_IDS) {
                    final String playerKey = getPlayerKey(playerId);
                    if (dataSnapshot.child(playerKey).exists()) {
                        playerNames.add((String) dataSnapshot.child(playerKey).getValue());
                    }
                }

                Boolean gameStarted = (Boolean) dataSnapshot.child("hasGameStarted").getValue();
                Boolean gameEnded = (Boolean) dataSnapshot.child("hasGameEnded").getValue();
                Boolean evenPlayers = playerNames.size() % 2 == 0;

                onGetGameStatusOperation.onSuccess(playerNames,
                        evenPlayers, gameStarted, gameEnded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
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


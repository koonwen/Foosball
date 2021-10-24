package com.example.foosball.database;

import android.util.Log;

import androidx.annotation.NonNull;

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

    /**
     * Creates a new game in the database and generates the corresponding game code.
     *
     * @param playerName Name of the player that is creating a new game.
     * @param createGameListener The listener to be called upon successful/failed game creation.
     */
    public static void createGame(String playerName, CreateGameListener createGameListener) {
        final String gameCode = Utils.generateGameCode();

        Log.d(TAG, "Creating game and storing in firebase realtime database");
        final DatabaseReference ref = getGameReference(gameCode);
        handleFailure(ref.get(), createGameListener).addOnSuccessListener(res -> {
            // Check if gameCode already exists in the database
            if (res.exists()) {
                Log.d(TAG, "Game code " + gameCode + " already exists");
                Object hasGameEnded = res.child(KEY_HAS_GAME_ENDED).getValue();

                // If gameCode exists, check if the game has ended
                if ((hasGameEnded == null) || !(boolean) hasGameEnded) {
                    Log.d(TAG, "Game has not ended. Trying a new game code");
                    // If game has not ended, generate a new gameCode and restart
                    createGame(playerName, createGameListener);
                    return;
                }
                Log.d(TAG, "Game has ended. Deleting and creating a new document");
                // If game ended, then delete that document
                // and create a new one with the same gameCode
                ref.removeValue().addOnCompleteListener(removeTask ->
                        setUpNewGame(playerName, createGameListener, ref, gameCode));
                return;
            }
            // If gameCode does no exist, then use it
            Log.d(TAG, "Creating game with game code " + gameCode);
            setUpNewGame(playerName, createGameListener, ref, gameCode);
        });
    }

    private static void setUpNewGame(String playerName, CreateGameListener createGameListener,
                                     DatabaseReference ref, String gameCode) {
        ref.child(getPlayerKey(Utils.HOST_PLAYER_ID)).setValue(playerName);
        ref.child(KEY_HAS_GAME_STARTED).setValue(false);
        ref.child(KEY_HAS_GAME_ENDED).setValue(false);
        createGameListener.onSuccess(gameCode);
    }

    public static void joinGame(String playerName, String gameCode,
                                JoinGameListener joinGameListener) {
        final DatabaseReference ref = getGameReference(gameCode);
        handleFailure(ref.get(), joinGameListener).addOnSuccessListener(res -> {
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
                joinGameListener.onGameDoesNotExistError();
                return;
            }
            if (hasGameStarted) {
                Log.d(TAG, "Game has already started.");
                joinGameListener.onGameAlreadyStartedError();
                return;
            }
            // Checks whether keys of playernames exists, if not insert current player
            // Else if all playerkeys already exist, then lobby is already full
            for (int playerId : PLAYER_IDS) {
                final String playerKey = getPlayerKey(playerId);
                if (!res.child(playerKey).exists()) {
                    ref.child(playerKey).setValue(playerName);
                    joinGameListener.onSuccess(playerId);
                    return;
                }
            }
            Log.d(TAG, "Lobby is full. Please try again later.");
            joinGameListener.onLobbyFullError();
        });
    }

    public static void updateStartGame(String gameCode,
                                       BasicDatabaseListener basicDatabaseListener) {
        final DatabaseReference ref = getGameReference(gameCode);
        handleFailure(ref.get(), basicDatabaseListener).addOnSuccessListener(res -> {
           ref.child("hasGameStarted").setValue(true);
        });
    }

    public static void removePlayer(String gameCode, int playerId,
                                    BasicDatabaseListener basicDatabaseListener) {
        final DatabaseReference ref = getGameReference(gameCode);
        handleFailure(ref.get(), basicDatabaseListener).addOnSuccessListener(res -> {
            ref.child(getPlayerKey(playerId)).removeValue();
            basicDatabaseListener.onSuccess();
        });
    }

    public static void updateBallCoords(String gameCode, int x, int y, int vx, int vy) {
        final DatabaseReference ref = getGameReference(gameCode).child("ballCoords");
        ref.child("posX").setValue(x);
        ref.child("posY").setValue(y);
        ref.child("velocityX").setValue(vx);
        ref.child("velocityY").setValue(vy);
    }

    public static void getBallCoords(String gameCode,
                                     BallCoordsListener ballCoordsListener) {
        final DatabaseReference ref = getGameReference(gameCode);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Object xObj = dataSnapshot.child("posX").getValue();
                final Object yObj = dataSnapshot.child("posY").getValue();
                final Object vxObj = dataSnapshot.child("velocityX").getValue();
                final Object vyObj = dataSnapshot.child("velocityY").getValue();
                assert xObj != null && yObj != null && vxObj != null && vyObj != null;
                final int x = ((Long) xObj).intValue();
                final int y = ((Long) yObj).intValue();
                final int vx = ((Long) vxObj).intValue();
                final int vy = ((Long) vyObj).intValue();
                ballCoordsListener.onSuccess(x, y, vx, vy);
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
                                               GameStatusListener gameStatusListener) {
        final DatabaseReference ref = getGameReference(gameCode);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> playerNames = new ArrayList<>();
                int numPlayers = 0;
                for (int playerId : PLAYER_IDS) {
                    final String playerKey = getPlayerKey(playerId);
                    if (dataSnapshot.child(playerKey).exists()) {
                        playerNames.add((String) dataSnapshot.child(playerKey).getValue());
                        numPlayers++;
                    } else {
                        playerNames.add(null);
                    }
                }

                Boolean gameStarted = (Boolean) dataSnapshot.child(KEY_HAS_GAME_STARTED).getValue();
                Boolean gameEnded = (Boolean) dataSnapshot.child(KEY_HAS_GAME_ENDED).getValue();
                Boolean evenPlayers = numPlayers % 2 == 0;

                gameStatusListener.onSuccess(playerNames,
                        evenPlayers, gameStarted, gameEnded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    private static Task<DataSnapshot> handleFailure(Task<DataSnapshot> task,
                                                    DatabaseListener databaseListener) {
        return task.addOnFailureListener(e -> {
            Log.e(TAG, "Error getting data", e);
            databaseListener.onConnectionError();
        });
    }

    private static String getPlayerKey(int playerId) {
        return String.format(KEY_FORMAT_PLAYER, playerId);
    }
}


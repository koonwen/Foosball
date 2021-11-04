package com.example.foosball.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.foosball.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents the database.
 * Follows the singleton pattern.
 */
public class Database {
    private static Database singleInstance = null;
    private static final String TAG = "Database";
    private static final ArrayList<Integer> PLAYER_IDS = new ArrayList<>(Arrays.asList(1, 2));
    private static final String DATABASE_PATH = "games";
    private static final String KEY_GAME_STATUS = "gameStatus";
    private static final String KEY_HAS_GAME_STARTED = "hasGameStarted";
    private static final String KEY_HAS_GAME_ENDED = "hasGameEnded";
    private static final String KEY_GAME_DATA = "gameData";
    private static final String KEY_BALL_X = "ballX";
    private static final String KEY_BALL_Y = "ballY";
    private static final String KEY_BALL_VX = "ballVX";
    private static final String KEY_BALL_VY = "ballVY";
    private static final String KEY_FOOSMEN_Y_TEAM_A = "fya";
    private static final String KEY_FOOSMEN_Y_TEAM_B = "fyb";
    private static final String KEY_SCORE_TEAM_A = "scoreA";
    private static final String KEY_SCORE_TEAM_B = "scoreB";
    private static final String KEY_FORMAT_PLAYER = "player%1$s";
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ValueEventListener gameStatusValueEventListener;
    private DatabaseReference ref;

    /**
     * Initialises Database singleton instance.
     */
    private Database() {
        Log.d(TAG, "Initialising Database instance");
    }

    /**
     * Returns Database singleton instance.
     *
     * @return Database singleton instance.
     */
    public static Database getInstance() {
        if (singleInstance == null) {
            singleInstance = new Database();
        }
        return singleInstance;
    }

    /**
     * Signs in anonymously to firebase.
     *
     * @param databaseListener The listener to be called upon connection error.
     * @return {@link Task} of {@link AuthResult} with the result of the operation.
     */
    private Task<AuthResult> signIn(DatabaseListener databaseListener) {
        Log.d(TAG, "Signing in anonymously to firebase");
        return handleFailure(mAuth.signInAnonymously(), databaseListener);
    }

    /**
     * Creates a new game in the database and generates the corresponding game code.
     *
     * @param playerName Name of the player that is creating a new game.
     * @param createGameListener The listener to be called upon successful/failed game creation.
     */
    public void createGame(String playerName, CreateGameListener createGameListener) {
        final String gameCode = Utils.generateGameCode();

        Log.d(TAG, "Checking game code in database: " + gameCode);
        signIn(createGameListener).addOnSuccessListener(authResult -> {
            final DatabaseReference ref = getGameReference(gameCode);
            handleFailure(ref.get(), createGameListener).addOnSuccessListener(res -> {
                // Check if gameCode already exists in the database
                if (res.exists()) {
                    Log.d(TAG, "Game code already exists: " + gameCode);
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
                    handleFailure(ref.removeValue(), createGameListener).addOnSuccessListener(
                            res2 -> setUpNewGame(playerName, createGameListener, ref, gameCode));
                    return;
                }
                // If gameCode does no exist, then use it
                setUpNewGame(playerName, createGameListener, ref, gameCode);
            });
        });
    }

    /**
     * Creates a new record on the db with the corresponding game code and player name.
     *
     * @param playerName Name of the player that is creating a new game.
     * @param createGameListener The listener to be called upon successful/failed game creation.
     * @param ref The database reference to the game path.
     * @param gameCode Game code that has been randomly generated.
     */
    private void setUpNewGame(String playerName, CreateGameListener createGameListener,
                              DatabaseReference ref, String gameCode) {
        Log.i(TAG, "Creating game with game code " + gameCode);
        final DatabaseReference refGameStatus = getRefGameStatus(ref);
        final HashMap<String, Object> updates = new HashMap<>();
        updates.put(getPlayerKey(Utils.HOST_PLAYER_ID), playerName);
        updates.put(KEY_HAS_GAME_STARTED, false);
        updates.put(KEY_HAS_GAME_ENDED, false);
        handleFailure(refGameStatus.updateChildren(updates), createGameListener)
                .addOnSuccessListener(res -> {
                    Log.d(TAG, "Successfully created game in database: " + gameCode);
                    setUpDatabaseRef(gameCode);
                    createGameListener.onSuccess(gameCode);
                });
    }

    /**
     * Stores database reference to the game path as a variable.
     *
     * @param gameCode Game code to be used.
     */
    private void setUpDatabaseRef(String gameCode) {
        this.ref = getGameReference(gameCode);
    }

    /**
     * Adds the player details an existing game record on the database.
     *
     * @param playerName Name of the current player.
     * @param gameCode Game code that matches the record on the database.
     * @param joinGameListener The listener to be called on successful/failed join operation.
     */
    public void joinGame(String playerName, String gameCode, JoinGameListener joinGameListener) {
        signIn(joinGameListener).addOnSuccessListener(authResult -> {
            final DatabaseReference ref = getGameReference(gameCode);
            final DatabaseReference refGameStatus = getRefGameStatus(ref);
            handleFailure(refGameStatus.get(), joinGameListener).addOnSuccessListener(res -> {
                // Checks if game code exists by accessing result from the task
                // Also check if game has neither started nor ended
                final Object hasGameStartedObj = res.child(KEY_HAS_GAME_STARTED).getValue();
                final Object hasGameEndedObj = res.child(KEY_HAS_GAME_ENDED).getValue();

                final boolean gameExists = res.exists();
                final boolean hasGameStarted =
                        hasGameStartedObj == null || (boolean) hasGameStartedObj;
                final boolean hasGameEnded =
                        hasGameEndedObj == null || (boolean) hasGameEndedObj;

                Log.d(TAG, "Trying to join game - gameExists: " + gameExists
                        + ", hasGameStarted: " + hasGameStarted
                        + ", hasGameEnded: " + hasGameEnded);

                if (!gameExists || hasGameEnded) {
                    joinGameListener.onGameDoesNotExistError();
                    return;
                }
                if (hasGameStarted) {
                    joinGameListener.onGameAlreadyStartedError();
                    return;
                }
                // Checks whether keys of player names exists, if not insert current player
                // Else if all player keys already exist, then lobby is already full
                for (int playerId : PLAYER_IDS) {
                    final String playerKey = getPlayerKey(playerId);
                    if (!res.child(playerKey).exists()) {
                        refGameStatus.child(playerKey).setValue(playerName);
                        setUpDatabaseRef(gameCode);
                        joinGameListener.onSuccess(playerId);
                        return;
                    }
                }
                joinGameListener.onLobbyFullError();
            });
        });
    }

    /**
     * Updates the status of the game on the db to indicate that it has started.
     *
     * @param basicDatabaseListener The listener to be called on successful/failed operation.
     */
    public void updateStartGame(BasicDatabaseListener basicDatabaseListener) {
        final DatabaseReference refGameStatus = getRefGameStatus();
        handleFailure(refGameStatus.get(), basicDatabaseListener).addOnSuccessListener(res ->
                refGameStatus.child(KEY_HAS_GAME_STARTED).setValue(true));
    }

    /**
     * Removes player name from the relevant game in the db.
     *
     * @param playerId Id of the player to be removed from the lobby.
     * @param basicDatabaseListener The listener to be called on successful/failed operation.
     */
    public void removePlayer(int playerId, BasicDatabaseListener basicDatabaseListener) {
        assert PLAYER_IDS.contains(playerId) : "Illegal playerId: " + playerId;
        final DatabaseReference refGameStatus = getRefGameStatus();
        handleFailure(refGameStatus.get(), basicDatabaseListener).addOnSuccessListener(res -> {
            refGameStatus.child(getPlayerKey(playerId)).removeValue();
            basicDatabaseListener.onSuccess();
            ref = null;
        });
    }

    /**
     * Updates coordinates and velocities of the ball and the foosmen belonging to the host.
     *
     * @param x x coordinates of the ball.
     * @param y y coordinates of the ball.
     * @param vx x velocity of the ball.
     * @param vy y-velocity of the ball.
     * @param fya y-position of the team A foosmen.
     */
    public void updateCoordsHost(int x, int y, int vx, int vy, int fya) {
        final DatabaseReference refGameData = ref.child(KEY_GAME_DATA);
        refGameData.child(KEY_BALL_X).setValue(x);
        refGameData.child(KEY_BALL_Y).setValue(y);
        refGameData.child(KEY_BALL_VX).setValue(vx);
        refGameData.child(KEY_BALL_VY).setValue(vy);
        refGameData.child(KEY_FOOSMEN_Y_TEAM_A).setValue(fya);
    }

    /**
     * Updates coordinates of the foosmen belonging to the non-host player.
     *
     * @param fyb y-position of the team B foosmen.
     */
    public void updateCoords(int fyb) {
        final DatabaseReference refGameData = ref.child(KEY_GAME_DATA);
        refGameData.child(KEY_FOOSMEN_Y_TEAM_B).setValue(fyb);
    }

    /**
     * Pulls any changes to the ball coordinates from the database.
     *
     * @param gameDataListener The listener to be called on successful/failed operation.
     */
    public void startGameDataListener(GameDataListener gameDataListener) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Object xObj = dataSnapshot.child(KEY_BALL_X).getValue();
                final Object yObj = dataSnapshot.child(KEY_BALL_Y).getValue();
                final Object vxObj = dataSnapshot.child(KEY_BALL_VX).getValue();
                final Object vyObj = dataSnapshot.child(KEY_BALL_VY).getValue();
                final Object fyaObj = dataSnapshot.child(KEY_FOOSMEN_Y_TEAM_A).getValue();
                final Object fybObj = dataSnapshot.child(KEY_FOOSMEN_Y_TEAM_B).getValue();
                if (xObj == null || yObj == null || vxObj == null || vyObj == null ||
                        fyaObj == null || fybObj == null) {
                    Log.d(TAG, "Coordinates have not been initialised");
                    return;
                }
                final int x = ((Long) xObj).intValue();
                final int y = ((Long) yObj).intValue();
                final int vx = ((Long) vxObj).intValue();
                final int vy = ((Long) vyObj).intValue();
                final int fya = ((Long) fyaObj).intValue();
                final int fyb = ((Long) fybObj).intValue();
                gameDataListener.onSuccess(x, y, vx, vy, fya, fyb);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "coordsListener:onCancelled", databaseError.toException());
            }
        };
        ref.child(KEY_GAME_DATA).addValueEventListener(postListener);
    }

    /**
     * Return a database reference to the relevant db record with a given game code
     *
     * @param gameCode Game code of the corresponding record on the db
     * @return Database Reference
     */
    private DatabaseReference getGameReference(String gameCode) {
        assert database != null;
        return database.getReference(DATABASE_PATH).child(gameCode);
    }

    /**
     * Pulls any updates to the status of the game and player names from the db.
     *
     * @param gameStatusListener @see GameStatusListener
     */
    public void startGameStatusListener(GameStatusListener gameStatusListener) {
        final DatabaseReference refGameStatus = getRefGameStatus();
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
                Boolean twoPlayers = numPlayers == 2;

                gameStatusListener.onSuccess(playerNames, twoPlayers, gameStarted, gameEnded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "gameStatusListener:onCancelled", databaseError.toException());
            }
        };
        gameStatusValueEventListener = refGameStatus.addValueEventListener(postListener);
    }

    /**
     * Stops the gameStatusListener.
     *
     * @see #startGameStatusListener(GameStatusListener)
     */
    public void stopGameStatusListener() {
        assert gameStatusValueEventListener != null;
        final DatabaseReference refGameStatus = getRefGameStatus();
        refGameStatus.removeEventListener(gameStatusValueEventListener);
    }

    /**
     * Handles database task failure by executing the {@code databaseListener.onConnectionError()}
     * callback.
     *
     * @param task Task to handle failure for.
     * @param databaseListener The listener to be called upon connection error.
     * @return Task argument that was passed in.
     */
    private <T> Task<T> handleFailure(Task<T> task, DatabaseListener databaseListener) {
        return task.addOnFailureListener(e -> {
            Log.e(TAG, "Error connecting to database", e);
            databaseListener.onConnectionError();
        });
    }

    /**
     * Returns formatted player key string depending on player id.
     *
     * @param playerId Id of player.
     * @return Formatted player key string.
     */
    private String getPlayerKey(int playerId) {
        assert PLAYER_IDS.contains(playerId) : "Illegal playerId: " + playerId;
        return String.format(KEY_FORMAT_PLAYER, playerId);
    }

    /**
     * Returns the node on the db that contains the game status details
     *
     * @return Database reference to game status node.
     */
    private DatabaseReference getRefGameStatus() {
        assert ref != null;
        return ref.child(KEY_GAME_STATUS);
    }

    /**
     * Returns the node on the db that contains the game status details
     *
     * @param ref Database reference root
     * @return Database reference to game status node
     */
    private DatabaseReference getRefGameStatus(DatabaseReference ref) {
        return ref.child(KEY_GAME_STATUS);
    }
}


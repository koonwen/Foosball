package com.example.foosball.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.foosball.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    private static Database singleInstance = null;
    private static final String TAG = "Database";
    private static final int[] PLAYER_IDS = {1, 2, 3, 4};
    private static final String DATABASE_PATH = "games";
    private static final String KEY_HAS_GAME_STARTED = "hasGameStarted";
    private static final String KEY_HAS_GAME_ENDED = "hasGameEnded";
    private static final String KEY_FORMAT_PLAYER = "player%1$s";
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ValueEventListener gameStatusValueEventListener;
    private DatabaseReference ref;

    private Database() {
    }

    public static Database getInstance() {
        if (singleInstance == null) {
            singleInstance = new Database();
        }
        return singleInstance;
    }

    private Task<AuthResult> signIn(DatabaseListener databaseListener) {
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

        Log.d(TAG, "Creating game and storing in firebase realtime database");
        signIn(createGameListener).addOnSuccessListener(authResult -> {
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
                    handleFailure(ref.removeValue(), createGameListener).addOnSuccessListener(
                            res2 -> setUpNewGame(playerName, createGameListener, ref, gameCode));
                    return;
                }
                // If gameCode does no exist, then use it
                Log.d(TAG, "Creating game with game code " + gameCode);
                setUpNewGame(playerName, createGameListener, ref, gameCode);
            });
        });
    }

    /**
     * Creates a new record on the db with the corresponding game code and player name
     *
     * @param playerName Name of the player that is creating a new game
     * @param createGameListener @see CreateGameListener
     * @param ref The database reference
     * @param gameCode Game code that has been randomly generated
     */

    private void setUpNewGame(String playerName, CreateGameListener createGameListener,
                              DatabaseReference ref, String gameCode) {
        final DatabaseReference refGameStatus = getRefGameStatus(ref);
        final HashMap<String, Object> updates = new HashMap<>();
        updates.put(getPlayerKey(Utils.HOST_PLAYER_ID), playerName);
        updates.put(KEY_HAS_GAME_STARTED, false);
        updates.put(KEY_HAS_GAME_ENDED, false);
        handleFailure(refGameStatus.updateChildren(updates), createGameListener)
                .addOnSuccessListener(res -> {
                    setUpDatabaseRef(gameCode);
                    createGameListener.onSuccess(gameCode);
                });
    }

    private void setUpDatabaseRef(String gameCode) {
        this.ref = getGameReference(gameCode);
    }

    /**
     * Adds the player details an existing game record on the database
     *
     * @param playerName Name of the current player
     * @param gameCode Game code that matches the record on the database
     * @param joinGameListener @see JoinGameListener
     */

    public void joinGame(String playerName, String gameCode,
                         JoinGameListener joinGameListener) {
        signIn(joinGameListener).addOnSuccessListener(authResult -> {
            final DatabaseReference ref = getGameReference(gameCode);
            final DatabaseReference refGameStatus = getRefGameStatus(ref);
            handleFailure(refGameStatus.get(), joinGameListener).addOnSuccessListener(res -> {
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
                        refGameStatus.child(playerKey).setValue(playerName);
                        setUpDatabaseRef(gameCode);
                        joinGameListener.onSuccess(playerId);
                        return;
                    }
                }
                Log.d(TAG, "Lobby is full. Please try again later.");
                joinGameListener.onLobbyFullError();
            });
        });
    }

    /**
     * Updates the status of the game on the db to indicate that it has started
     * and initialises the position and velocity of the ball to 0
     *
     * @param basicDatabaseListener @see BasicDatabaseListener
     */

    public void updateStartGame(BasicDatabaseListener basicDatabaseListener) {
        final DatabaseReference refGameStatus = getRefGameStatus();
        handleFailure(refGameStatus.get(), basicDatabaseListener).addOnSuccessListener(res -> {
            refGameStatus.child("hasGameStarted").setValue(true);
            final DatabaseReference refBallCoords = ref.child("ballCoords");
            refBallCoords.child("posX").setValue(0);
            refBallCoords.child("posY").setValue(0);
            refBallCoords.child("velocityX").setValue(0);
            refBallCoords.child("velocityY").setValue(0);
            refBallCoords.child("fya").setValue(0);
            refBallCoords.child("fyb").setValue(0);
        });
    }

    /**
     * Removes player name from the relevant game in the db
     *
     * @param playerId Whether the player to be removed is the 1st, 2nd, 3rd or 4th player in the lobby
     * @param basicDatabaseListener @see BasicDatabaseListener
     */
    public void removePlayer(int playerId, BasicDatabaseListener basicDatabaseListener) {
        final DatabaseReference refGameStatus = getRefGameStatus();
        handleFailure(refGameStatus.get(), basicDatabaseListener).addOnSuccessListener(res -> {
            refGameStatus.child(getPlayerKey(playerId)).removeValue();
            basicDatabaseListener.onSuccess();
            ref = null;
        });
    }

    /**
     * Updates the ball coordinates in the db
     *
     * @param x x coordinates
     * @param y y coordinates
     * @param vx x velocity
     * @param vy y velocity
     */

    public void updateCoordsHost(int x, int y, int vx, int vy, int fya) {
        final DatabaseReference refBallCoords = ref.child("ballCoords");
        refBallCoords.child("posX").setValue(x);
        refBallCoords.child("posY").setValue(y);
        refBallCoords.child("velocityX").setValue(vx);
        refBallCoords.child("velocityY").setValue(vy);
        refBallCoords.child("fya").setValue(fya);
    }

    public void updateCoords(int fyb) {
        final DatabaseReference refBallCoords = ref.child("ballCoords");
        refBallCoords.child("fyb").setValue(fyb);
    }


    /**
     * Pulls any changes to the ball coordinates from the database
     *
     * @param coordsListener @see BallCoordsListener
     */

    public void getCoords(CoordsListener coordsListener) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Object xObj = dataSnapshot.child("posX").getValue();
                final Object yObj = dataSnapshot.child("posY").getValue();
                final Object vxObj = dataSnapshot.child("velocityX").getValue();
                final Object vyObj = dataSnapshot.child("velocityY").getValue();
                final Object fyaObj = dataSnapshot.child("fya").getValue();
                final Object fybObj = dataSnapshot.child("fyb").getValue();
                assert xObj != null && yObj != null && vxObj != null && vyObj != null &&
                        fyaObj != null && fybObj != null;
                final int x = ((Long) xObj).intValue();
                final int y = ((Long) yObj).intValue();
                final int vx = ((Long) vxObj).intValue();
                final int vy = ((Long) vyObj).intValue();
                final int fya = ((Long) fyaObj).intValue();
                final int fyb = ((Long) fybObj).intValue();
                coordsListener.onSuccess(x, y, vx, vy, fya, fyb);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.child("ballCoords").addValueEventListener(postListener);
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
     * Pulls any updates to the status of the game and player names from the db
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
                Boolean evenPlayers = numPlayers % 2 == 0;

                gameStatusListener.onSuccess(playerNames,
                        evenPlayers, gameStarted, gameEnded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        gameStatusValueEventListener = refGameStatus.addValueEventListener(postListener);
    }

    /**
     * Stops @startGameStatusListener once the game has started
     */
    public void stopGameStatusListener() {
        assert gameStatusValueEventListener != null;
        final DatabaseReference refGameStatus = getRefGameStatus();
        refGameStatus.removeEventListener(gameStatusValueEventListener);
    }

    /**
     * Prints an error message whenever other methods that accesses the db fails to connect
     *
     * @param task DataSnapShot
     * @param databaseListener @see DatabaseListener
     * @return Error message
     */
    private <T> Task<T> handleFailure(Task<T> task, DatabaseListener databaseListener) {
        return task.addOnFailureListener(e -> {
            Log.e(TAG, "Error connecting to database", e);
            databaseListener.onConnectionError();
        });
    }

    /**
     * Returns a formatted string of "player1", "player2", "player3", or "player4" depending on ID
     *
     * @param playerId Any int between 1 - 4
     * @return Formatted string with player key
     */
    private String getPlayerKey(int playerId) {
        return String.format(KEY_FORMAT_PLAYER, playerId);
    }

    /**
     * Returns the node on the db that contains the game status details
     *
     * @return Database reference to game status node
     */
    private DatabaseReference getRefGameStatus() {
        assert ref != null;
        return ref.child("gameStatus");
    }

    /**
     * Returns the node on the db that contains the game status details
     *
     * @param ref Database reference root
     * @return Database reference to game status node
     */
    private DatabaseReference getRefGameStatus(DatabaseReference ref) {
        return ref.child("gameStatus");
    }
}


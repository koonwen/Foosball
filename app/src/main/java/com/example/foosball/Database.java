package com.example.foosball;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Database {
    public static final String TAG = "Database";

    // TODO: Generate our own shortened game code and use that to store as the document id
    // TODO: Figure out Firestore rules (currently there is no authentication)
    // TODO: Figure out how everyone else should handle Firebase credentials
    public static void createGame(String playerName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String gameCode = Utils.generateGameCode();
        // Check if gameCode already exists in the database
        // If gameCode does no exist, then use it
        // If gameCode exists, check if the game has ended
        // If game ended, then delete that document, and create a new one with the same gameCode
        // If game has not ended, then don't delete that document. Generate a new gameCode and loop.

        Log.d(TAG, "Creating game and storing in firebase realtime database");
        DatabaseReference ref = database.getReference("games").child(gameCode);
        ref.child("player1").setValue(playerName);
        ref.child("player2").setValue("");
        ref.child("player3").setValue("");
        ref.child("player4").setValue("");
    }



    public static void joinGame(String playerName, String gameCode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("games").child(gameCode);
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                // Checks if gamecode exists by accessing result from the task
                DataSnapshot res = Objects.requireNonNull(task.getResult());
                if (res.exists()) {
                    String[] playerKeys = {"player1", "player2", "player3", "player4"};

                    // Checks whether keys of playernames exists, if not insert current player
                    // Else if all playerkeys already exist, then lobby is already full
                    for (String playerKey : playerKeys) {
                        if (!res.child(playerKey).exists()) {
                            ref.child(playerKey).setValue(playerName);
                            return;
                        }
                    }
                    Log.e(TAG, "Lobby is full", task.getException());
                } else {
                    Log.e(TAG, "Game does not exist", task.getException());
                }
            }
        });
    }
}

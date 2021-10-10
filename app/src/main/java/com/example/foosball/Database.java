package com.example.foosball;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Database {
    public static final String TAG = "Database";

    // TODO: Generate our own shortened game code and use that to store as the document id
    // TODO: Figure out Firestore rules (currently there is no authentication)
    // TODO: Figure out how everyone else should handle Firebase credentials
    public static void createGame(String playerName, OnDatabaseOperation onDatabaseOperation) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String gameCode = Utils.generateGameCode();
        // Check if gameCode already exists in the database
        // If gameCode does no exist, then use it
        // If gameCode exists, check if the game has ended
        // If game ended, then delete that document, and create a new one with the same gameCode
        // If game has not ended, then don't delete that document. Generate a new gameCode and loop.

        Log.d(TAG, "Creating game and storing in firebase realtime database");
        DatabaseReference ref = database.getReference("games").child(gameCode);
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                onDatabaseOperation.onError();
            } else {
                if (Objects.requireNonNull(task.getResult()).exists()) {
                    Log.d(TAG, "Game code already exists");
                    createGame(playerName, onDatabaseOperation);
                } else {
                    Log.d(TAG, "game code does not exist");
                    ref.child("player1").setValue(playerName);
                    onDatabaseOperation.onSuccess();
                }
            }
        });
    }
}


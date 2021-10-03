package com.example.foosball;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Database {
    public static final String TAG = "Database";

    // TODO: Generate our own shortened game code and use that to store as the document id
    // TODO: Figure out Firestore rules (currently there is no authentication)
    // TODO: Figure out how everyone else should handle Firebase credentials
    public static void createGame(String playerName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> game = new HashMap<>();
        game.put("player1", playerName);
        Task<DocumentReference> task = db.collection("games")
                .add(game)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}

package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FullScreenActivity {

    private Button createGame;
    private Button joinGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createGame = findViewById(R.id.createGame);
        joinGame = findViewById(R.id.joinGame);
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                startActivity(intent);
            }
        });
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                startActivity(intent);
            }
        });
    }
}

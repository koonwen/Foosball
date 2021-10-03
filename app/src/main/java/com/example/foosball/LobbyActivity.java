package com.example.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LobbyActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        final String playerName = Utils.getPlayerName(this);
        TextView player1Text = findViewById(R.id.player1Text);
        player1Text.setText(playerName);

        final String gameCode = Utils.getGameCode(this);
        TextView gameCodeText = findViewById(R.id.codeID);
        gameCodeText.setText(gameCode);


        Button returnMenu = findViewById(R.id.returnMenu);
        returnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

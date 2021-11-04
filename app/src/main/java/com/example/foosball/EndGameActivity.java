package com.example.foosball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Bundle extras = getIntent().getExtras();
        int teamAConceeeded = 0, teamBConceeded = 0;
        String winner = "Winner is ";
        if (extras != null) {
            teamAConceeeded = extras.getInt("teamA");
            teamBConceeded = extras.getInt("teamB");
            if (teamAConceeeded > teamBConceeded) {
                winner += "Team B!!!";
            } else {
                winner += "Team A!!!";
            }
        }
        winner += "\nScore " + teamAConceeeded +  " - " + teamBConceeded;

        TextView score = (TextView) findViewById(R.id.scoreTextView);
        score.setText(winner);

        final Button returnMenuButton = findViewById(R.id.returnMenuButton);


        returnMenuButton.setOnClickListener(view -> {
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }
}
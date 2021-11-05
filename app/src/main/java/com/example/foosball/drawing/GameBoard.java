package com.example.foosball.drawing;
import com.example.foosball.R;
import com.example.foosball.models.Background;
import com.example.foosball.models.Ball;
import com.example.foosball.models.Foosman;
import com.example.foosball.models.FoosmenTeam;
import com.example.foosball.models.Goal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is a Custom View Component. More information can be found here:
 * https://developer.android.com/guide/topics/ui/custom-components
 *
 * It is extended from `res/layout/main.xml`.
 *
 * It renders the background (stars field), ball and foosmen objects.
 */

public class GameBoard extends View {

    // Create board items
    private Bitmap bmTeamA = BitmapFactory.decodeResource(getResources(), R.drawable.ship1);
    private Bitmap bmTeamB = BitmapFactory.decodeResource(getResources(), R.drawable.ship2);
    private Bitmap bmBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
    public FoosmenTeam teamA = new FoosmenTeam(getHeight()/2);
    public FoosmenTeam teamB = new FoosmenTeam(getHeight()/2);
    public Goal goalA = new Goal("Team A Goal");
    public Goal goalB = new Goal("Team B Goal");
    public Background bg = new Background();
    public Ball b = new Ball(bmBall);
    private List<String> foosmanNames = Arrays.asList("TeamAGoalie", "TeamADefender1",
            "TeamADefender2", "TeamAAttacker1", "TeamAAttacker2", "TeamAAttacker3", "TeamBGoalie",
            "TeamBDefender1", "TeamBDefender2", "TeamBAttacker1", "TeamBAttacker2", "TeamBAttacker3");
    private final Map<String, Foosman> foosmanMap = new TreeMap<String, Foosman>();
    synchronized public Foosman getFoosman(String name) {
        return foosmanMap.get(name);
    }

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);

        // Generate Foosman
        for (String name : foosmanNames) {
            if (name.startsWith("TeamA")) {
                Foosman fman = new Foosman(name, bmTeamA);
                teamA.addPlayer(fman);
                foosmanMap.put(name, fman);
            } else {
                Foosman fman = new Foosman(name, bmTeamB);
                teamB.addPlayer(fman);
                foosmanMap.put(name, fman);
            }
        }
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {

        // Draw Background
        bg.refresh(canvas, getWidth(), getHeight());

        // Draw Foosmen
        for (Map.Entry<String, Foosman> entry : foosmanMap.entrySet()) {
            Foosman foosman = entry.getValue();
            foosman.refresh(canvas);
        }
        // Draw Ball
        b.refresh(canvas);

        // Draw Goal
        goalA.refresh(canvas);
        goalB.refresh(canvas);


        /**
         * This draws points of collisions detected between the ball and the foosmen.
         * It is for debugging purposes and should be removed in production.
         */
//        b.setCollisionDetected(checkForCollision());
//        if (collisionDetected ) {
//            p.setColor(Color.RED);
//            p.setAlpha(255);
//            p.setStrokeWidth(5);
//            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5, lastCollision.x + 5, lastCollision.y + 5, p);
//            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5, lastCollision.x - 5, lastCollision.y + 5, p);
//        }
    }
}

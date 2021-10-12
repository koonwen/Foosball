package com.example.foosball;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.foosball.drawing.GameBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class GameActivity extends FullScreenActivity implements OnClickListener {

    private Handler frame = new Handler();
    private Point sprite1Velocity;
    private Point sprite2Velocity;
    private Point ballVelocity;
    private int sprite1MaxX;
    private int sprite2MaxY;
    private int sprite1MaxY;
    private int sprite2MaxX;
    private int ballMaxY;
    private int ballMaxX;
    //acceleration flag
    private boolean isAccelerating = false;
    private boolean hasCollided = false;
    private static final int FRAME_RATE = 20; //50 frames per second

    //Method for getting touch state--requires android 2.1 or greater
    @Override
    synchronized public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                isAccelerating = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isAccelerating = false;
                break;
        }
        return true;
    }

    //Increase the velocity towards five or decrease
    //back to one depending on state
    private void updateVelocity() {
        int xDir = (ballVelocity.x > 0) ? 1 : -1;
        int yDir = (ballVelocity.y > 0) ? 1 : -1;
        int speed = 0;
        if (ballVelocity.x > 7) {
            speed = Math.abs(ballVelocity.x)+1;
        } else {
            speed = Math.abs(ballVelocity.x);
        }
        ballVelocity.x=speed*xDir;
        ballVelocity.y=speed*yDir;
//        int speed = 0;
//        if (isAccelerating) {
//            speed = Math.abs(ballVelocity.x)+1;
//        } else {
//            speed = Math.abs(ballVelocity.x)-1;
//        }
//        if (speed>8) speed =8;
//        if (speed<1) speed =1;
//        ballVelocity.x=speed*xDir;
//        ballVelocity.y=speed*yDir;
    }

    private void checkCollision() {
        if (((GameBoard)findViewById(R.id.the_canvas)).wasCollisionDetected()) {
            ballVelocity.x *= 1.1;
            ballVelocity.x *= -1;
            hasCollided = false;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
    }

    private Point getRandomVelocity() {
        Random r = new Random();
        int min = 1;
        int max = 5;
        int x = r.nextInt(max-min+1)+min;
        int y = r.nextInt(max-min+1)+min;
        return new Point (x,y);
    }

    synchronized public void initGfx() {
        ((GameBoard)findViewById(R.id.the_canvas)).resetStarField();
        Point pBall, pTeamAGoalie, pTeamADefender1, pTeamADefender2, pTeamAAttacker1, pTeamAAttacker2, pTeamAAttacker3,
                pTeamBGoalie, pTeamBDefender1, pTeamBDefender2, pTeamBAttacker1, pTeamBAttacker2, pTeamBAttacker3;

        List<String> foosmanNames = Arrays.asList("TeamAGoalie", "TeamADefender1",
                "TeamADefender2", "TeamAAttacker1", "TeamAAttacker2", "TeamAAttacker3", "TeamBGoalie",
                "TeamBDefender1", "TeamBDefender2", "TeamBAttacker1", "TeamBAttacker2", "TeamBAttacker3");

        // Generate Positions
        int canvasWidth = findViewById(R.id.the_canvas).getWidth();
        int canvasHeight = findViewById(R.id.the_canvas).getHeight();

        pTeamAGoalie = new Point ((int) (canvasWidth * 0.05), (int) (canvasHeight * 0.5));
        pTeamADefender1 = new Point ((int) (canvasWidth * 0.20), (int) (canvasHeight * 0.625));
        pTeamADefender2 = new Point ((int) (canvasWidth * 0.20), (int) (canvasHeight * 0.375));
        pTeamAAttacker1 = new Point ((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.75));
        pTeamAAttacker2 = new Point ((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.50));
        pTeamAAttacker3 = new Point ((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.25));
        pTeamBGoalie = new Point ((int) (canvasWidth * 0.90), (int) (canvasHeight * 0.50));
        pTeamBDefender1 = new Point ((int) (canvasWidth * 0.80), (int) (canvasHeight * 0.625));
        pTeamBDefender2 = new Point ((int) (canvasWidth * 0.80), (int) (canvasHeight * 0.375));
        pTeamBAttacker1 = new Point ((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.75));
        pTeamBAttacker2 = new Point ((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.50));
        pTeamBAttacker3 = new Point ((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.25));
        pBall = new Point((int) (canvasWidth * 0.50), (int) (canvasHeight * 0.50));

        List<Point> foosmanPoints = Arrays.asList(pTeamAGoalie, pTeamADefender1, pTeamADefender2, pTeamAAttacker1, pTeamAAttacker2, pTeamAAttacker3,
                pTeamBGoalie, pTeamBDefender1, pTeamBDefender2, pTeamBAttacker1, pTeamBAttacker2, pTeamBAttacker3);

        // Set Positions
        for (int i = 0; i < foosmanNames.size(); i++) {
            ((GameBoard)findViewById(R.id.the_canvas)).getFoosman(foosmanNames.get(i)).setPoint(foosmanPoints.get(i).x - 50, foosmanPoints.get(i).y - 50);
        }
        ((GameBoard)findViewById(R.id.the_canvas)).setBall(pBall.x, pBall.y);

//        sprite1Velocity = getRandomVelocity();
//        sprite2Velocity = new Point(1,1);
        ballVelocity = new Point(5,5);

//        sprite1MaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width();
//        sprite1MaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Height();
//        sprite2MaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Width();
//        sprite2MaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Height();
        ballMaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getBallWidth();
        ballMaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getBallHeight();
        frame.removeCallbacks(frameUpdate);
        ((GameBoard)findViewById(R.id.the_canvas)).invalidate(); // marks the canvas as outdated - so it will be updated on next frame
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    @Override
    synchronized public void onClick(View v) {
        initGfx();
    }

    private Runnable frameUpdate = new Runnable() {

        @Override
        synchronized public void run() {
//            if (((GameBoard)findViewById(R.id.the_canvas)).wasCollisionDetected()) {
//                hasCollided = true;
//                updateVelocity();
////                Point collisionPoint = ((GameBoard)findViewById(R.id.the_canvas)).getLastCollision();
//            }
            frame.removeCallbacks(frameUpdate);

            checkCollision();
//
//            //Add our call to increase or decrease velocity
            updateVelocity();

//            Point sprite1 = new Point (((GameBoard)findViewById(R.id.the_canvas)).getSprite1X(),
//                    ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Y()) ;
//            Point sprite2 = new Point (((GameBoard)findViewById(R.id.the_canvas)).getSprite2X(),
//                    ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Y());
            Point ball = new Point (((GameBoard)findViewById(R.id.the_canvas)).getBallX(),
                    ((GameBoard)findViewById(R.id.the_canvas)).getBallY());
//
//            // Check if points exceed the canvas
//            sprite1.x = sprite1.x + sprite1Velocity.x;
//            if (sprite1.x > sprite1MaxX || sprite1.x < 5) {
//                sprite1Velocity.x *= -1;
//            }
//            sprite1.y = sprite1.y + sprite1Velocity.y;
//            if (sprite1.y > sprite1MaxY || sprite1.y < 5) {
//                sprite1Velocity.y *= -1;
//            }
//            sprite2.x = sprite2.x + sprite2Velocity.x;
//            if (sprite2.x > sprite2MaxX || sprite2.x < 5) {
//                sprite2Velocity.x *= -1;
//            }
//            sprite2.y = sprite2.y + sprite2Velocity.y;
//            if (sprite2.y > sprite2MaxY || sprite2.y < 5) {
//                sprite2Velocity.y *= -1;
//            }
            ball.x = ball.x + ballVelocity.x;
            if (ball.x > ballMaxX || ball.x < 5) {
                ballVelocity.x *= -1;
            }
            ball.y = ball.y + ballVelocity.y;
            if (ball.y > ballMaxY || ball.y < 5) {
                ballVelocity.y *= -1;
            }

//            ((GameBoard)findViewById(R.id.the_canvas)).TeamAGoalie.setPoint(TeamAGoalie.x, TeamAGoalie.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamADefender1.setPoint(pTeamADefender1.x, pTeamADefender1.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamADefender2.setPoint(pTeamADefender2.x, pTeamADefender2.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamAAttacker1.setPoint(pTeamAAttacker1.x, pTeamAAttacker1.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamAAttacker2.setPoint(pTeamAAttacker2.x, pTeamAAttacker2.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamAAttacker3.setPoint(pTeamAAttacker3.x, pTeamAAttacker3.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamBGoalie.setPoint(pTeamBGoalie.x, pTeamBGoalie.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamBDefender1.setPoint(pTeamBDefender1.x, pTeamBDefender1.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamBDefender2.setPoint(pTeamBDefender2.x, pTeamBDefender2.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamBAttacker1.setPoint(pTeamBAttacker1.x, pTeamBAttacker1.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamBAttacker2.setPoint(pTeamBAttacker2.x, pTeamBAttacker2.y);
//            ((GameBoard)findViewById(R.id.the_canvas)).TeamBAttacker3.setPoint(pTeamBAttacker3.x, pTeamBAttacker3.y);
            ((GameBoard)findViewById(R.id.the_canvas)).setBall(ball.x, ball.y);
            ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }

    };
}

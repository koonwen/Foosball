package com.example.foosball;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import com.example.foosball.database.BallCoordsListener;
import com.example.foosball.database.Database;
import com.example.foosball.drawing.GameBoard;
import com.example.foosball.models.Background;
import com.example.foosball.models.Ball;
import com.example.foosball.models.Foosman;
import com.example.foosball.models.FoosmenTeam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This acts as an Game Engine. It initiates the positions of all elements including the ball
 * and foosmen and also handles the movement of the elements for every frame.
 */

public class GameActivity extends FullScreenActivity implements OnTouchListener {

    private final Handler frame = new Handler();
    private Point ballVelocity;
    private int ballMaxY;
    private int ballMaxX;

    // acceleration flag
    private final boolean isAccelerating = false;

    private boolean hasCollided = false;
    private boolean collisionFromTopOrBottom = false;
    private boolean upButtonDown = false;
    private boolean downButtonDown = false;
    private static final int FPS = 50;
    private static final int FRAME_RATE = 1000 / FPS;
    private String gameCode;
    private boolean isGameHost;
    private final List<String> foosmanNames = Arrays.asList("TeamAGoalie", "TeamADefender1",
            "TeamADefender2", "TeamAAttacker1", "TeamAAttacker2", "TeamAAttacker3", "TeamBGoalie",
            "TeamBDefender1", "TeamBDefender2", "TeamBAttacker1", "TeamBAttacker2", "TeamBAttacker3");
    private final List<String> foosmanNamesTeamA = Arrays.asList("TeamAGoalie", "TeamADefender1",
            "TeamADefender2", "TeamAAttacker1", "TeamAAttacker2", "TeamAAttacker3");
    private final List<Foosman> foosmanList = new ArrayList<Foosman>();

    //Method for getting touch state--requires android 2.1 or greater
    //    @Override
    //    synchronized public boolean onTouchEvent(MotionEvent ev) {
    //        final int action = ev.getAction();
    //        switch (action & MotionEvent.ACTION_MASK) {
    //            case MotionEvent.ACTION_DOWN:
    //            case MotionEvent.ACTION_POINTER_DOWN:
    //                isAccelerating = true;
    //                break;
    //            case MotionEvent.ACTION_UP:
    //            case MotionEvent.ACTION_POINTER_UP:
    //                isAccelerating = false;
    //                break;
    //        }
    //        return true;
    //    }

    /**
     * Increase the velocity towards seven or
     * and hold steady afterwards
     */

    private void updateVelocity() {
        int xDir = (ballVelocity.x > 0) ? 1 : -1;
        int yDir = (ballVelocity.y > 0) ? 1 : -1;
        int speed = 0;
        if (ballVelocity.x > 7) {
            speed = Math.abs(ballVelocity.x) + 1;
        } else {
            speed = Math.abs(ballVelocity.x);
        }
        ballVelocity.x = speed * xDir;
        ballVelocity.y = speed * yDir;
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


    private boolean checkCollision(Ball b, List<Foosman> foosmanList) {

        Bitmap bmTeamA = BitmapFactory.decodeResource(getResources(), R.drawable.ship1);
        Bitmap bmTeamB = BitmapFactory.decodeResource(getResources(), R.drawable.ship2);
        Bitmap bmBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        int bmWidth = bmTeamA.getWidth();
        int bmHeight = bmTeamA.getHeight();

        List<Rect> foosmanboundsList = new ArrayList<Rect>();

        for (Foosman foosman : foosmanList) {
            int foosmanPointX = foosman.getPointX();
            int foosmanPointY = foosman.getPointY();
            Rect r = new Rect(foosmanPointX, foosmanPointY, foosmanPointX + bmWidth, foosmanPointY + bmHeight);
            foosmanboundsList.add(r);
        }

        Rect rball = new Rect(b.getPointX(), b.getPointY(), b.getPointX() + b.getWidth(), b.getPointY() + b.getHeight());

        for (Rect r : foosmanboundsList) {
            if (Math.abs(rball.left - r.left) < 25 || Math.abs(rball.right - r.right) < 25) {
                collisionFromTopOrBottom = true;
            } else {
                collisionFromTopOrBottom = false;
            }
            Rect r3 = rball;

            if (rball.intersect(r)) {
                for (int i = rball.left; i < rball.right; i++) {
                    for (int j = rball.top; j < rball.bottom; j++) {
                        if (bmBall.getPixel(i - r3.left, j - r3.top) != Color.TRANSPARENT) {
                            if (bmTeamA.getPixel(i - r.left, j - r.top) != Color.TRANSPARENT || bmTeamB.getPixel(i - r.left, j - r.top) != Color.TRANSPARENT) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void handleCollision() {
        if (hasCollided && collisionFromTopOrBottom) {
            ballVelocity.x *= 1.1;
            ballVelocity.y *= 1.1;
            ballVelocity.y *= -1;
            collisionFromTopOrBottom = false;
            hasCollided = false;
        } else if (hasCollided) {
            ballVelocity.x *= 1.1;
            ballVelocity.y *= 1.1;
            ballVelocity.x *= -1;
            hasCollided = false;
        }
    }

    /**
     * This method is called when the user presses the 'Up' or 'Down' buttons on screen.
     * It checks if `upButtonDown` or `downButtonDown` are true and then checks if the
     * foosmen closest to the canvas top and bottom edges are within a distance of 50.
     * If false, it specifies a new position for each foosmen by 5 in the Y-direction.
     */
    private void moveFoosman() {
        int canvasHeight = findViewById(R.id.the_canvas).getHeight();

        if (downButtonDown) {
            if (((GameBoard) findViewById(R.id.the_canvas)).getFoosman("TeamAAttacker1").getPointY() < (canvasHeight - 150)) {
                ((GameBoard) findViewById(R.id.the_canvas)).teamA.movePlayers(5);
            }
        }
        if (upButtonDown) {
            if (((GameBoard) findViewById(R.id.the_canvas)).getFoosman("TeamAAttacker3").getPointY() > 50) {
                ((GameBoard) findViewById(R.id.the_canvas)).teamA.movePlayers(-5);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Handler h = new Handler();
        ((ImageButton) findViewById(R.id.up_button)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.down_button)).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downButtonDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        downButtonDown = false;
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
        gameCode = Utils.getGameCode(getApplicationContext());
        isGameHost = Utils.isGameHost(getApplicationContext());
        if (!isGameHost) {
            Database.getBallCoords(gameCode, new BallCoordsListener() {
                @Override
                public void onSuccess(int x, int y, int vx, int vy) {
                    if (ballVelocity != null) {
                        ballVelocity.x = vx;
                        ballVelocity.y = vy;
                    }
                    ((GameBoard) findViewById(R.id.the_canvas)).b.setPoint(x, y);
                }

                @Override
                public void onConnectionError() {

                }
            });
        }
    }

    private Point getRandomVelocity() {
        Random r = new Random();
        int min = 3;
        int max = 6;
        int x = r.nextInt(max - min + 1) + min;
        int y = r.nextInt(max - min + 1) + min;
        return new Point(x, y);
    }

    synchronized public void initGfx() {
        ((GameBoard) findViewById(R.id.the_canvas)).bg.resetStarField();
        Point pBall, pTeamAGoalie, pTeamADefender1, pTeamADefender2, pTeamAAttacker1, pTeamAAttacker2, pTeamAAttacker3,
                pTeamBGoalie, pTeamBDefender1, pTeamBDefender2, pTeamBAttacker1, pTeamBAttacker2, pTeamBAttacker3;

        // Generate Positions
        int canvasWidth = findViewById(R.id.the_canvas).getWidth();
        int canvasHeight = findViewById(R.id.the_canvas).getHeight();

        // Retrieve Foosmen
        for (String name : foosmanNames) {
            foosmanList.add(((GameBoard) findViewById(R.id.the_canvas)).getFoosman(name));
        }

        pTeamAGoalie = new Point((int) (canvasWidth * 0.05), (int) (canvasHeight * 0.5));
        pTeamADefender1 = new Point((int) (canvasWidth * 0.20), (int) (canvasHeight * 0.625));
        pTeamADefender2 = new Point((int) (canvasWidth * 0.20), (int) (canvasHeight * 0.375));
        pTeamAAttacker1 = new Point((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.75));
        pTeamAAttacker2 = new Point((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.50));
        pTeamAAttacker3 = new Point((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.25));
        pTeamBGoalie = new Point((int) (canvasWidth * 0.90), (int) (canvasHeight * 0.50));
        pTeamBDefender1 = new Point((int) (canvasWidth * 0.80), (int) (canvasHeight * 0.625));
        pTeamBDefender2 = new Point((int) (canvasWidth * 0.80), (int) (canvasHeight * 0.375));
        pTeamBAttacker1 = new Point((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.75));
        pTeamBAttacker2 = new Point((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.50));
        pTeamBAttacker3 = new Point((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.25));
        pBall = new Point((int) (canvasWidth * 0.25), (int) (canvasHeight * 0.75));

        List<Point> foosmanPoints = Arrays.asList(pTeamAGoalie, pTeamADefender1, pTeamADefender2, pTeamAAttacker1, pTeamAAttacker2, pTeamAAttacker3,
                pTeamBGoalie, pTeamBDefender1, pTeamBDefender2, pTeamBAttacker1, pTeamBAttacker2, pTeamBAttacker3);

        // Set Positions
        for (int i = 0; i < foosmanNames.size(); i++) {
            ((GameBoard) findViewById(R.id.the_canvas)).getFoosman(foosmanNames.get(i)).setPoint(foosmanPoints.get(i).x - 50, foosmanPoints.get(i).y - 50);
        }
        // Set Relative positions for the team
        ((GameBoard) findViewById(R.id.the_canvas)).teamA.fixRelativePos();
        ((GameBoard) findViewById(R.id.the_canvas)).teamB.fixRelativePos();


        ((GameBoard) findViewById(R.id.the_canvas)).b.setPoint(pBall.x, pBall.y);


        ballVelocity = new Point(6, -2);

        ballMaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard) findViewById(R.id.the_canvas)).b.getWidth();
        ballMaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard) findViewById(R.id.the_canvas)).b.getHeight();
        ((ImageButton) findViewById(R.id.up_button)).setEnabled(true);
        ((ImageButton) findViewById(R.id.down_button)).setEnabled(true);
        frame.removeCallbacks(frameUpdate);
        ((GameBoard) findViewById(R.id.the_canvas)).invalidate(); // marks the canvas as outdated - so it will be updated on next frame
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }


    private final Runnable frameUpdate = new Runnable() {

        @Override
        synchronized public void run() {
            frame.removeCallbacks(frameUpdate);


            hasCollided = checkCollision(((GameBoard) findViewById(R.id.the_canvas)).b, foosmanList);
            handleCollision();
            moveFoosman();
            updateVelocity();

            Point ball = new Point(((GameBoard) findViewById(R.id.the_canvas)).b.getPointX(),
                    ((GameBoard) findViewById(R.id.the_canvas)).b.getPointY());
            ball.x = ball.x + ballVelocity.x;
            if (ball.x > ballMaxX || ball.x < 5) {
                ballVelocity.x *= -1;
            }
            ball.y = ball.y + ballVelocity.y;
            if (ball.y > ballMaxY || ball.y < 5) {
                ballVelocity.y *= -1;
            }
            ((GameBoard) findViewById(R.id.the_canvas)).b.setPoint(ball.x, ball.y);

            if (isGameHost) {
                Database.updateBallCoords(gameCode, ball.x, ball.y, ballVelocity.x,
                        ballVelocity.y);
            }

            ((GameBoard) findViewById(R.id.the_canvas)).invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }

    };

    @Override
    synchronized public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                upButtonDown = true;
                break;
            case MotionEvent.ACTION_UP:
                upButtonDown = false;
                break;
            default:
                break;
        }

        return true;
    }
}

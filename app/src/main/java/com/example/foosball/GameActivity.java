package com.example.foosball;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.example.foosball.database.Database;
import com.example.foosball.database.GameDataListener;
import com.example.foosball.drawing.GameBoard;
import com.example.foosball.models.Ball;
import com.example.foosball.models.Foosman;
import com.example.foosball.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * This acts as an Game Engine. It initiates the positions of all elements including the ball
 * and foosmen and also handles the movement of the elements for every frame.
 */
public class GameActivity extends FullScreenActivity {

    private final Handler frame = new Handler();
    private Point ballVelocity;
    private int ballMaxY;
    private int ballMaxX;
    private int foosmanWidth;
    private int foosmanHeight;
    private int canvasWidth;
    private int canvasHeight;
    private boolean collisionFromTop = false;
    private boolean collisionFromLeft = false;
    private boolean collisionFromRight = false;
    private boolean collisionFromBottom = false;
    private boolean upButtonDown = false;
    private boolean downButtonDown = false;
    private static final int FPS = 50;
    private static final int FRAME_RATE = 1000 / FPS;
    private static final int FOOSMAN_SENSITIVITY = 5;
    private static final int INITIAL_SPEED_X = 12;
    private static final int INITIAL_SPEED_Y = 8;
    private boolean isGameHost;
    private final List<String> foosmanNames = Arrays.asList("TeamAGoalie", "TeamADefender1",
            "TeamADefender2", "TeamAAttacker1", "TeamAAttacker2", "TeamAAttacker3", "TeamBGoalie",
            "TeamBDefender1", "TeamBDefender2", "TeamBAttacker1", "TeamBAttacker2", "TeamBAttacker3");
    private final List<Foosman> foosmanList = new ArrayList<>();
    private GameBoard gameBoard;
    private static final Database database = Database.getInstance();

    /**
     * This method updates the velocity of the ball.
     * It gradually increases the velocity of the ball towards 12
     * and holds steady afterwards.
     */
    private void updateVelocity() {
        int xDir = (ballVelocity.x > 0) ? 1 : -1;
        int yDir = (ballVelocity.y > 0) ? 1 : -1;
        int xSpeed = Math.abs(ballVelocity.x);
        int ySpeed = Math.abs(ballVelocity.y);
        if (xSpeed + ySpeed > 20) {
            xSpeed -= 1;
            ySpeed -= 1;
        }
        ballVelocity.x = xSpeed * xDir;
        ballVelocity.y = ySpeed * yDir;
    }

    /**
     * Checks if there is a collision between the ball and any foosmen
     * Bounds are drew around each foosman at their present position and
     * the ball. If the bounds intersect, then their bitmaps are checked
     * to find a pixel that overlaps for more precision.
     *
     * @param b Ball
     * @param foosmanList List of foosmen
     * @return Boolean if there is a detected collision
     */
    private boolean checkCollision(Ball b, List<Foosman> foosmanList) {
        Bitmap bmTeamA = BitmapFactory.decodeResource(getResources(), R.drawable.ship1);
        Bitmap bmTeamB = BitmapFactory.decodeResource(getResources(), R.drawable.ship2);
        Bitmap bmBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

        List<Rect> foosmanboundsList = new ArrayList<>();

        for (Foosman foosman : foosmanList) {
            int foosmanPointX = foosman.getPointX();
            int foosmanPointY = foosman.getPointY();
            Rect r = new Rect(foosmanPointX, foosmanPointY, foosmanPointX + foosmanWidth, foosmanPointY + foosmanHeight);
            foosmanboundsList.add(r);
        }

        Rect rball = new Rect(b.getPointX(), b.getPointY(), b.getPointX() + b.getWidth(), b.getPointY() + b.getHeight());

        for (Rect r : foosmanboundsList) {
            if (rball.top < r.top) {
                collisionFromTop = true;
                collisionFromBottom = false;
            } else if (rball.bottom > r.bottom) {
                collisionFromBottom = true;
                collisionFromTop = false;
            } else {
                collisionFromTop = false;
                collisionFromBottom = false;
            }

            if (rball.left < r.left) {
                collisionFromLeft = true;
                collisionFromRight = false;
            } else if (rball.right > r.right) {
                collisionFromRight = true;
                collisionFromLeft = false;
            } else {
                collisionFromLeft = false;
                collisionFromRight = false;
            }

            if (rball.intersect(r)) {
                for (int i = rball.left; i < rball.right; i++) {
                    for (int j = rball.top; j < rball.bottom; j++) {
                        if (bmBall.getPixel(i - rball.left, j - rball.top) != Color.TRANSPARENT) {
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

    /**
     * Changes direction and adds acceleration (in the opposite direction to collision)
     * to ball if there is collision detected
     */
    private void handleCollision() {
        int xDir = (ballVelocity.x > 0) ? 1 : -1;
        int yDir = (ballVelocity.y > 0) ? 1 : -1;
        int xSpeed = Math.abs(ballVelocity.x);
        int ySpeed = Math.abs(ballVelocity.y);
        if (xSpeed + ySpeed < 40) {
            xSpeed += 10;
            ySpeed += 10;
        }
        ballVelocity.x = xSpeed * xDir;
        ballVelocity.y = ySpeed * yDir;

        if (collisionFromTop) {
            ballVelocity.y = -Math.abs(ballVelocity.y);
            collisionFromTop = false;
        } else if (collisionFromBottom) {
            ballVelocity.y = Math.abs(ballVelocity.y);
            collisionFromBottom = false;
        }
        if (collisionFromLeft) {
            ballVelocity.x = -Math.abs(ballVelocity.x);
            collisionFromLeft = false;
        } else if (collisionFromRight) {
            ballVelocity.x = Math.abs(ballVelocity.x);
            collisionFromRight = false;
        }
    }

    /**
     * This method is called when the user presses the 'Up' or 'Down' buttons on screen.
     * It checks if `upButtonDown` or `downButtonDown` are true and then checks if the
     * foosmen closest to the canvas top and bottom edges are within a distance of 50.
     * <p>
     * If false, it specifies a new position for each foosmen by the value defined by
     * `foosmanSensitivity` in the Y-direction.
     */
    private void moveFoosman() {
        if (isGameHost) {
            if (downButtonDown) {
                if (gameBoard.getFoosman("TeamAAttacker1").getPointY() < (canvasHeight - foosmanHeight)) {
                    gameBoard.teamA.movePlayers(FOOSMAN_SENSITIVITY);
                }
            }
            if (upButtonDown) {
                if (gameBoard.getFoosman("TeamAAttacker3").getPointY() > 0) {
                    gameBoard.teamA.movePlayers(-FOOSMAN_SENSITIVITY);
                }
            }
        } else {
            if (downButtonDown) {
                if (gameBoard.getFoosman("TeamBAttacker1").getPointY() < (canvasHeight - foosmanHeight)) {
                    gameBoard.teamB.movePlayers(FOOSMAN_SENSITIVITY);
                }
            }
            if (upButtonDown) {
                if (gameBoard.getFoosman("TeamBAttacker3").getPointY() > 0) {
                    gameBoard.teamB.movePlayers(-FOOSMAN_SENSITIVITY);
                }
            }
        }

    }


    /**
     * Starts the game activity
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Handler h = new Handler();

        /**
         * Handles touch controls for up and down buttons
         */
        findViewById(R.id.up_button).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View view, MotionEvent event) {
                view.performClick();
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
        });
        findViewById(R.id.down_button).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View view, MotionEvent event) {
                view.performClick();
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
        h.postDelayed(this::initGfx, 10);
        isGameHost = Utils.isGameHost(getApplicationContext());
        gameBoard = findViewById(R.id.the_canvas);

        // Pulls ball and foosmen positions from the db
        database.startGameDataListener(new GameDataListener() {
            @Override
            public void onSuccess(int x, int y, int vx, int vy, int fya, int fyb, int scoreA,
                                  int scoreB) {

                if (!isGameHost) {
                    gameBoard.teamA.setYCord(fya);
                    if (ballVelocity != null) {
                        ballVelocity.x = vx;
                        ballVelocity.y = vy;
                    }
                    gameBoard.b.setPoint(x, y);
                    if (scoreA > gameBoard.goalB.getConceeded()) {
                        assert scoreA - gameBoard.goalB.getConceeded() == 1;
                        gameBoard.goalB.scoreGoal();
                        scoreGoal("Team A");
                        setUpNewRound();
                    }
                    if (scoreB > gameBoard.goalA.getConceeded()) {
                        assert scoreB - gameBoard.goalA.getConceeded() == 1;
                        gameBoard.goalA.scoreGoal();
                        scoreGoal("Team B");
                        setUpNewRound();
                    }
                } else {
                    gameBoard.teamB.setYCord(fyb);
                }
            }

            @Override
            public void onConnectionError() {

            }
        });
    }

    private Point getRandomStartingVelocity() {
        final int i = new Random().nextInt(4);
        switch (i) {
            case 0:
                return new Point(INITIAL_SPEED_X, INITIAL_SPEED_Y);
            case 1:
                return new Point(INITIAL_SPEED_X, -INITIAL_SPEED_Y);
            case 2:
                return new Point(-INITIAL_SPEED_X, INITIAL_SPEED_Y);
            case 3:
                return new Point(-INITIAL_SPEED_X, -INITIAL_SPEED_Y);
            default:
                throw new UnsupportedOperationException(
                        "Only should have 4 different cases for starting velocity");
        }
    }

    /**
     * Initialise graphics
     */
    synchronized public void initGfx() {
        gameBoard.bg.resetStarField();
        Point pBall, pTeamAGoalie, pTeamADefender1, pTeamADefender2, pTeamAAttacker1, pTeamAAttacker2, pTeamAAttacker3,
                pTeamBGoalie, pTeamBDefender1, pTeamBDefender2, pTeamBAttacker1, pTeamBAttacker2, pTeamBAttacker3;

        // Initialise widths and heights
        canvasWidth = gameBoard.getWidth();
        canvasHeight = gameBoard.getHeight();
        int ballWidth = gameBoard.b.getWidth();
        int ballHeight = gameBoard.b.getWidth();
        foosmanWidth = gameBoard.getFoosman("TeamAGoalie").getWidth();
        foosmanHeight = gameBoard.getFoosman("TeamAGoalie").getHeight();

        // Retrieve Foosmen
        for (String name : foosmanNames) {
            foosmanList.add(gameBoard.getFoosman(name));
        }

        pTeamAGoalie = new Point((int) (canvasWidth * 0.05), (int) (canvasHeight * 0.5));
        pTeamADefender1 = new Point((int) (canvasWidth * 0.20), (int) (canvasHeight * 0.625));
        pTeamADefender2 = new Point((int) (canvasWidth * 0.20), (int) (canvasHeight * 0.375));
        pTeamAAttacker1 = new Point((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.75));
        pTeamAAttacker2 = new Point((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.50));
        pTeamAAttacker3 = new Point((int) (canvasWidth * 0.60), (int) (canvasHeight * 0.25));
        pTeamBGoalie = new Point((int) (canvasWidth * 0.95), (int) (canvasHeight * 0.50));
        pTeamBDefender1 = new Point((int) (canvasWidth * 0.80), (int) (canvasHeight * 0.625));
        pTeamBDefender2 = new Point((int) (canvasWidth * 0.80), (int) (canvasHeight * 0.375));
        pTeamBAttacker1 = new Point((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.75));
        pTeamBAttacker2 = new Point((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.50));
        pTeamBAttacker3 = new Point((int) (canvasWidth * 0.40), (int) (canvasHeight * 0.25));
        pBall = new Point((int) (canvasWidth * 0.5), (int) (canvasHeight * 0.5));

        List<Point> foosmanPoints = Arrays.asList(pTeamAGoalie, pTeamADefender1, pTeamADefender2, pTeamAAttacker1, pTeamAAttacker2, pTeamAAttacker3,
                pTeamBGoalie, pTeamBDefender1, pTeamBDefender2, pTeamBAttacker1, pTeamBAttacker2, pTeamBAttacker3);

        // Set Positions
        for (int i = 0; i < foosmanNames.size(); i++) {
            gameBoard.getFoosman(foosmanNames.get(i)).setPoint(foosmanPoints.get(i).x - 50, foosmanPoints.get(i).y - 50);
        }
        // Set Relative positions for the team
        gameBoard.teamA.fixRelativePos();
        gameBoard.teamB.fixRelativePos();

        gameBoard.b.setPoint(pBall.x, pBall.y);
        gameBoard.goalA.setGoalPoints(5, (int) (canvasHeight * 0.7), (int) (canvasHeight * 0.3));
        gameBoard.goalB.setGoalPoints(canvasWidth - 5, (int) (canvasHeight * 0.7), (int) (canvasHeight * 0.3));

        setUpNewRound();

        ballMaxX = canvasWidth - ballWidth;
        ballMaxY = canvasHeight - ballHeight;
        findViewById(R.id.up_button).setEnabled(true);
        findViewById(R.id.down_button).setEnabled(true);
        frame.removeCallbacks(frameUpdate);
        gameBoard.invalidate(); // marks the canvas as outdated - so it will be updated on next frame
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    private void setUpNewRound() {
        ballVelocity = new Point(0, 0);
        gameBoard.b.setPoint((int) (canvasWidth * 0.5), (int) (canvasHeight * 0.5));
        gameBoard.stopBallRotation();
        final Handler h = new Handler();
        h.postDelayed(() -> displaySnackbar("Starting in 3.."), 1000);
        h.postDelayed(() -> displaySnackbar("Starting in 2.."), 2000);
        h.postDelayed(() -> displaySnackbar("Starting in 1.."), 3000);
        h.postDelayed(this::startRound, 4000);
    }

    private void displaySnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.game_root), message, 700);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setPadding(0, 0, 0, 0);
        snackbar.show();

    }

    private void startRound() {
        ballVelocity = getRandomStartingVelocity();
        gameBoard.startBallRotation();
    }

    private void scoreGoal(String name) {
        Snackbar.make(findViewById(R.id.game_root), name + " GOAL!!!",
                Snackbar.LENGTH_LONG).show();
        final TextView viewScoreTeamA = findViewById(R.id.scoreTeamA);
        final TextView viewScoreTeamB = findViewById(R.id.scoreTeamB);
        viewScoreTeamA.setText(String.format(Locale.ENGLISH, "%d", gameBoard.goalB.getConceeded()));
        viewScoreTeamB.setText(String.format(Locale.ENGLISH, "%d", gameBoard.goalA.getConceeded()));
    }

    /**
     * Starts the end game activity with the saved number of goals for each team
     *
     * @param view
     */
    public void endGame(View view) {
        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("teamA", gameBoard.goalA.getConceeded());
        intent.putExtra("teamB", gameBoard.goalB.getConceeded());
        startActivity(intent);
    }

    /**
     * Checks if greater than 3 goals are scored by any team, updates ball position on screen and
     * sends host's ball position to db per frame.
     */
    private final Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {

            // Check if goals conceded is more than 3.
            if (gameBoard.goalB.getConceeded() >= 3 || gameBoard.goalA.getConceeded() >= 3) {
                endGame(findViewById(R.id.the_canvas));
                return;
            }

            Log.d("Ball velocity", ballVelocity.x + ", " + ballVelocity.y);

            frame.removeCallbacks(frameUpdate);

            updateVelocity();
            if (checkCollision(gameBoard.b, foosmanList)) {
                handleCollision();
            }
            moveFoosman();

            Point ball = new Point(gameBoard.b.getPointX(), gameBoard.b.getPointY());
            ball.x = ball.x + ballVelocity.x;
            ball.y = ball.y + ballVelocity.y;
            gameBoard.b.setPoint(ball.x, ball.y);

            if (ball.x > ballMaxX) {
                ballVelocity.x = -Math.abs(ballVelocity.x);
                if (isGameHost) {
                    if (gameBoard.goalB.checkGoal(ball.y)) {
                        setUpNewRound();
                        scoreGoal("Team A");
                    }
                }
            }
            if (ball.x < 5) {
                ballVelocity.x = Math.abs(ballVelocity.x);
                if (isGameHost) {
                    if (gameBoard.goalA.checkGoal(ball.y)) {
                        setUpNewRound();
                        scoreGoal("Team B");
                    }
                }
            }
            if (ball.y > ballMaxY) {
                ballVelocity.y = -Math.abs(ballVelocity.y);
            }
            if (ball.y < 5) {
                ballVelocity.y = Math.abs(ballVelocity.y);
            }

            // This assumes 2 player game and sends coords to db
            if (isGameHost) {
                database.updateCoordsHost(ball.x, ball.y, ballVelocity.x, ballVelocity.y,
                        gameBoard.teamA.getYCord(), gameBoard.goalB.getConceeded(),
                        gameBoard.goalA.getConceeded());
            } else {
                database.updateCoords(gameBoard.teamB.getYCord());
            }

            gameBoard.invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }

    };
}

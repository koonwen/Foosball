package com.example.foosball.drawing;
import com.example.foosball.R;
import com.example.foosball.models.Ball;
import com.example.foosball.models.Foosman;

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

public class GameBoard extends View{
    private Paint p;
    private List<Point> starField = null;
    private int starAlpha = 80;
    private int starFade = 2;

    private List<String> foosmanNames = Arrays.asList("TeamAGoalie", "TeamADefender1",
            "TeamADefender2", "TeamAAttacker1", "TeamAAttacker2", "TeamAAttacker3", "TeamBGoalie",
            "TeamBDefender1", "TeamBDefender2", "TeamBAttacker1", "TeamBAttacker2", "TeamBAttacker3");

    private Map<String, Foosman> foosmanMap = new TreeMap<String, Foosman>();
    public Ball b;
    private boolean collisionDetected;

    synchronized  public Foosman getFoosman(String name) {
        return foosmanMap.get(name);
    }

    private int ballRotation = 0;

    // Bitmaps
    private Bitmap bmTeamA = BitmapFactory.decodeResource(getResources(), R.drawable.ship1);
    private Bitmap bmTeamB = BitmapFactory.decodeResource(getResources(), R.drawable.ship2);
    private Bitmap bmBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

    // Background
    private static final int NUM_OF_STARS = 36;
    synchronized public void resetStarField() {
        starField = null;
    }

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        p = new Paint();

        // Generate Foosman
        for (String name : foosmanNames) {
            foosmanMap.put(name, new Foosman());
        }
        // Generate Ball
        b = new Ball();
        b.setBounds(new Rect(0,0, bmBall.getWidth(), bmBall.getHeight()));
    }

    synchronized private void initializeStars(int maxX, int maxY) {
        starField = new ArrayList<Point>();
        for (int i=0; i<NUM_OF_STARS; i++) {
            Random r = new Random();
            int x = r.nextInt(maxX-5+1)+5;
            int y = r.nextInt(maxY-5+1)+5;
            starField.add(new Point (x,y));
        }
        collisionDetected = false;
    }

    private boolean checkForCollision() {

        // Create bounds for each foosman and the ball
        List<Rect> foosmanboundsList = new ArrayList<Rect>();

        for (Map.Entry<String, Foosman> entry : foosmanMap.entrySet()) {
            Foosman foosman = entry.getValue();
            int bmWidth = bmTeamA.getWidth(); // Note: Team A and B bm should have the same w & h
            int bmHeight = bmTeamB.getWidth();
            int foosmanPointX = foosman.getPointX();
            int foosmanPointY = foosman.getPointY();
            Rect r = new Rect(foosmanPointX, foosmanPointY, foosmanPointX + bmWidth, foosmanPointY + bmHeight);
            foosmanboundsList.add(r);
        }

        Rect rball = new Rect(b.getPointX(), b.getPointY(), b.getPointX() + b.getWidth(), b.getPointY() + b.getHeight());

        for (Rect r : foosmanboundsList) {
            Rect r3 = rball;
            if (rball.intersect(r)) {
                for (int i = rball.left; i<rball.right; i++) {
                    for (int j = rball.top; j<rball.bottom; j++) {
                        if (bmBall.getPixel(i-r3.left, j-r3.top) != Color.TRANSPARENT) {
                            if (bmTeamA.getPixel(i-r.left, j-r.top)!= Color.TRANSPARENT || bmTeamB.getPixel(i-r.left, j-r.top)!= Color.TRANSPARENT) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        b.setLastCollision(new Point(-1,-1));
        return false;
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {
        // Draw Background
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        if (starField==null) {
            initializeStars(canvas.getWidth(), canvas.getHeight());
        }
        p.setColor(Color.CYAN);
        p.setAlpha(starAlpha+=starFade);
        if (starAlpha>=252 || starAlpha <=80) starFade=starFade*-1;
        p.setStrokeWidth(5);
        for (int i=0; i<NUM_OF_STARS; i++) {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
        }

        // Draw Foosmen
        for (Map.Entry<String, Foosman> entry : foosmanMap.entrySet()) {
            Foosman foosman = entry.getValue();
            String name = entry.getKey();
            if (foosman.getPointX() >= 0 && name.startsWith("TeamA")) {
                canvas.drawBitmap(bmTeamA, foosman.getPointX(), foosman.getPointY(), null);
            }
            if (foosman.getPointX() >= 0 && name.startsWith("TeamB")) {
                canvas.drawBitmap(bmTeamB, foosman.getPointX(), foosman.getPointY(), null);
            }
        }

        // Draw Ball
        if (b.getPointX() > 0){
            b.mball.reset();
            b.mball.postTranslate((float)(b.getPointX()), (float)(b.getPointY()));
            b.mball.postRotate(b.getBallRotation(), (float)(b.getPointX() + b.getWidth()/2.0), (float)(b.getPointY() + b.getWidth()/2.0));
            canvas.drawBitmap(bmBall, b.mball, null);
            b.setBallRotation(b.getBallRotation() + 5);
            if (b.getBallRotation() >= 360) b.setBallRotation(0);
        }


        // Draw Points of Collision Detected
        b.setCollisionDetected(checkForCollision());
//        if (collisionDetected ) {
//            p.setColor(Color.RED);
//            p.setAlpha(255);
//            p.setStrokeWidth(5);
//            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5, lastCollision.x + 5, lastCollision.y + 5, p);
//            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5, lastCollision.x - 5, lastCollision.y + 5, p);
//        }
    }
}

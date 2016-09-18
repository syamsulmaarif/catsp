package com.syamms.firstgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by syamms on 9/17/16.
 */
public class PlayerShip {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -12;
    // Stop ship leaving the screen
    private int maxY;
    private int minY;
    //Limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;
    // A hit box for collision detection
    private Rect hitBox;
    private int shieldStrength;
    private Context context;
    // Constructor
    public PlayerShip(Context context, int screenX, int screenY) {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource
                (context.getResources(), R.drawable.ship);
        boosting = false;

        maxY = screenY - bitmap.getHeight();
        minY = 0;

        // Initialize the hit box
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        shieldStrength = 2;
    }


    public void update() {
        // Are we boosting?
        if (boosting) {
// Speed up
            speed += 125;
        } else {
// Slow down
            speed -= 125;
        }
// Constrain top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
// Never stop completely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }
// move the ship up or down
        y -= speed + GRAVITY;
        // But don't let ship stray off screen
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;

        }
        // Refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }
    //Getters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getSpeed() {
        return speed;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setBoosting() {
        boosting = true;
    }
    public void stopBoosting() {
        boosting = false;
    }
    public Rect getHitbox(){
        return hitBox;
    }
    public int getShieldStrength() {
        return shieldStrength;
    }
    public void reduceShieldStrength(){
        shieldStrength --;
    }
}
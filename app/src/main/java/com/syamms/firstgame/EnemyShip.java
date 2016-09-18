package com.syamms.firstgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by syamms on 9/17/16.
 */
public class EnemyShip {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    // Detect enemies leaving the screen
    private int maxX;
    private int minX;
    // Spawn enemies within screen bounds
    private int maxY;
    private int minY;
    // A hit box for collision detection
    private Rect hitBox;


    //Getters and Setters
    public Bitmap getBitmap(){
        return bitmap;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Rect getHitbox(){
        return hitBox;
    }
    // Constructor
    public EnemyShip(Context context, int screenX, int screenY) {
        Random generator = new Random();
        int whichBitmap = generator.nextInt(2);
        switch (whichBitmap){
            case 0:
                bitmap = BitmapFactory.decodeResource
                        (context.getResources(), R.drawable.enemy);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource
                        (context.getResources(), R.drawable.enemy2);
                break;

        }
        scaleBitmap(screenX);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        speed = generator.nextInt(6) + 10;
        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();
        // Initialize the hit box
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update(int playerSpeed){
// Move to the left
        x -= playerSpeed;
        x -= speed;
//respawn when off screen
        if(x < minX-bitmap.getWidth()){
            Random generator = new Random();
            speed = generator.nextInt(1)+2;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }
        // Move to the left
        x -= playerSpeed;
        x -= speed;
        // Refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }
    // This is used by the TDView update() method to
// Make an enemy out of bounds and force a re-spawn
    public void setX(int x) {
        this.x = x;
    }
    public void scaleBitmap(int x){
        if(x < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 3,
                    bitmap.getHeight() / 3,
                    false);
        }else if(x < 1200){
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 2,
                    bitmap.getHeight() / 2,
                    false);
        }
    }
}

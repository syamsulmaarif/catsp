package com.syamms.firstgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by syamms on 9/17/16.
 */
public class TDView extends SurfaceView implements Runnable {
    volatile boolean playing;
    Thread gameThread = null;
    //Game objects
    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public EnemyShip enemy4;
    // For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    // Make some random space dust
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private int screenX;
    private int screenY;
    private Context context;
    private boolean gameEnded;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;
    public TDView(Context context, int x, int y) {
        super(context);
        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();
        // Initialize our player ship
     /*   player = new PlayerShip(context, x, y);
        enemy1 = new EnemyShip(context, x, y);
        enemy2 = new EnemyShip(context, x, y);
        enemy3 = new EnemyShip(context, x, y);

        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
// Where will the dust spawn?
            SpaceDust spec = new SpaceDust(x, y);
            dustList.add(spec);
        }
        */
        screenX = x;
        screenY = y;
        this.context = context;
        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
//Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
//create our three fx in memory ready for use
            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(descriptor, 0);
        }catch(IOException e){
//Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
        startGame();
// Get a reference to a file called HiScores.
// If id doesn't exist one is created
        prefs = context.getSharedPreferences("HiScores",
                context.MODE_PRIVATE);
// Initialize the editor ready
        editor = prefs.edit();
// Load fastest time from a entry in the file
// labeled "fastestTime"
// if not available highscore = 1000000
        fastestTime = prefs.getLong("fastestTime", 1000000);
    }
    private void startGame(){
//Initialize game objects
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        if(screenX > 1000){
            enemy4 = new EnemyShip(context, screenX, screenY);
        }

        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
// Where will the dust spawn?
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }
// Reset time and distance
        distanceRemaining = 10000;// 10 km
        timeTaken = 0;
// Get start time
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();

        }
    }

    private void update(){
        // Collision detection on new positions
// Before move because we are testing last frames
// position which has just been drawn
// If you are using images in excess of 100 pixels
// wide then increase the -100 value accordingly
        boolean hitDetected = false;
        if(Rect.intersects
                (player.getHitbox(), enemy1.getHitbox())){
            hitDetected = true;
            enemy1.setX(-100);
        }
        if(Rect.intersects
                (player.getHitbox(), enemy2.getHitbox())){
            hitDetected = true;
            enemy2.setX(-100);
        }
        if(Rect.intersects
                (player.getHitbox(), enemy3.getHitbox())){
            hitDetected = true;
            enemy3.setX(-100);
        }
        if(screenX > 1000){
            if(Rect.intersects(player.getHitbox(), enemy4.getHitbox())){
                hitDetected = true;
                enemy4.setX(-100);
            }
        }

        if(hitDetected) {
            soundPool.play(bump, 1, 1, 0, 0, 1);
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
//game over so do something
                soundPool.play(destroyed, 1, 1, 0, 0, 1);
                gameEnded = true;

            }
        }
        // Update the player
        player.update();
        // Update the enemies
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());
        if(screenX > 1000) {
            enemy4.update(player.getSpeed());
        }
        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());

        }



        if(!gameEnded) {
//subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();
//How long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }
        //Completed the game!
        if(distanceRemaining < 0){
            soundPool.play(win, 1, 1, 0, 0, 1);
//check for new fastest time
            if(timeTaken < fastestTime) {
                fastestTime = timeTaken;
            }
// avoid ugly negative numbers
// in the HUD
            distanceRemaining = 0;
// Now end the game
            gameEnded = true;
        }
    }
    private void draw(){
        if (ourHolder.getSurface().isValid()) {
//First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();
// Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

// For debugging
// Switch to white pixels
            paint.setColor(Color.argb(255, 255, 255, 255));
// Draw Hit boxes
         //   canvas.drawRect(player.getHitbox().left,
           //         player.getHitbox().top,
             //       player.getHitbox().right,
               //     player.getHitbox().bottom,
                 //   paint);
        /*    canvas.drawRect(enemy1.getHitbox().left,
                    enemy1.getHitbox().top,
                    enemy1.getHitbox().right,
                    enemy1.getHitbox().bottom,
                    paint);
            canvas.drawRect(enemy2.getHitbox().left,
                    enemy2.getHitbox().top,
                    enemy2.getHitbox().right,
                    enemy2.getHitbox().bottom,
                    paint);
            canvas.drawRect(enemy3.getHitbox().left,
                    enemy3.getHitbox().top,
                    enemy3.getHitbox().right,
                    enemy3.getHitbox().bottom,
                    paint);
                    */
// Draw the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);
            canvas.drawBitmap
                    (enemy1.getBitmap(),
                            enemy1.getX(),
                            enemy1.getY(), paint);
            canvas.drawBitmap
                    (enemy2.getBitmap(),
                            enemy2.getX(),
                            enemy2.getY(), paint);
            canvas.drawBitmap
                    (enemy3.getBitmap(),
                            enemy3.getX(),
                            enemy3.getY(), paint);
            if(screenX > 1000) {
                canvas.drawBitmap(enemy4.getBitmap(),
                        enemy4.getX(), enemy4.getY(), paint);
            }

            // White specs of dust
            paint.setColor(Color.argb(255, 255, 255, 255));
//Draw the dust from our arrayList
            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
// Draw the player
// ...
            }
            if (!gameEnded) {
                // Draw the hud
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
               // canvas.drawText("Fastest:" + fastestTime + "s", 10, 20, paint);
                canvas.drawText("Fastest:" +
                        formatTime(fastestTime) +
                        "s", 10, 20, paint);
                //canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 20,
                  //      paint);
                canvas.drawText("Time:" +
                        formatTime(timeTaken) +
                        "s", screenX / 2, 20, paint);
                canvas.drawText("Distance:" +
                        distanceRemaining / 1000 +
                        " KM", screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield:" +
                        player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed:" +
                        player.getSpeed() * 60 +
                        " MPS", (screenX / 3) * 2, screenY - 20, paint);
            }else{
                //this happens when the game is ended
                // Show pause screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2, 100, paint);
                paint.setTextSize(25);
               // canvas.drawText("Fastest:"+ fastestTime + "s", screenX/2, 160, paint);
                canvas.drawText("Fastest:"+
                        formatTime(fastestTime) + "s", screenX/2, 160, paint);
                //canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 200, paint);
                canvas.drawText("Time:"
                        + formatTime(timeTaken) + "s", screenX / 2, 200, paint);
                canvas.drawText("Distance remaining:" +
                        distanceRemaining/1000 + " KM",screenX/2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX/2, 350, paint);
            }
// Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
            }


    }
    private void control(){
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
        }
    }
// Clean up our thread if the game is interrupted or the playe quits
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    // Make a new thread and start it
// Execution moves to our R
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();

    }
    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
// There are many different events in MotionEvent
// We care about just 2 - for now.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
// Has the player lifted their finger up?
            case MotionEvent.ACTION_UP:
// Do something here
                player.stopBoosting();
                // If we are currently on the pause screen, start a new game
                if(gameEnded){
                    startGame();
                }
                break;
// Has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
// Do something here
                player.setBoosting();
                break;
        }
        return true;
    }
    private String formatTime(long time){
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;
        if (thousandths < 100){strThousandths = "0" + thousandths;}
        if (thousandths < 10){strThousandths = "0" + strThousandths;}
        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
    }
}

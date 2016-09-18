package com.syamms.firstgame;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

public class GameActivity extends Activity {
    // Our object to handle the View
    private TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This is where the "Play" button from HomeActivity sends us
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
// Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

// Create an instance of our Tappy Defender View (TDView)
// Also passing in "this" which is the Context of our app

// Create an instance of our Tappy Defender View
// Also passing in this.
// Also passing in the screen resolution to the constructor
        gameView = new TDView(this, size.x, size.y);
// Make our gameView the view for the Activity
        setContentView(gameView);
    }

    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
    // If the player hits the back button, quit the app
   /* public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }*/
    @Override
    public void onBackPressed() {
//onPause();

    }

}

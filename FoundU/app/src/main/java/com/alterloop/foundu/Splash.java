package com.alterloop.foundu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.alterloop.util.MyLocationListener;

import java.sql.SQLDataException;
import java.sql.SQLException;

public class Splash extends ActionBarActivity {
    private ViewFlipper viewFlipper;
    private float lastX;

    // --- Global variables
    LocationManager mlocManager;
    LocationListener mlocListener;
    Double lat = 0.0;
    Double lng = 0.0;
    StringBuilder strReturnedAddress = new StringBuilder();
    String address = "";
    int rowcount=0;     // Identify whether first time user or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Hide action bar
        ActionBar a = getSupportActionBar();
        a.hide();

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        viewFlipper.setFlipInterval(2000);
        viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(this,R.anim.slide_out_to_left);
        viewFlipper.startFlipping();

        Button checkIn=(Button)findViewById(R.id.CheckIn);
        checkIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // If phonenumber row exists redirect to LocationSharePrompt screen
                // else redirect to Register new user screen.
                if(rowcount>0) {
                    startActivity(new Intent(getApplication(), LocationSharePrompt.class));
                }
                else {
                    startActivity(new Intent(getApplication(), RegisterNewUser.class));
                }
            }
        });

        try {
            // ----------------Start: Location fetch section -------------------------
            // Try to fetch location, lat long and address while splash screen is running
            mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mlocListener = new MyLocationListener(Splash.this);
            mlocManager.getBestProvider(new Criteria(), true);

            /* Provides	the name of the provider with which to register
            minTime	minimum time interval between location updates, in milliseconds
            minDistance	minimum distance between location updates, in meters
            listener a LocationListener whose onLocationChanged(Location) method will be called for each location update
            */

            // Network service gives fast results
            if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, mlocListener);
            }
            //GPS takes time but gives more accurate results
            else if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, mlocListener);
            }
            // ----------------End: Location fetch section -----------------------

            // ----------------Start: Database setup -----------------------------
            String userPhoneNumber="";
            // On opening of the App
            // Scenario 1: App opened for the first time -> create the database and required table
            // Scenario 2: Not the first time, then check whether user phonenumber row exists in table and isisActivated=1(true)

            SQLiteDatabase db=openOrCreateDatabase("FoundApp",MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    "RegisteredUserInfo(id INTEGER PRIMARY KEY AUTOINCREMENT, Phonenumber TEXT, " +
                    "UserName TEXT, Address TEXT, Latitude REAL, Longitude REAL, Status TEXT, RegistrationDate DATETIME DEFAULT CURRENT_TIMESTAMP, isActivated INTEGER DEFAULT 0, ActivationCode TEXT, ImageId TEXT );");
            db.execSQL("CREATE TABLE IF NOT EXISTS FriendLocationInfo(id INTEGER PRIMARY KEY AUTOINCREMENT, Phonenumber TEXT, FriendName TEXT, Latitude REAL, Longitude REAL, Address TEXT, AddressDisplay TEXT, Range REAL, RangeDisplay TEXT, Status TEXT, LastSeenOn DATETIME DEFAULT CURRENT_TIMESTAMP, LastSeenOnDisplay TEXT, ImageId TEXT);");
            db.execSQL("CREATE TABLE IF NOT EXISTS Notifications(id INTEGER PRIMARY KEY AUTOINCREMENT, GlobalID INTEGER DEFAULT 0, Phonenumber TEXT, NotificationText TEXT, isShown INTEGER DEFAULT 0, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP); ");
            Cursor c=db.rawQuery("SELECT * FROM RegisteredUserInfo WHERE isActivated=1",null);

            while(c.moveToNext()) {
                rowcount=rowcount+1;
            }
            db.close();
            // ----------------End: Database setup -------------------------------
        }
        catch (Exception e){
            System.out.print("..."+e.getMessage());
        }
        finally {
            SQLiteDatabase.releaseMemory();
        }

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Found ?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Splash.super.onBackPressed();
                    }
                }).create().show();
    }

    // Using the following method, we will handle all screen swaps.
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = touchevent.getX();

                // Handling left to right screen swap.
                if (lastX < currentX) {

                    // If there aren't any other children, just break.
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    // Next screen comes in from left.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                    // Current screen goes out from right.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

                    // Display next screen.
                    viewFlipper.showNext();
                }

                // Handling right to left screen swap.
                if (lastX > currentX) {

                    // If there is a child (to the left), kust break.
                    if (viewFlipper.getDisplayedChild() == 1)
                        break;

                    // Next screen comes in from right.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                    // Current screen goes out from left.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

                    // Display previous screen.
                    viewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }
}
package com.alterloop.foundu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alterloop.kriya.FoundService;
import com.alterloop.util.MyLocationListener;
import com.alterloop.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class LocationSharePrompt extends ActionBarActivity {

    TextView currentLocation, intermediateMessages, locationSharePrompt;
    Button submitLocationYesOrNo;
    RadioButton shareLocationYes;
    RadioButton shareLocationNo;
    RadioGroup radioGroup;
    EditText userStatus;

    LocationManager mlocManager;
    LocationListener mlocListener;
    Double lat = 0.0;
    Double lng = 0.0;
    StringBuilder strReturnedAddress = new StringBuilder();
    String[] addressAndStatus =null;

    String phonenumber = "";
    ArrayList<String> contactPhoneNumberList = new ArrayList<String>();
    //store phonenumber and name as key value pair
    Map<String, String> dictionary = new HashMap<String, String>();
    //String webserviceURL="http://aspspider.ws/yashgoswami/RestServiceImpl.svc/JSONSaveLocation";
    //RESTful Web service URL to save self location
    //String webserviceURLSaveSelfLocation="http://services.alterloop.com.103-21-58-169.sdin-pp-wb3.webhostbox.net/RestServiceImpl.svc/JSONSaveLocation";
    //RESTful Web service URL to fetch friend location
    //String webserviceURLFetchFriendLocation="http://services.alterloop.com.103-21-58-169.sdin-pp-wb3.webhostbox.net/RestServiceImpl.svc/getlocationsoffriends";

    String global="";
    static AlertDialog alert;
    ProgressDialog locationFetchProgressDialog;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_share_prompt);

        try {

            //check Internt connection
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
            } else {
                //Allow loading till current location is fetched.
                //locationFetchProgressDialog = ProgressDialog.show(LocationSharePrompt.this, "Progress Dialog", "Loading...");
                //region Initialization
                currentLocation = (TextView) findViewById(R.id.textViewLocation);
                locationSharePrompt = (TextView) findViewById(R.id.textViewLocationSharePrompt);
                intermediateMessages = (TextView) findViewById(R.id.textViewIntermediateMessages);
                radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                shareLocationNo = (RadioButton) findViewById(R.id.radioButtonShareLocationNo);
                shareLocationYes = (RadioButton) findViewById(R.id.radioButtonShareLocationYes);
                submitLocationYesOrNo = (Button) findViewById(R.id.buttonSubmitLocationYesOrNo);
                userStatus = (EditText) findViewById(R.id.editTextStatus);
                //endregion
                //intermediateMessages.setText("Move you device a bit. Fetching Location.");
                mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //Check for Location Services on/Off
                //LocationSharePrompt lsp = new LocationSharePrompt();
                if ((!mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) && (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                    buildAlertMessageNoGps(this);
                }
                addressAndStatus = new String[2]; // stores address at index 0, Status at index 1
                addressAndStatus = Utility.fetchCurrentAddressAndStatusFromDB(this.getApplicationContext());
                if (addressAndStatus != null) {
                    if (addressAndStatus[0].equals(""))
                        currentLocation.setText("Location not found. Check your internet connection ...");
                    else
                        currentLocation.setText("You are Near\n" + addressAndStatus[0]);
                    //Show already status saved in local DB
                    userStatus.setText(addressAndStatus[1]);
                } else {
                    currentLocation.setText("Location not found. Check your internet connection ...");
                }

                //region Start :Pick contact List phone numbers and prepare a list
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getPhoneNumberListAndSaveInDB(LocationSharePrompt.this.getContentResolver());
                    }
                });
                t.start();
            }
            // Start repeating service
            Intent myIntent = new Intent(LocationSharePrompt.this, FoundService.class);
            pendingIntent = PendingIntent.getService(LocationSharePrompt.this, 0, myIntent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.add(Calendar.SECOND, 10);
            // Cancel alarms
            try {
                alarmManager.cancel(pendingIntent);
            } catch (Exception e) {
                //Log.e(TAG, "AlarmManager update was not canceled. " + e.toString());
            }
            //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), 90*1000, pendingIntent);
            //Toast.makeText(LocationSharePrompt.this, "Start Alarm", Toast.LENGTH_LONG).show();

        }
        catch(Exception e)
        {
            System.out.print("..."+e.getMessage());

        }
        //endregion

        // endregion
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Found ?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        LocationSharePrompt.super.onBackPressed();
                    }
                }).create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_share_prompt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplication(), Settings.class));
            return true;
        }
        else if (id == R.id.action_refresh) {
            if (!haveNetworkConnection()) {
                Toast.makeText(this, "FoundU requires an active internet connection. Please check your internet connection", Toast.LENGTH_LONG).show();
            } else {
                refreshCurrentLocation();
            }            return true;
        }
        else if(id==R.id.action_help) {
            startActivity(new Intent(getApplication(), Help.class));
            return true;
        }
        else if(id==R.id.action_notifications) {
            startActivity(new Intent(getApplication(), Notifications.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshCurrentLocation() {

        locationFetchProgressDialog = ProgressDialog.show(LocationSharePrompt.this, "Progress Dialog", "Loading...");

        // Try to fetch location, lat long and address while splash screen is running
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener(LocationSharePrompt.this);
        mlocManager.getBestProvider(new Criteria(), true);

            /* Provides	the name of the provider with which to register
            minTime	minimum time interval between location updates, in milliseconds
            minDistance	minimum distance between location updates, in meters
            listener a LocationListener whose onLocationChanged(Location) method will be called for each location update
            */

        // Network service gives fast results
        if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, mlocListener);
            intermediateMessages.setText("Using Network Service");
        }
        //GPS takes time but gives more accurate results
        else if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, mlocListener);
            intermediateMessages.setText("Using GPS.");
        }
        if(locationFetchProgressDialog.isShowing())
        {
            locationFetchProgressDialog.dismiss();
        }
        finish();
        startActivity(getIntent());
    }

    //Method to fetch phone numbers form contact List call web service, save returned result in DB
    public void getPhoneNumberListAndSaveInDB(ContentResolver cr) {

        String result="";
        Map<String, String> dictionary=new HashMap<String, String>();
        dictionary=Utility.getPhoneNumberList(cr);
        // list of phonenumbers will be converted in JSON format and posted to wcf web service
        JSONArray jsonArrayPhoneList=new JSONArray();

        try{
            jsonArrayPhoneList=Utility.createJSONArray(dictionary);
            result= Utility.GetSessionFetch(Utility.webserviceURLFetchFriendLocation, jsonArrayPhoneList);
            // Save web service data returned data in local DB
            // last param 'true' indicates image will be downloaded.
            boolean flag=Utility.fetchWebServiceResultAndSave(result, dictionary, this.getApplicationContext(),true);
        }
        catch(Exception ex) {
        }
        finally {
            jsonArrayPhoneList=null;
            dictionary=null;
            SQLiteDatabase.releaseMemory();
        }
    }

    // Submit button click event : method names defined in xml
    public void submitLocationYesOrNo(View view)
    {
        try {
            // If Yes radio button selected -> call webservice and save location
            // if No, dont call webservice
            if (radioGroup.getCheckedRadioButtonId() == shareLocationNo.getId()) {
                //Toast.makeText(getApplicationContext(), "No clicked", Toast.LENGTH_SHORT).show();
                //Do not call webservice to save user's current location in global repo
                //startActivity(new Intent(getApplication(), Search.class));
                startActivity(new Intent(getApplication(), Friends.class));
            } else if (radioGroup.getCheckedRadioButtonId() == shareLocationYes.getId()) {
                //Toast.makeText(getApplicationContext(), "Yes clicked", Toast.LENGTH_SHORT).show();
                //Call webservice to save user's current location in global repo
                Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveSelfLocationInfo();
                    }
                });
                t2.start();

                //startActivity(new Intent(getApplication(), Search.class));
                startActivity(new Intent(getApplication(), Friends.class));
            } else {
                Toast.makeText(getApplicationContext(), "No option selected.", Toast.LENGTH_SHORT).show();
            }
            boolean flag;
            if(userStatus.getText().toString()!="")
                flag=Utility.updateUserStatus(getApplicationContext(), userStatus.getText().toString());
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Recheck your Internet and Location settings.", Toast.LENGTH_SHORT).show();
            System.out.print("...."+ex.getMessage());
        }

    }

    private void saveSelfLocationInfo() {
        JSONObject jsonList=new JSONObject();
        try {
            //region get current date in yyyy-MM-dd HH:mm:ss
            //Calendar calendar = Calendar.getInstance();
            //2014-10-13 23:54:00
            // Save DateTime in UTC format in global Database
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            //String strDate = sdf.format(calendar.getTime());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String strDate=sdf.format(new Date());
            //endregion
            SQLiteDatabase db=openOrCreateDatabase("FoundApp",MODE_PRIVATE,null);
            Cursor c=db.rawQuery("SELECT * FROM RegisteredUserInfo WHERE isActivated=1",null);

            while(c.moveToNext()) {
                jsonList.put("phonenumber",c.getString(c.getColumnIndex("Phonenumber")));
                jsonList.put("longitude", c.getFloat(c.getColumnIndex("Longitude")));
                jsonList.put("latitude", c.getFloat(c.getColumnIndex("Latitude")));
                jsonList.put("lastSeenOn",strDate);    //UTC format
                jsonList.put("address", c.getString(c.getColumnIndex("Address")));
                if(c.getString(c.getColumnIndex("ImageId"))!=null)
                    jsonList.put("imageId",c.getString(c.getColumnIndex("ImageId")));
                else
                    jsonList.put("imageId","null");
                jsonList.put("status",c.getString(c.getColumnIndex("Status")));
            }
            db.close();
            // Call webservice method to save self location and return true or false
            String result= Utility.GetSession2(Utility.webserviceURLSaveSelfLocation, jsonList);

        }
        catch(Exception ex)
        {

        }
        finally {
            jsonList=null;
            SQLiteDatabase.releaseMemory();
        }

    }

    public void buildAlertMessageNoGps(final Activity actObj ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(actObj);
        builder.setTitle("Enable Location Services");
        builder.setMessage("Your Location services seems to be disabled, please enable it to move ahead")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,  int id) {
                        //Intent intent = new Intent(Settings.LOCATION_SERVICE);
                        //actObj.startActivity(intent);
                        //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        //startActivity(intent);
                        //alert.dismiss();
                        actObj.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                    }
                });
        alert = builder.create();
        alert.show();

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
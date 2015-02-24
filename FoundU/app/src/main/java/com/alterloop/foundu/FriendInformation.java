package com.alterloop.foundu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Date;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alterloop.util.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class FriendInformation extends ActionBarActivity {

    TextView textViewFriendAddress,textViewFriendName, textViewFriendRange, textViewFriendLastSeenOn, textViewFriendPhonenumber, textViewFriendStatus;
    ImageView imageViewFriendImage;
    String[] latLngNameAddressLastSeenOnRangePhone;
    Double[] friendLatLng;
    public final static String param_Lat="", param_Lng="", param_Name="", param_Address="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_information);
        Date LastSeenOn = null;

        latLngNameAddressLastSeenOnRangePhone = new String[9];
        try {
            String paramFromSearchPage = getIntent().getStringExtra(Search.ID_EXTRA);
            friendLatLng = new Double[2];
            textViewFriendAddress = (TextView) findViewById(R.id.textViewFriendAddress);
            textViewFriendName = (TextView) findViewById(R.id.textViewFriendName);
            textViewFriendRange = (TextView) findViewById(R.id.textViewFriendRange);
            textViewFriendLastSeenOn = (TextView) findViewById(R.id.textViewFriendLastSeenOn);
            textViewFriendPhonenumber=(TextView)findViewById(R.id.textViewFriendPhonenumber);
            textViewFriendStatus=(TextView)findViewById(R.id.textViewFriendStatus);
            imageViewFriendImage=(ImageView)findViewById(R.id.imageViewFriend);
            //fetch Friend details based on the item selected from Search page
            latLngNameAddressLastSeenOnRangePhone = Utility.populateFriendDetails(this.getApplicationContext(), paramFromSearchPage);
            // fill details
            friendLatLng[0] = Double.parseDouble(latLngNameAddressLastSeenOnRangePhone[0]);
            friendLatLng[1] = Double.parseDouble(latLngNameAddressLastSeenOnRangePhone[1]);
            textViewFriendName.setText(latLngNameAddressLastSeenOnRangePhone[2]);
            textViewFriendAddress.setText("Near " + latLngNameAddressLastSeenOnRangePhone[3]);
            textViewFriendLastSeenOn.setText(latLngNameAddressLastSeenOnRangePhone[4]);
            textViewFriendRange.setText("Within " + latLngNameAddressLastSeenOnRangePhone[5]);
            textViewFriendPhonenumber.setText(latLngNameAddressLastSeenOnRangePhone[6]);
            if(!latLngNameAddressLastSeenOnRangePhone[7].equals("null")) {
                Bitmap myBitmap = Utility.ConvertToImage(latLngNameAddressLastSeenOnRangePhone[7]);
                imageViewFriendImage.setImageBitmap(myBitmap);
            }
            else
            {
                imageViewFriendImage.setImageResource(R.drawable.defaultimg);
            }
            if(!latLngNameAddressLastSeenOnRangePhone[8].equals("null"))
            {textViewFriendStatus.setText(latLngNameAddressLastSeenOnRangePhone[8]);}
            else
                textViewFriendStatus.setText("");
        }
        catch(Exception ex)
        {
            String exc=ex.getMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_information, menu);
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

    public void viewFriendOnMap(View view)
    {
        /*Intent intent = new Intent(getApplicationContext(), FriendDetails.class);
        intent.putExtra(param_Lat,latLngNameAddressLastSeenOnRangePhone[0]);
        intent.putExtra(param_Lng,latLngNameAddressLastSeenOnRangePhone[1]);
        intent.putExtra(param_Name,latLngNameAddressLastSeenOnRangePhone[2]);
        intent.putExtra(param_Address,latLngNameAddressLastSeenOnRangePhone[3]);
        startActivity(intent);*/
        //startActivity(new Intent(getApplication(), FriendDetails.class));

    }
}

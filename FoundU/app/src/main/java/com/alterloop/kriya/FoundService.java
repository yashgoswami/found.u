package com.alterloop.kriya;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alterloop.foundu.Friends;
import com.alterloop.foundu.Notifications;
import com.alterloop.foundu.R;
import com.alterloop.foundu.Search;
import com.alterloop.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yash on 11/16/2014.
 */
public class FoundService extends Service {

    public String CUSTOM_MESSAGE_MANY=" of your friends are in proximity. Say Hi! to them.";
    public String CUSTOM_MESSAGE_ONE=" is in your proximity. Say Hi!. ";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        //Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        //Log.d(TAG, "onCreate");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        //Log.d(TAG, "onStart");
        /*String s="";
        SQLiteDatabase db1 = openOrCreateDatabase("FoundApp", Context.MODE_PRIVATE, null);

        Cursor c = db1.rawQuery("SELECT * FROM FriendLocationInfo", null);
        while (c.moveToNext()) {
          s=c.getString(c.getColumnIndex("FriendName"));
        }
        Toast.makeText(this, "Friend Name: " +s, Toast.LENGTH_LONG).show();
        db1.close();
        */

        //region Start :Pick contact List phone numbers and prepare a list
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getPhoneNumberListAndSaveInDB(FoundService.this.getContentResolver());
            }
        });
        t.start();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                getNewNotificationsAndSaveInDB();
            }
        });
        t2.start();

        //showNotification();


    }

    // Method to fetch new notifications from global repository
    private void getNewNotificationsAndSaveInDB() {
        JSONObject notificationRequest=new JSONObject();
        Double[] latlng=new Double[2];
        List<String> newMessages=new ArrayList<String>();
        String notificationIdsAlreadyShown="", notificationResponse="";
        try{
            //prepare json of format {"latitude":"76.32","longitude":"24.56","idsNotToShow":"1,2,3"}
            latlng=Utility.fetchCurrentLatLngFromDB(this.getApplicationContext());
            notificationIdsAlreadyShown= Utility.fetchAlreadyShownNotificationIds(this.getApplicationContext());

            if(latlng[0].intValue()!=0 && latlng[1].intValue()!=0) {
                notificationRequest = Utility.createNotificationRequestJSON(latlng, notificationIdsAlreadyShown);
                // wait for reply from webservice
                notificationResponse  = Utility.GetSession(Utility.webserviceURLFetchNotifications, notificationRequest);
                // JSONArray response of the form {"notificationID":"1","notificationText":"Generic notification for all"}
                newMessages=Utility.insertGlobalNotifications(this.getApplicationContext(), notificationResponse);

                if(newMessages.size()>0) {
                    for (int i = 0; i < newMessages.size(); i++) {
                        showCustomNotification(newMessages.get(i));
                    }
                }
            }
        }
        catch(Exception ex) {



        }
        finally {

        }
    }

    //Method to fetch phone numbers form contact List call web service, save returned result in DB
    public void getPhoneNumberListAndSaveInDB(ContentResolver cr) {

        String result="";
        boolean status=false;
        List<String> nearbyFriends = new ArrayList<String>();
        Map<String, String> dictionary=new HashMap<String, String>();
        dictionary=Utility.getPhoneNumberList(cr);
        // list of phonenumbers will be converted in JSON format and posted to wcf web service
        JSONArray jsonArrayPhoneList=new JSONArray();

        try{
            jsonArrayPhoneList=Utility.createJSONArray(dictionary);
            result= Utility.GetSessionFetch(Utility.webserviceURLFetchFriendLocationNoImage, jsonArrayPhoneList);
            // Save web service data returned data in local DB
            boolean flag=Utility.fetchWebServiceResultAndSave(result, dictionary, this.getApplicationContext(),false);
            nearbyFriends=Utility.checkForNotifications(this.getApplicationContext());
            if(nearbyFriends.size()>1) {
                if(!Utility.checkBeforeNotificationInsert(this.getApplicationContext(),nearbyFriends.size()+CUSTOM_MESSAGE_MANY)) {
                    showCustomNotification(nearbyFriends.size()+CUSTOM_MESSAGE_MANY);
                    status=Utility.insertFriendNotifications(this.getApplicationContext(),nearbyFriends.size()+CUSTOM_MESSAGE_MANY );
                }
            }
            else if(nearbyFriends.size()==1){
                if(!Utility.checkBeforeNotificationInsert(this.getApplicationContext(),nearbyFriends.get(0)+CUSTOM_MESSAGE_ONE)) {
                    showCustomNotification(nearbyFriends.get(0) + CUSTOM_MESSAGE_ONE);
                    status = Utility.insertFriendNotifications(this.getApplicationContext(), nearbyFriends.get(0) + CUSTOM_MESSAGE_ONE);
                }
            }
            //else
            //    showCustomNotification("No body nearby.");
        }
        catch(Exception ex) {
        }
        finally {
            jsonArrayPhoneList=null;
            dictionary=null;
            nearbyFriends=null;
            SQLiteDatabase.releaseMemory();
        }
    }

    public void showNotification() {
        //We get a reference to the NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String MyText = "Reminder";
        Notification mNotification = new Notification(R.drawable.ic_menu_notification, MyText, System.currentTimeMillis() );
        //The three parameters are: 1. an icon, 2. a title, 3. time when the notification appears
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults |= Notification.DEFAULT_SOUND;

        String MyNotificationTitle = "Found!";
        String MyNotificationText  = "Some of your friends are nearby.";

        //Intent MyIntent = new Intent(Intent.ACTION_VIEW);
        //Intent MyIntent = new Intent(getApplicationContext(), Search.class);
        Intent MyIntent = new Intent(getApplicationContext(), Friends.class);
        PendingIntent StartIntent = PendingIntent.getActivity(getApplicationContext(),0,MyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //A PendingIntent will be fired when the notification is clicked. The FLAG_CANCEL_CURRENT flag cancels the pendingintent

        mNotification.setLatestEventInfo(getApplicationContext(), MyNotificationTitle, MyNotificationText, StartIntent);

        int NOTIFICATION_ID = 1;
        notificationManager.notify(NOTIFICATION_ID , mNotification);
        //We are passing the notification to the NotificationManager with a unique id.
    }

    public void showCustomNotification(String message) {
        //We get a reference to the NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String MyText = "Friends in proximity";
        Notification mNotification = new Notification(R.drawable.ic_menu_notification, MyText, System.currentTimeMillis() );
        //The three parameters are: 1. an icon, 2. a title, 3. time when the notification appears
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults |= Notification.DEFAULT_SOUND;

        String MyNotificationTitle = "Found!";
        String MyNotificationText  = message;

        //Intent MyIntent = new Intent(Intent.ACTION_VIEW);
        Intent MyIntent = new Intent(getApplicationContext(), Notifications.class);
        PendingIntent StartIntent = PendingIntent.getActivity(getApplicationContext(),0,MyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //A PendingIntent will be fired when the notification is clicked. The FLAG_CANCEL_CURRENT flag cancels the pendingintent

        mNotification.setLatestEventInfo(getApplicationContext(), MyNotificationTitle, MyNotificationText, StartIntent);

        int NOTIFICATION_ID = 1;
        notificationManager.notify(NOTIFICATION_ID , mNotification);
        //We are passing the notification to the NotificationManager with a unique id.
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        //Log.d(TAG, "onDestroy");
    }

}

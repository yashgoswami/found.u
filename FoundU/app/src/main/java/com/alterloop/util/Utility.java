package com.alterloop.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Toast;

//import com.google.android.gms.drive.internal.AddEventListenerRequest;

import com.alterloop.foundu.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLClientInfoException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Yash on 11/15/2014.
 */
public class Utility {

    //public static String webserviceURLSaveSelfLocation="http://services.alterloop.com.103-21-58-169.sdin-pp-wb3.webhostbox.net/RestServiceImpl.svc/JSONSaveLocation";
    public static String webserviceURLSaveSelfLocation="http://services.alterloop.com/RestServiceImpl.svc/JSONSaveLocation";


    //RESTful Web service URL to fetch friend location
    //public static String webserviceURLFetchFriendLocation="http://services.alterloop.com.103-21-58-169.sdin-pp-wb3.webhostbox.net/RestServiceImpl.svc/getlocationsoffriends";
    public static String webserviceURLFetchFriendLocation="http://services.alterloop.com/RestServiceImpl.svc/getlocationsoffriends";
    //RESTful webservice URL to fetch friend location without image string; called by background service
    public static String webserviceURLFetchFriendLocationNoImage="http://services.alterloop.com/RestServiceImpl.svc/getlocationsoffriendswithoutimage";

    //RESTful Web service URL to fetch relevant notifications from global database
    //public static String webserviceURLFetchNotifications="http://services.alterloop.com.103-21-58-169.sdin-pp-wb3.webhostbox.net/RestServiceImpl.svc/getNotifications";
    public static String webserviceURLFetchNotifications="http://services.alterloop.com/RestServiceImpl.svc/getNotifications";

    // Method to call REST webservice POST method and pass json data
    // JSONobject of <Lat, Long, LastSeenOn, Phonenumber> is passed
    public static String GetSession(String URL, JSONObject jsonListOfPhonenumbers) {
        boolean isValid = true;
        String Result = "";

        if (isValid) {

            HttpPost requestAuth = new HttpPost(URL);
            try {
                StringEntity se = new StringEntity(jsonListOfPhonenumbers.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                requestAuth.setHeader("Accept", "application/json");
                requestAuth.setEntity(se);
                DefaultHttpClient httpClientAuth = new DefaultHttpClient();
                HttpResponse responseAuth = httpClientAuth.execute(requestAuth);
                HttpEntity responseEntityAuth = responseAuth.getEntity();
                char[] bufferAuth = new char[(int) responseEntityAuth.getContentLength()];
                InputStream streamAuth = responseEntityAuth.getContent();
                InputStreamReader readerAuth = new InputStreamReader(streamAuth);
                readerAuth.read(bufferAuth);
                streamAuth.close();
                Result = new String(bufferAuth);
            } catch (ClientProtocolException e)
            {

            } catch (IOException e)
            {


            } catch (Exception e) {
                System.out.println("exce" + e.getMessage() + e.getStackTrace());
                //Log.i("RM", "error", e);
            }
            finally{
                requestAuth=null;
            }
        }
        return Result;
    }

    public static String GetSession2(String URL, JSONObject jsonListOfPhonenumbers) {
        boolean isValid = true;
        String Result = "";
        StringBuilder builder = new StringBuilder();
        if (isValid) {

            HttpPost requestAuth = new HttpPost(URL);
            try {
                StringEntity se = new StringEntity(jsonListOfPhonenumbers.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                requestAuth.setHeader("Accept", "application/json");
                requestAuth.setEntity(se);
                DefaultHttpClient httpClientAuth = new DefaultHttpClient();
                HttpResponse responseAuth = httpClientAuth.execute(requestAuth);
                HttpEntity responseEntityAuth = responseAuth.getEntity();
                /*char[] bufferAuth = new char[(int) responseEntityAuth.getContentLength()];
                InputStream streamAuth = responseEntityAuth.getContent();
                InputStreamReader readerAuth = new InputStreamReader(streamAuth);
                readerAuth.read(bufferAuth);
                streamAuth.close();
                Result = new String(bufferAuth);*/
                InputStream streamAuth = responseEntityAuth.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(streamAuth));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }

                Result=builder.toString();


            } catch (ClientProtocolException e)
            {

            } catch (IOException e)
            {


            } catch (Exception e) {
                System.out.println("exce" + e.getMessage() + e.getStackTrace());
                //Log.i("RM", "error", e);
            }
            finally{
                builder=null;
                requestAuth=null;
            }
        }
        return Result;
    }


    // JSONArray is passed containing list of friend phone numbers and List of
    // <Lat, Long, LastSeenOn, Phonenumber> is returned.
    public static String GetSessionFetch(String URL, JSONArray jsonListOfPhonenumbers) {
        boolean isValid = true;
        String Result = "";
        StringBuilder builder = new StringBuilder();
        if (isValid) {

            HttpPost requestAuth = new HttpPost(URL);
            try {
                StringEntity se = new StringEntity(jsonListOfPhonenumbers.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                requestAuth.setHeader("Accept", "application/json");
                requestAuth.setEntity(se);
                DefaultHttpClient httpClientAuth = new DefaultHttpClient();
                HttpResponse responseAuth = httpClientAuth.execute(requestAuth);
                HttpEntity responseEntityAuth = responseAuth.getEntity();
                /*char[] bufferAuth = new char[(int) responseEntityAuth.getContentLength()];
                InputStream streamAuth = responseEntityAuth.getContent();
                InputStreamReader readerAuth = new InputStreamReader(streamAuth);
                readerAuth.read(bufferAuth);
                streamAuth.close();
                Result= new String(bufferAuth);*/

                InputStream streamAuth = responseEntityAuth.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(streamAuth));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }

                Result=builder.toString();

            }
            catch (ClientProtocolException e) {
                System.out.println("exce" + e.getMessage() + e.getStackTrace());
            }
            catch (IOException e) {
                System.out.println("exce" + e.getMessage() + e.getStackTrace());
            }
            catch (Exception e) {
                System.out.println("exce" + e.getMessage() + e.getStackTrace());
            }
            finally {
                builder=null;
                requestAuth=null;
            }

        }
        return Result;
    }

    //Method to fetch phone numbers form contact List
    public static Map<String, String> getPhoneNumberList(ContentResolver cr) {

        Map<String, String> dictionary=new HashMap<String, String>();
        String phonenumber="";
        try {
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phonenumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //Remove extra spaces in a contact's phonenumber
                if (phonenumber.contains(" ")) {
                    String[] temp = phonenumber.split(" ");
                    String tempPhonenumber = "";
                    for (String s : temp) {
                        tempPhonenumber += s;
                    }
                    phonenumber = tempPhonenumber;
                    tempPhonenumber = null;
                }
                if (!dictionary.containsKey(phonenumber)) {
                    dictionary.put(phonenumber, name); //key, value pair
                }
            }
            phones.close();
        } catch (Exception ex) {
            System.out.print("...." + ex.getMessage());
        }
        finally{
            phonenumber=null;
        }
        return dictionary;
    }

    // Create a JSONArray from dictionary key values
    public static JSONArray createJSONArray( Map<String, String> dictionary) {
        JSONArray jsonArrayPhoneList = new JSONArray();
        try {
            // iterate through dictionary keys and create JSONArray
            for (String key : dictionary.keySet()) {
                JSONObject jsonList = new JSONObject();
                jsonList.put("phonenumber", key);
                jsonArrayPhoneList.put(jsonList);
                jsonList = null;
            }

        }
        catch(JSONException ex) {
        }
        catch(Exception ex){
        }
        finally{
        }
        return jsonArrayPhoneList;
    }

    //Json object of form {"latitude":"55.88","longitude":"55.88","idsNotToBeShown":"1"}
    public static JSONObject createNotificationRequestJSON(Double[] latlng, String notificationIDs)
    {
        JSONObject obj=new JSONObject();
        try{
            obj.put("latitude",latlng[0].toString());
            obj.put("longitude",latlng[1].toString());
            obj.put("idsNotToBeShown",notificationIDs);
        }
        catch(JSONException ex) {
        }
        catch(Exception ex){
        }
        finally {

        }
        return obj;
    }

    // Method to update status of user
    public static boolean updateUserStatus(Context context, String statusToUpdate)
    {
        boolean flag=false;
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            db.execSQL("UPDATE RegisteredUserInfo SET  Status='" + statusToUpdate + "';");
            db.close();
            flag=true;

        } catch (Exception ex) {
        }
        return flag;
    }
    // Fetches JSON data returned from web service and save in local Database
    // condition imageReturn indicates whether image is to be saved or not.
    public static boolean fetchWebServiceResultAndSave(String result, Map<String, String> dictionary, Context context, boolean imageReturn)
    {
        int rowCount=0;
        Double selfLatitude=0.0, selfLongitude=0.0;
        String temp,Phonenumber="",FriendName="", Address="",
                AddressDisplay="", LastSeenOn="", LastSeenOnDisplay="", RangeDisplay="", ImageId="", Status="";
        Double Latitude, Longitude, Range;
        boolean flag=false;
        try {
            SQLiteDatabase db1 = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            Cursor cursor=db1.rawQuery("SELECT * FROM RegisteredUserInfo WHERE isActivated=1",null);
            while(cursor.moveToNext()) {
                selfLongitude= cursor.getDouble(cursor.getColumnIndex("Longitude"));
                selfLatitude= cursor.getDouble(cursor.getColumnIndex("Latitude"));
            }

            JSONArray jsonArray = new JSONArray(result);
            //db1.execSQL("DELETE from FriendLocationInfo;");
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Phonenumber = object.getString("phonenumber");
                //fetch name from contact
                FriendName = dictionary.get(Phonenumber).indexOf("'")>0?dictionary.get(Phonenumber).replace("'",""):dictionary.get(Phonenumber); // fetch value of key
                Longitude = Double.parseDouble(object.getString("longitude"));
                Latitude = Double.parseDouble(object.getString("latitude"));
                Address = object.getString("address").trim();
                Status=object.getString("status").trim();
                if(Address.length()>45) {
                    AddressDisplay=Address.substring(0,44)+"...";
                }
                else
                    AddressDisplay=Address.trim();
                LastSeenOn = object.getString("lastSeenOn"); // UTC timezone

                // UTC to local time zone
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date myDate = simpleDateFormat.parse(object.getString("lastSeenOn"));
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                LastSeenOn=dateFormat2.format(myDate);
                // For display purpose in format like 09 Nov 09:00 Am
                SimpleDateFormat print = new SimpleDateFormat("dd MMM hh:mm a");
                LastSeenOnDisplay=print.format(myDate);

                Range = DistanceAlgorithm.DistanceBetweenPoints(selfLatitude, selfLongitude, Latitude, Longitude);
                //Convert 5.098 to 5 km
                RangeDisplay=RangeToString(Range);
                //ImageId="img"+Phonenumber.substring(1);     //if phonenumber is +919654490876 then imageId = img919654490876
                ImageId=object.getString("imageId");
                Cursor c = db1.rawQuery("SELECT * FROM FriendLocationInfo where Phonenumber like '%" + Phonenumber + "%'", null);
                while (c.moveToNext()) {
                    rowCount = rowCount + 1;
                }
                if (rowCount == 0) {
                    String sql="";
                    if(imageReturn) {
                        sql="INSERT INTO FriendLocationInfo(Phonenumber, FriendName, Latitude, Longitude, Address, Range, RangeDisplay, LastSeenOn, LastSeenOnDisplay, AddressDisplay, ImageId,Status)" +
                                " VALUES('" + Phonenumber + "', '" +
                                FriendName + "'," + Latitude + "," + Longitude +
                                ",'" + Address + "'," + Range +",'"+RangeDisplay+"','"+LastSeenOn+"','"+LastSeenOnDisplay+"','"+
                                AddressDisplay+"','"+ImageId+"','"+Status+"');";
                    }
                    else {
                        sql = "INSERT INTO FriendLocationInfo(Phonenumber, FriendName, Latitude, Longitude, Address, Range, RangeDisplay, LastSeenOn, LastSeenOnDisplay, AddressDisplay, Status)" +
                                " VALUES('" + Phonenumber + "', '" +
                                FriendName + "'," + Latitude + "," + Longitude +
                                ",'" + Address + "'," + Range + ",'" + RangeDisplay + "','" + LastSeenOn + "','" + LastSeenOnDisplay + "','" +
                                AddressDisplay + "','" + Status + "');";
                    }
                    db1.execSQL(sql);

                }
                else
                {
                    String sql="";
                    if(imageReturn){
                        sql="UPDATE FriendLocationInfo set FriendName='"+FriendName+"', Latitude="
                                +Latitude+", Longitude="+Longitude+", Address='"+Address+"', Range="+Range
                                +", LastSeenOn='"+ LastSeenOn+"'"+", LastSeenOnDisplay='"+LastSeenOnDisplay+"'"
                                +", AddressDisplay='"+AddressDisplay+"',RangeDisplay='"+RangeDisplay+"'"+
                                ", ImageId='"+ImageId+"'"+", Status='"+Status+"'"+
                                " where Phonenumber like '%" + Phonenumber + "%';";
                    }
                    else {
                        sql = "UPDATE FriendLocationInfo set FriendName='" + FriendName + "', Latitude="
                                + Latitude + ", Longitude=" + Longitude + ", Address='" + Address + "', Range=" + Range
                                + ", LastSeenOn='" + LastSeenOn + "'" + ", LastSeenOnDisplay='" + LastSeenOnDisplay + "'"
                                + ", AddressDisplay='" + AddressDisplay + "',RangeDisplay='" + RangeDisplay + "'" +
                                ", Status='" + Status + "'" +
                                " where Phonenumber like '%" + Phonenumber + "%';";
                    }
                    db1.execSQL(sql);
                }
                rowCount=0;
                object=null;
                flag=true;
            }
            db1.close();

        }
        catch(Exception ex){
            String c=ex.toString();
        }
        finally {
            SQLiteDatabase.releaseMemory();
        }
        return flag;
    }

    private static String RangeToString(Double range) {
        String temp="";
        int roundedValue=(int)Math.round(range);
        temp=Integer.toString(roundedValue)+" km";
        return temp;
    }

    // Fetch current address and Status from DB
    public static String[] fetchCurrentAddressAndStatusFromDB(Context context) {
        String addressAndStatus[]=new String[2];
        try {
            SQLiteDatabase db1 = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            Cursor cursor=db1.rawQuery("SELECT Address, Status FROM RegisteredUserInfo WHERE isActivated=1",null);
            while(cursor.moveToNext()) {
                addressAndStatus[0]= cursor.getString(cursor.getColumnIndex("Address"));
                addressAndStatus[1]= cursor.getString(cursor.getColumnIndex("Status"));
            }
        }
        catch(Exception ex) {
            addressAndStatus=null;
        }
        finally {
            SQLiteDatabase.releaseMemory();
        }
        return addressAndStatus;
    }

    // Fetch current latitude and longitude from DB
    // if lat lng not present return null;
    public static Double[] fetchCurrentLatLngFromDB(Context context) {
        Double[] latlng=new Double[2];
        try {
            SQLiteDatabase db1 = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            Cursor cursor=db1.rawQuery("SELECT Latitude, Longitude FROM RegisteredUserInfo WHERE isActivated=1",null);
            while(cursor.moveToNext()) {
                latlng[0]= cursor.getDouble(cursor.getColumnIndex("Latitude"));
                latlng[1]=cursor.getDouble(cursor.getColumnIndex("Longitude"));
            }
        }
        catch(Exception ex) {
            latlng[0]=0.0;
            latlng[1]=0.0;
        }
        finally {
            SQLiteDatabase.releaseMemory();
        }
        return latlng;
    }

    // create a comma separated list of all Notification IDs already shown and saved in users db
    public static String fetchAlreadyShownNotificationIds(Context context)
    {
        String commaSeparatedList="";
        try {
            SQLiteDatabase db1 = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            // GlobalID represents the id of notification saved in global DB. 0 indicates non global notificaiton.
            Cursor cursor=db1.rawQuery("SELECT GlobalID FROM Notifications WHERE GlobalID<>0",null);
            while(cursor.moveToNext()) {
                if (commaSeparatedList.equals("")) {
                    commaSeparatedList=cursor.getString(cursor.getColumnIndex("id"));
                }
                else {
                    commaSeparatedList=commaSeparatedList+","+cursor.getString(cursor.getColumnIndex("id"));
                }
            }
        }
        catch(Exception ex) {
            commaSeparatedList="";
        }
        finally {
            SQLiteDatabase.releaseMemory();
        }
        if (commaSeparatedList.equals("")) {
            commaSeparatedList="-1";
        }
        return commaSeparatedList;
    }



    // Save self details in local DB
    public static boolean updateCurrentAddressInDB(Context context, String address, double lat, double lng)
    {
        boolean flag=false;
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            db.execSQL("UPDATE RegisteredUserInfo SET  Address='" + address + "', Latitude=" + lat + ", Longitude=" + lng + ";");
            db.close();
            flag=true;
        } catch (Exception ex) {
        }
        finally{
            SQLiteDatabase.releaseMemory();
        }
        return flag;
    }
    // update self profile through settings page on upload button click
    public static boolean updateProfilePic(Context context, String imageBase64String){
        boolean flag=false;
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            db.execSQL("UPDATE RegisteredUserInfo SET  ImageId='" + imageBase64String + "';");
            db.close();
            flag=true;
        } catch (Exception ex) {
        }
        finally{
            SQLiteDatabase.releaseMemory();
        }
        return flag;
    }

    // update self profile through settings page on upload button click
    public static String readImageString(Context context){
        boolean flag=false;
        String image="";
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            Cursor cursor=db.rawQuery("SELECT ImageId FROM RegisteredUserInfo",null);
            while(cursor.moveToNext()) {
                image=cursor.getString(cursor.getColumnIndex("ImageId"));
            }
            db.close();
            flag=true;
        } catch (Exception ex) {
        }
        finally{
            SQLiteDatabase.releaseMemory();
        }
        return image;
    }

    // Check if any of friends is nearby and send notification
    public static List<String> checkForNotifications(Context context) {
        int count=0;
        List<String> nearbyFriends = new ArrayList<String>();
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);

            //Cursor cursor=db.rawQuery("Select * from FriendLocationInfo where Range < 2 and " +
            //        "LastSeenOn between DATETIME(CURRENT_TIMESTAMP, '+10 minutes') AND CURRENT_TIMESTAMP",null);
            //String sql="Select * from FriendLocationInfo where Range < 220 and LastSeenOn> datetime('now', '-8 hours')";
            String sql="Select * from FriendLocationInfo F where Range < 5";

            Cursor cursor=db.rawQuery(sql,null);
            while(cursor.moveToNext()) {

                String friendName=cursor.getString(cursor.getColumnIndex("FriendName"));
                String lastSeenOn=cursor.getString(cursor.getColumnIndex("LastSeenOn"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                java.util.Date lastSeenDate = simpleDateFormat.parse(lastSeenOn);
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
                Date date = new Date();
                //String p=dateFormat.format(date); //2014/08/06 15:59:48
                // GetTime returns milliseconds . Fetch hours.
                long diff = (date.getTime() - lastSeenDate.getTime())/(1000*60*60);
                // only those who have been within 5 km in last 6 hours
                if(diff<6) {
                    nearbyFriends.add(friendName);
                    count=count+1;
                }
            }
            db.close();


        } catch (Exception ex) {
            String s=ex.getMessage();
        }

        finally{
            SQLiteDatabase.releaseMemory();
        }
        return nearbyFriends;
    }

    // insert nearby friends notifications and save in DB

    public static boolean insertFriendNotifications(Context context,String text) {
        boolean flag=true;
        int count=0;
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            // notification not already displayed within 6 hours

            db.execSQL("INSERT INTO NOTIFICATIONS(NotificationText, isShown) VALUES('"+text+"',"+1+");");
            db.close();

        } catch (Exception ex) {
            flag=false;
        }
        finally{
            SQLiteDatabase.releaseMemory();
        }
        return flag;
    }

    // method inserts new notifications and returns the text of newly inserted messages.
    public static List<String> insertGlobalNotifications(Context context,String jsonArraystring) {
        boolean flag=false;
        int count=0;
        List<String> messages=new ArrayList<String>();
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);

            JSONArray jsonArray = new JSONArray(jsonArraystring);
            // notification not already displayed within 6 hours
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                int notificationID = Integer.parseInt(object.getString("notificationID"));
                String notificationText = object.getString("notificationText");
                String sql = "Select * from NOTIFICATIONS where GlobalID=" + notificationID;
                Cursor cursor = db.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    count = count + 1;
                }
                // insert only if global id already does not exist
                if (count == 0) {
                    db.execSQL("INSERT INTO NOTIFICATIONS(GlobalID, NotificationText, isShown) VALUES(" + notificationID + ",'" + notificationText + "'," + 1 + ");");
                    flag=true;
                    messages.add(notificationText);
                }
                count=0;
            }
            db.close();
        } catch (Exception ex) {
            flag=false;
        }
        finally{
            SQLiteDatabase.releaseMemory();
        }
        return messages;
    }

    public static boolean checkBeforeNotificationInsert(Context context,String text) {
        boolean flag=true;
        int count=0;
        try {
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            // notification not already displayed within 6 hours
            //String sql="Select * from NOTIFICATIONS where NotificationText like '%"+text+"%' and " +
            //        "Timestamp< datetime('now') and Timestamp> datetime('now', '-6 hours')";
            String sql="Select * from NOTIFICATIONS N where N.NotificationText like '%"+text+"%'";

            Cursor cursor=db.rawQuery(sql,null);
            while(cursor.moveToNext()) {
                String timestamp=cursor.getString(cursor.getColumnIndex("Timestamp"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date lastSeenDate = simpleDateFormat.parse(timestamp);
                //DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
                Date date = new Date();
                // GetTime returns milliseconds . Fetch hours.
                long diff = (date.getTime() - lastSeenDate.getTime())/(1000*60*60);
                // notification not already displayed within 6 hours
                if(diff<6)
                    count=count+1;
            }
            if (count==0)
                flag=false;
            db.close();

        } catch (Exception ex) {
            flag=false;
        }
        finally{
            SQLiteDatabase.releaseMemory();
        }
        return flag;
    }


    // Fetch Friend Details and return latitude longitude
    // @param context passed from calling activity
    // @param Id passed from search page i.e row num
    public static String[] populateFriendDetails(Context context, String parameterFromSearchPage)
    {

        String[] latLngNameAddressLastSeenOnRange=new String[9];
        try
        {   // Toast.makeText(getApplicationContext(), "Item clicked", Toast.LENGTH_SHORT).show();
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            // simplecursor adapter needs a column with name '_id'
            Cursor c = db.rawQuery("Select rowid _id, FriendName, Phonenumber, Address, Range, RangeDisplay, LastSeenOn,Status, LastSeenOnDisplay, Latitude, Longitude, ImageId " +
                    "from FriendLocationInfo where rowid='" + parameterFromSearchPage  + "'", null);
            while(c.moveToNext())
            {
                latLngNameAddressLastSeenOnRange[0]=c.getString(c.getColumnIndex("Longitude")).trim();
                latLngNameAddressLastSeenOnRange[1]=c.getString(c.getColumnIndex("Latitude")).trim();

                latLngNameAddressLastSeenOnRange[2]=c.getString(c.getColumnIndex("FriendName")).trim();
                latLngNameAddressLastSeenOnRange[3]=c.getString(c.getColumnIndex("Address")).trim();
                latLngNameAddressLastSeenOnRange[4]=c.getString(c.getColumnIndex("LastSeenOnDisplay")).trim();
                latLngNameAddressLastSeenOnRange[5]=c.getString(c.getColumnIndex("RangeDisplay")).trim();
                latLngNameAddressLastSeenOnRange[6]=c.getString(c.getColumnIndex("Phonenumber")).trim();
                latLngNameAddressLastSeenOnRange[7]=c.getString(c.getColumnIndex("ImageId")).trim();
                latLngNameAddressLastSeenOnRange[8]=c.getString(c.getColumnIndex("Status")).trim();
            }
            db.close();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        finally {
            SQLiteDatabase.releaseMemory();

        }
        return latLngNameAddressLastSeenOnRange;
    }

    // COnvert Base64 string to bitmap
    public static Bitmap ConvertToImage(String image) {
        try {
            //InputStream stream = new ByteArrayInputStream(image.getBytes());
            InputStream stream = new ByteArrayInputStream(Base64.decode(image.getBytes(), Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    // Get country codes
    public static int GetCountryZipCode(Context context){
        String CountryID="";
        String CountryZipCode="";
        String combination="";
        int countryPosition=0;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            combination=rl[i];
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                countryPosition=i;
                break;
            }
        }
        return countryPosition;
    }


}


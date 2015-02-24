package com.alterloop.util;

        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.app.SearchManager;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Build;
        import android.provider.ContactsContract;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.support.v7.widget.SearchView;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.TextView;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

        import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;


public class FriendHelper  {

    private final String ImageId;
    private final String Name;
    private final String Address;
    private final String PhoneNumber;
    private final String LastSeenOn;
    private final String Range;
    private final String Lat;
    private final String Lng;
    private final String Status;


    ListView friendsInfoList;
    SimpleCursorAdapter adapter;
    TextView test;
    //public final static String ID_EXTRA = "com.foundu.yash.foundu.Search._ID";
    ArrayList<String> contactPhoneNumberList = new ArrayList<String>();
    ProgressDialog FriendDetailFetchProgressDialog;
    //private static final String friendInfo[]=new String[5];

    /* getter setter and constructor for foldable view starts*/

    public FriendHelper(String ImageId,String Name,String Address, String PhoneNumber, String LastSeenOn, String Range, String Lat, String Lng, String Status)
    {
        this.ImageId=ImageId;
        this.Name=Name;
        this.Address=Address;
        this.PhoneNumber=PhoneNumber;
        this.LastSeenOn=LastSeenOn;
        this.Range=Range;
        this.Lat=Lat;
        this.Lng=Lng;
        this.Status=Status;
    }

    public String getImageId() {
        return ImageId;
    }

    public String getName() {
        return Name;
    }


    public String getAddress() {
        return Address;
    }

    public String getLastSeenOn() {
        return LastSeenOn;
    }

    public String getRange() {
        return Range;
    }
    public String getPhoneNo() {
        return PhoneNumber;
    }

    public String getLat() { return Lat;}

    public String getLng() { return Lng;}

    public String getStatus() { return Status;}

    public  static FriendHelper[] populateListViewFromDB(Context context) {

        FriendHelper[] list = new FriendHelper[0];
        try {

            String phonenumberTrimmed = "";
            //adapter = null;
            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            // simplecursor adapter needs a column with name '_id'
            //Cursor cursor = db.rawQuery("Select rowid _id,FriendName,ImageId, Phonenumber,Address,AddressDisplay, Range, RangeDisplay, LastSeenOn, LastSeenOnDisplay from FriendLocationInfo order by Range", null);
            Cursor cursor = db.rawQuery("Select * from FriendLocationInfo order by range", null);
            int cnt = cursor.getCount();
            /*Foldable changes starts*/
            db.close();

            String[] FriendName = new String[cnt]; // Names array
            String[] ImageId = new String[cnt]; // Image array
            String[] PhoneNumber = new String[cnt]; // PhoneNumber array
            String[] AddressDisplay = new String[cnt]; // AddressDisplay array
            String[] Range = new String[cnt]; // Range array
            String[] LastSeenOnDisplay = new String[cnt]; // LastSeenOnDisplay array
            String[] Lat = new String[cnt]; //Latitude array
            String[] Lng = new String[cnt]; // Longitude Array
            String[] Status=new String[cnt]; // Status array
            int i = 0;
            if (cursor != null && cursor.moveToFirst()) ;
            do {
                PhoneNumber[i] = cursor.getString(cursor.getColumnIndex("Phonenumber"));
                FriendName[i] = cursor.getString(cursor.getColumnIndex("FriendName"));
                AddressDisplay[i] = cursor.getString(cursor.getColumnIndex("Address"));
                Range[i] = cursor.getString(cursor.getColumnIndex("RangeDisplay"));
                LastSeenOnDisplay[i] = cursor.getString(cursor.getColumnIndex("LastSeenOnDisplay"));
                ImageId[i] = cursor.getString(cursor.getColumnIndex("ImageId"));
                Lat[i]=cursor.getString(cursor.getColumnIndex("Latitude"));
                Lng[i]=cursor.getString(cursor.getColumnIndex("Longitude"));
                Status[i]=cursor.getString(cursor.getColumnIndex("Status")).equals("null")?"":cursor.getString(cursor.getColumnIndex("Status"));
                i = i + 1;
            } while (i < cursor.getCount() && cursor.moveToNext());


            int size = FriendName.length;
            list = new FriendHelper[size];

            for (int j = 0; j < size; j++) {
                list[j] = new FriendHelper(ImageId[j], FriendName[j], AddressDisplay[j], PhoneNumber[j],
                        LastSeenOnDisplay[j], Range[j], Lat[j], Lng[j], Status[j]);
            }

        /*commented for foldable changes end*/
        }catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return list;
    }
            /*Foldable changes ends */

    /* Populate data by filtering from  the Search bar -> Starts */

    public  static FriendHelper[] populateListViewFromDBOnSearch(String query, Context context) {

        FriendHelper[] listSearch = new FriendHelper[0];
        boolean numericCheck;
        double numericQuery = 0.0;
        Cursor c;


        try {
            numericQuery = Double.parseDouble(query);
            numericCheck = true;
        } catch (NumberFormatException nfe) {
            numericCheck = false;
        }


        try {

            SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
            // simplecursor adapter needs a column with name '_id'
            // If string is number
            if (numericCheck) {
                c = db.rawQuery("Select * from FriendLocationInfo" +
                        " WHERE Address like '%" + query + "%' or FriendName like '%" + query + "%' or Range <" + numericQuery + " order by Range", null);
            } else {
                c = db.rawQuery("Select * from FriendLocationInfo" +
                        " WHERE Address like '%" + query + "%' or FriendName like '%" + query + "%' order by Range", null);
            }
            //


            int count = c.getCount();
            db.close();

            String[] FriendName = new String[count]; // Names array
            String[] ImageId = new String[count]; // Image array
            String[] PhoneNumber = new String[count]; // PhoneNumber array
            String[] AddressDisplay = new String[count]; // AddressDisplay array
            String[] Range = new String[count]; // Range array
            String[] LastSeenOnDisplay = new String[count]; // LastSeenOnDisplay array
            String[] Lat = new String[count]; //Latitude array
            String[] Lng = new String[count]; // Longitude Array
            String[] Status = new String[count]; // Longitude Array
            int i = 0;
            if (c != null && c.moveToFirst()) ;
            do {
                PhoneNumber[i] = c.getString(c.getColumnIndex("Phonenumber"));
                FriendName[i] = c.getString(c.getColumnIndex("FriendName"));
                AddressDisplay[i] = c.getString(c.getColumnIndex("Address"));
                Range[i] = c.getString(c.getColumnIndex("RangeDisplay"));
                LastSeenOnDisplay[i] = c.getString(c.getColumnIndex("LastSeenOnDisplay"));
                ImageId[i] = c.getString(c.getColumnIndex("ImageId"));
                Lat[i]=c.getString(c.getColumnIndex("Latitude"));
                Lng[i]=c.getString(c.getColumnIndex("Longitude"));
                Status[i]=c.getString(c.getColumnIndex("Status")).equals("null")?"":c.getString(c.getColumnIndex("Status"));
                i = i + 1;
            } while (i < c.getCount() && c.moveToNext());


            int size = FriendName.length;
            listSearch = new FriendHelper[size];

            for (int j = 0; j < size; j++) {
                listSearch[j] = new FriendHelper(ImageId[j], FriendName[j], AddressDisplay[j], PhoneNumber[j],
                        LastSeenOnDisplay[j], Range[j],Lat[j],Lng[j], Status[j]);
            }

        }

        catch (Exception ex) {
            String a =ex.getMessage();
        }

        return listSearch;
    }




    /* Populate data by filtering from the Search bar -> ends */


}

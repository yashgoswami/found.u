package com.alterloop.foundu;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.alterloop.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Search extends ActionBarActivity {

    ListView friendsInfoList;
    SimpleCursorAdapter adapter;
    TextView test;
    public final static String ID_EXTRA = "";
    ArrayList<String> contactPhoneNumberList = new ArrayList<String>();
    ProgressDialog FriendDetailFetchProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            //test = (TextView) findViewById(R.id.textViewTest);
            SQLiteDatabase db1 = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
            StringBuilder temp = new StringBuilder();
            Cursor c = db1.rawQuery("SELECT * FROM FriendLocationInfo", null);
            while (c.moveToNext()) {
                temp.append(c.getString(c.getColumnIndex("FriendName")));

            }
            //test.setText(temp.toString());
            temp = null;
            db1.close();

            friendsInfoList = (ListView) findViewById(R.id.listViewFriendsInfo);
            populateListViewFromDB();
        } catch (Exception e) {
            System.out.print("..." + e.getMessage());
            //test.setText(e.getMessage());
        } finally {
            SQLiteDatabase.releaseMemory();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //return true;

        //this.menu=menu;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

                SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

                search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

                search.setQueryHint("Area, Name, Range ... ");
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


                    @Override
                    public boolean onQueryTextChange(String query) {

                        //loadData(query);
                        //Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
                        populateListViewFromDBOnSearch(query);
                        return true;

                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        //loadData(query);

                        return true;

                    }

                });

            }
        } catch (Exception e) {
            System.out.print("..." + e.getMessage());
        }
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }
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

    public void populateListViewFromDB() {
        MySimpleCursorAdapter mAdapter;
        try {
            String phonenumberTrimmed = "";
            adapter = null;
            SQLiteDatabase db = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
            // simplecursor adapter needs a column with name '_id'
            Cursor c = db.rawQuery("Select rowid _id,FriendName,ImageId, Phonenumber,Address,AddressDisplay, Range, RangeDisplay, LastSeenOn, LastSeenOnDisplay from FriendLocationInfo order by Range", null);
            mAdapter = new MySimpleCursorAdapter(this, R.layout.single_row, c,
                    new String[]
                            {"FriendName", "AddressDisplay", "RangeDisplay", "LastSeenOnDisplay","ImageId"},
                    new int[]
                            {R.id.friend_name,
                                    R.id.friend_address,
                                    R.id.friend_range,
                                    R.id.friend_lastseenon,
                                    R.id.friend_imageView});
            friendsInfoList.setAdapter(mAdapter);

            db.close();

            // To handle the click on List View Item
            friendsInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                    //On clicking a list item take to its details
                    int pos = position;
                    //Intent intent = new Intent(getApplicationContext(), FriendDetails.class);
                    Intent intent = new Intent(getApplicationContext(), FriendInformation.class);
                    intent.putExtra(ID_EXTRA, String.valueOf(arg3));

                    startActivity(intent);
                }

            });


        }
        catch(Exception ex)
        {

        }
    }

    private void populateListViewFromDBOnSearch(String query) {
        MySimpleCursorAdapter mAdapter;
        boolean numericCheck;
        double numericQuery = 0.0;
        Cursor c;
        try {

            try {
                numericQuery = Double.parseDouble(query);
                numericCheck = true;
            } catch (NumberFormatException nfe) {
                numericCheck = false;
            }

            adapter = null;
            SQLiteDatabase db = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
            // simplecursor adapter needs a column with name '_id'
            // If string is number
            if (numericCheck) {
                c = db.rawQuery("Select rowid _id,FriendName, Address, AddressDisplay, Range, RangeDisplay, ImageId,LastSeenOn, LastSeenOnDisplay from FriendLocationInfo" +
                        " WHERE Address like '%" + query + "%' or FriendName like '%" + query + "%' or Range <" + numericQuery + " order by Range", null);
            } else {
                c = db.rawQuery("Select rowid _id,FriendName, Address, AddressDisplay, Range, RangeDisplay,ImageId,LastSeenOn, LastSeenOnDisplay from FriendLocationInfo" +
                        " WHERE Address like '%" + query + "%' or FriendName like '%" + query + "%' order by Range", null);
            }
            //setup mapping from cursor
            //String[] fromFieldNames = new String[]
            //        {"FriendName", "Address", "Range", "LastSeenOn"};
            mAdapter = new MySimpleCursorAdapter(this, R.layout.single_row, c,
                    new String[]
                            {"FriendName", "AddressDisplay", "RangeDisplay", "LastSeenOnDisplay","ImageId"},
                    new int[]
                            {R.id.friend_name,
                                    R.id.friend_address,
                                    R.id.friend_range,
                                    R.id.friend_lastseenon,
                                    R.id.friend_imageView});
            friendsInfoList.setAdapter(mAdapter);

            db.close();

            // To handle the click on List View Item
            friendsInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                    //On clicking a list item take to its details
                    int pos = position;
                    //Intent intent = new Intent(getApplicationContext(), FriendDetails.class);
                    Intent intent = new Intent(getApplicationContext(), FriendInformation.class);
                    intent.putExtra(ID_EXTRA, String.valueOf(arg3));

                    startActivity(intent);
                }

            });


        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    //Called when Refresh button in menu is clicked
    private void refresh() {
        //Stop the running service from here//MyService is your service class name
        //Service will only stop if it is already running.
        //stopService(new Intent(this, FoundService.class));

        //region Start :Pick contact List phone numbers and prepare a list
        FriendDetailFetchProgressDialog = ProgressDialog.show(Search.this, "Progress Dialog", "Loading...");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getPhoneNumberListAndSaveInDB(Search.this.getContentResolver());
            }
        });
        t.start();
        // endregion
    }

    //Method to fetch phone numbers form contact List
    public void getPhoneNumberListAndSaveInDB(ContentResolver cr) {

        String result = "";
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary = Utility.getPhoneNumberList(cr);
        // list of phone numbers will be converted in JSON format and posted to wcf web service
        JSONArray jsonArrayPhoneList = new JSONArray();

        try {
            jsonArrayPhoneList = Utility.createJSONArray(dictionary);
            result = Utility.GetSessionFetch(Utility.webserviceURLFetchFriendLocation, jsonArrayPhoneList);
            // Save web service data returned data in local DB
            boolean flag = Utility.fetchWebServiceResultAndSave(result, dictionary, this.getApplicationContext(),true);
            if (FriendDetailFetchProgressDialog.isShowing()) {
                FriendDetailFetchProgressDialog.dismiss();
            }
            // reload the activity
            finish();
            startActivity(getIntent());
        } catch (Exception ex) {
        } finally {
            jsonArrayPhoneList = null;
            dictionary = null;
        }
    }

    private class MySimpleCursorAdapter extends SimpleCursorAdapter {

        public MySimpleCursorAdapter(Context context, int layout, Cursor cur,
                                     String[] from, int[] to) {
            super(context, layout, cur, from, to);
        }

        public void bindView(View v, Context context, Cursor c) {

            try{
                String imageId = c.getString(c.getColumnIndex("ImageId"));
                String friendName = c.getString(c.getColumnIndex("FriendName"));
                String addressDisplay = c.getString(c.getColumnIndex("AddressDisplay"));
                String rangeDisplay = c.getString(c.getColumnIndex("RangeDisplay"));
                String lastSeenOnDisplay = c.getString(c.getColumnIndex("LastSeenOnDisplay"));

                TextView friend_name = (TextView) v.findViewById(R.id.friend_name);
                if (friend_name != null) {
                    friend_name.setText(friendName);
                }
                TextView friend_address = (TextView) v.findViewById(R.id.friend_address);
                if (friend_address != null) {
                    //friend_address.setText(addressDisplay);
                }
                TextView friend_range = (TextView) v.findViewById(R.id.friend_range);
                if (friend_range != null) {
                    friend_range.setText(rangeDisplay);
                }
                TextView friend_lastseenon = (TextView) v.findViewById(R.id.friend_lastseenon);
                if (friend_range != null) {
                    friend_lastseenon.setText(lastSeenOnDisplay);
                }
                ImageView friend_imageView = (ImageView) v.findViewById(R.id.friend_imageView);
                if(!imageId.equals("null") && friend_imageView!=null) {
                    Bitmap myBitmap = Utility.ConvertToImage(imageId);
                    friend_imageView.setImageBitmap(myBitmap);
                }
                else
                {
                    friend_imageView.setImageResource(R.drawable.defaultimg);
                }
                //Bitmap myBitmap = ConvertToImage(imageId);
                //ImageView cimg = (ImageView) findViewById(R.id.friend_imageView);
                //cimg.setImageBitmap(myBitmap);
            }
            catch(Exception ex) {
                String e=ex.getMessage();
            }
        }
    }

    /*public Bitmap ConvertToImage(String image) {
        try {
            //InputStream stream = new ByteArrayInputStream(image.getBytes());
            InputStream stream = new ByteArrayInputStream(Base64.decode(image.getBytes(), Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }*/


}

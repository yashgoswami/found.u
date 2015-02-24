package com.alterloop.foundu;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class Notifications extends ActionBarActivity {

    ListView notificationList;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        notificationList = (ListView) findViewById(R.id.listViewNotification);
        populateListViewNotificationsFromDB();
    }

    private void populateListViewNotificationsFromDB() {
        try {
            adapter = null;
            SQLiteDatabase db = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
            // simplecursor adapter needs a column with name '_id'
            Cursor c = db.rawQuery("Select rowid _id, Phonenumber , NotificationText, Timestamp from Notifications where isShown=1 " +
                    "order by Timestamp desc", null);

            //setup mapping from cursor
            String[] fromFieldNames = new String[]
                    {"NotificationText", "Timestamp"};
            int[] toViewIDs = new int[]
                    {R.id.notification_text,
                            R.id.notification_timestamp};

            adapter = new SimpleCursorAdapter(this, R.layout.single_row_notification, c, fromFieldNames, toViewIDs);
            notificationList.setAdapter(adapter);
            db.close();

            // To handle the click on List View Item
            notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                    //On clicking a list item take to its details
                    int pos = position;
                    //Intent intent = new Intent(getApplicationContext(), FriendDetails.class);
                    //Intent intent = new Intent(getApplicationContext(), FriendInformation.class);
                    //intent.putExtra(ID_EXTRA, String.valueOf(arg3));

                    //startActivity(intent);
                }

            });

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    private void populateListViewNotificationsFromDBOnSearch(String keyword) {
        try {
            adapter = null;
            SQLiteDatabase db = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
            // simplecursor adapter needs a column with name '_id'
            Cursor c = db.rawQuery("Select rowid _id, Phonenumber , NotificationText, Timestamp from Notifications " +
                    "WHERE  NotificationText like '%" + keyword + "%' order by Timestamp desc", null);

            //setup mapping from cursor
            String[] fromFieldNames = new String[]
                    {"NotificationText", "Timestamp"};
            int[] toViewIDs = new int[]
                    {R.id.notification_text,
                            R.id.notification_timestamp};

            adapter = new SimpleCursorAdapter(this, R.layout.single_row_notification, c, fromFieldNames, toViewIDs);
            notificationList.setAdapter(adapter);
            db.close();

            // To handle the click on List View Item
            notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                    //On clicking a list item take to its details
                    int pos = position;
                    //Intent intent = new Intent(getApplicationContext(), FriendDetails.class);
                    //Intent intent = new Intent(getApplicationContext(), FriendInformation.class);
                    //intent.putExtra(ID_EXTRA, String.valueOf(arg3));

                    //startActivity(intent);
                }

            });

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
                search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
                search.setQueryHint("Keyword... ");
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String query) {
                        populateListViewNotificationsFromDBOnSearch(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplication(), Settings.class));
            return true;
        }
        else if(id==R.id.action_help) {
            startActivity(new Intent(getApplication(), Help.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

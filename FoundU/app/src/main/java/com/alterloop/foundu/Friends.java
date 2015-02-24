package com.alterloop.foundu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alterloop.adapter.FriendsAdapter;
import com.alterloop.foldablelayout.UnfoldableView;
import com.alterloop.foldablelayout.shading.GlanceFoldShading;
import com.alterloop.util.FriendHelper;
import com.alterloop.util.SpannableBuilder;
import com.alterloop.util.Utility;
import com.alterloop.util.Views;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;


public class Friends extends ActionBarActivity {

    private ListView mListView;
    private View mListTouchInterceptor;
    private View mDetailsLayout;
    ProgressDialog FriendDetailFetchProgressDialog;

    private UnfoldableView mUnfoldableView;

    public boolean onSearchBarCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            try {
                setContentView(R.layout.activity_friends);

                //      refresh();
                //Asynch call to fetch the list from DB(so that it does not run on main UI thread)
                new Connection().execute();
                //getPhoneNumberListAndSaveInDB(UnfoldableDetailsActivity.this.getContentResolver());
                mListView = Views.find(this, R.id.list_view);
                mListView.setAdapter(new FriendsAdapter(this));

                mListTouchInterceptor = Views.find(this, R.id.touch_interceptor_view);
                mListTouchInterceptor.setClickable(false);

                mDetailsLayout = Views.find(this, R.id.details_layout);
                mDetailsLayout.setVisibility(View.INVISIBLE);

                mUnfoldableView = Views.find(this, R.id.unfoldable_view);

                Bitmap glance = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
                mUnfoldableView.setFoldShading(new GlanceFoldShading(this, glance));

            } catch (Exception ex) {
                String msg = ex.getMessage();
            }

            mUnfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
                @Override
                public void onUnfolding(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(true);
                    mDetailsLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onUnfolded(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(false);
                }

                @Override
                public void onFoldingBack(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(true);
                }

                @Override
                public void onFoldedBack(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(false);
                    mDetailsLayout.setVisibility(View.INVISIBLE);
                }
            });
        }
        catch(Exception ex)
        {
            String s=ex.getMessage();
        }
    }

    @Override
    public void onBackPressed() {
        if (mUnfoldableView != null && (mUnfoldableView.isUnfolded() || mUnfoldableView.isUnfolding())) {
            mUnfoldableView.foldBack();
        } else {
            super.onBackPressed();
        }
    }

    public void openDetails(View coverView, final FriendHelper frndInfo) {

        try {
            ImageView image = Views.find(mDetailsLayout, R.id.imageViewFriend);

            TextView title = Views.find(mDetailsLayout, R.id.textViewFriendName);
            TextView range=Views.find(mDetailsLayout, R.id.textViewFriendRange);
            TextView description = Views.find(mDetailsLayout, R.id.details_text);

                String imageId=frndInfo.getImageId();
            if(!imageId.equals("null") && image!=null) {
                Bitmap myBitmap = Utility.ConvertToImage(imageId);
                image.setImageBitmap(myBitmap);
            }
            else
            {
                image.setImageResource(R.drawable.defaultimg);
            }

            //Picasso.with(this).load(frndInfo.getImageId()).into(image);
            title.setText(frndInfo.getName());
            range.setText(frndInfo.getRange());
            SpannableBuilder builder = new SpannableBuilder(this);
            builder
                    .createStyle().setFont(Typeface.DEFAULT_BOLD).apply()
                    .append("Near: ")
                    .clearStyle()
                    .append(frndInfo.getAddress()).append("\n")
                    .createStyle().setFont(Typeface.DEFAULT_BOLD).apply()
                    .append("Last Found: ")
                    .clearStyle()
                    .append(frndInfo.getLastSeenOn()).append("\n")
                    .createStyle().setFont(Typeface.DEFAULT_BOLD).apply()
                    //.append("Within").append(": ")
                    //.clearStyle()
                    .createStyle().setFont(Typeface.DEFAULT_BOLD).apply()
                    .append("Phone: ")
                    .clearStyle()
                    .append(frndInfo.getPhoneNo()).append("\n")
                    .createStyle().setFont(Typeface.DEFAULT_BOLD).apply()
                    .append("Status: ")
                    .clearStyle()
                    .append(frndInfo.getStatus());
            description.setText(builder.build());
            mUnfoldableView.unfold(coverView, mDetailsLayout);

            //methods to perform communications modules start

            ImageButton call = (ImageButton) findViewById(R.id.Call);
            ImageButton chat = (ImageButton) findViewById(R.id.Chat);
            ImageButton meet = (ImageButton) findViewById(R.id.MeetFriend);
            ImageButton note = (ImageButton) findViewById(R.id.Note);

            //Call on Click
            call.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    callFriend(frndInfo.getPhoneNo());
                }
            });

            //Chat On Click

            chat.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    chatFriend(frndInfo.getPhoneNo());
                }
            });

            //Send a Note on Click

            note.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    sendNote();
                }
            });


            //Meet on Click

            meet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                   meetFriend(frndInfo.getLat(), frndInfo.getLng(), frndInfo.getName());
                    //xyz();
                }
            });

            //methods to perform communication ends

        } catch (Exception ex) {
            String a = ex.getMessage();
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

                SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

                search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

                search.setQueryHint("Area, Name, Range ... ");
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextChange(String query) {

                        // method to be call to submit query
                        clickSearchBar(query);
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
        } else if (id == R.id.action_help) {
            startActivity(new Intent(getApplication(), Help.class));
            return true;
        } else if (id == R.id.action_notifications) {
            startActivity(new Intent(getApplication(), Notifications.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Called when Refresh button in menu is clicked
    private void refresh() {
        //Stop the running service from here//MyService is your service class name
        //Service will only stop if it is already running.
        //stopService(new Intent(this, FoundService.class));

        //region Start :Pick contact List phone numbers and prepare a list
        FriendDetailFetchProgressDialog = ProgressDialog.show(Friends.this, "Please wait", "Loading...");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getPhoneNumberListAndSaveInDB(Friends.this.getContentResolver());
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
            String c = ex.toString();
        } finally {
            jsonArrayPhoneList = null;
            dictionary = null;
        }
    }


    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            getPhoneNumberListAndSaveInDB(Friends.this.getContentResolver());
            return null;
        }

    }


    void callFriend(String FriendPhone) {
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + FriendPhone));
        startActivity(i);
    }

    void chatFriend(String recipient) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("sms:" + recipient));
        smsIntent.putExtra("sms_body", "Hello");
        startActivity(smsIntent);

        // will be enhanced later
                /*
                Intent i = new Intent(FriendDetails.this,Chat.class);
                startActivity(i); */
    }

    void sendNote() {
        //yet to form
    }


    void meetFriend(String Lat, String Lng, String FriendName) {

        try {
            Double frndLat = Double.parseDouble(Lat);
            Double frndLong = Double.parseDouble(Lng);


            Intent meetIntent = new Intent(Friends.this,
                    Meet.class);
            meetIntent.putExtra("Latitude", frndLat);
            meetIntent.putExtra("Longitude", frndLong);
            meetIntent.putExtra("FriendName", FriendName);
            startActivity(meetIntent);
        }
        catch(Exception ex)
        {
            String a=ex.getMessage();
        }
    }



    // Method for Search Bar

    public void clickSearchBar(String query)
    {

        try {

            mListView.setAdapter(new FriendsAdapter(query, getApplicationContext()));

            mListTouchInterceptor = Views.find(this, R.id.touch_interceptor_view);
            mListTouchInterceptor.setClickable(false);

            mDetailsLayout = Views.find(this, R.id.details_layout);
            mDetailsLayout.setVisibility(View.INVISIBLE);

            mUnfoldableView = Views.find(this, R.id.unfoldable_view);

            Bitmap glance = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
            mUnfoldableView.setFoldShading(new GlanceFoldShading(this, glance));

            mUnfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
                @Override
                public void onUnfolding(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(true);
                    mDetailsLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onUnfolded(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(false);
                }

                @Override
                public void onFoldingBack(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(true);
                }

                @Override
                public void onFoldedBack(UnfoldableView unfoldableView) {
                    mListTouchInterceptor.setClickable(false);
                    mDetailsLayout.setVisibility(View.INVISIBLE);
                }
            });



        }

        catch(Exception ex)
        {
            String msg=ex.getMessage();
        }

    }



}



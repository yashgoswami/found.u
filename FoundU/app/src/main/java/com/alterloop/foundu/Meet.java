package com.alterloop.foundu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alterloop.util.DirectionsJSONParser;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import android.support.v4.app.Fragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Meet extends FragmentActivity {

    private GoogleMap map;
    ArrayList<LatLng> markerPoints;
    TextView meet_friendName;
    private int zoomLevel = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);
        //GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        meet_friendName = (TextView) findViewById(R.id.meet_friendName);

        //    Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_meet
        try {


            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null) {
                map.getUiSettings().setCompassEnabled(true);

                map.setTrafficEnabled(true);

                // Enable MyLocation Button in the Map
                map.setMyLocationEnabled(true);



                // Getting URL to the Google Directions API
                String srcAddress = null;
                Double srcLat = 0.0;
                Double srcLong = 0.0;
                SQLiteDatabase db = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
                // simplecursor adapter needs a column with name '_id'
                Cursor c = db.rawQuery("Select rowid _id, Address, Latitude, Longitude " +
                        "from RegisteredUserInfo", null);
                while (c.moveToNext()) {
                    srcAddress = c.getString(c.getColumnIndex("Address"));
                    srcLat = c.getDouble(c.getColumnIndex("Latitude"));
                    srcLong = c.getDouble(c.getColumnIndex("Longitude"));
                }
                db.close();
                //Toast.makeText(getApplicationContext(), srcAddress, Toast.LENGTH_SHORT).show();

                // getting destination address


                Double destLat = getIntent().getDoubleExtra("Latitude", 0.0);
                Double destLong = getIntent().getDoubleExtra("Longitude", 0.0);
                String frndName=getIntent().getStringExtra("FriendName");

                meet_friendName.setText("Locating: "+frndName );

                ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
                LatLng srcLoc = new LatLng(srcLat, srcLong);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(srcLoc, zoomLevel));
                LatLng destLoc = new LatLng(destLat, destLong);
                String origin = (srcLat + "," + srcLong);
                String dest = (destLat + "," + destLong);
                String url = getDirectionsUrl(origin, dest);
                markerPoints.add(srcLoc);
                markerPoints.add(destLoc);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(srcLoc);
                options.position(destLoc);

                //
                 //* For the start location, the color of marker is GREEN and
                 //* for the end location, the color of marker is RED.
                 //
                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                map.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng originPoint = markerPoints.get(0);
                    LatLng destPoint = markerPoints.get(1);


                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);

                }
            }

        }

        catch(NullPointerException e)
        {

        }
        catch(Exception ex)
        {
        }
    }




    private String getDirectionsUrl(String origin,String dest){

        // Origin of route


        String str_origin = "origin="+origin;

        // Destination of route
        String str_dest = "destination="+dest;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(11);
                lineOptions.color(Color.RED);
            }

            //tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meet, menu);
        return true;
    }



}
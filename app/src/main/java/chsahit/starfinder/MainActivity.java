package chsahit.starfinder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ActionBarActivity
        implements LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    double longitude = 0.0;
    double latitude = 0.0;
    double bolideLong = 0.0;
    double bolideLat = 0.0;
    GoogleApiClient client;
    LocationRequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } catch (Exception e) {
            //while(longitude==0.0)
            try {
                Log.w("starfinder", "sleep");
                Thread.sleep(5000);
                Log.w("starfinder","end sleep");
                //Log.w("starfinder",longitude+"");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        //Log.w("starfinder",longitude+"");
        try {
            getNearestShootingStar(longitude, latitude);
        } catch (Exception e) {/*darn*/}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * finds the shooting stae nearest to the user
     * @param longitude user's longitutde
     * @param lat user's latitude
     * @return a JSON with the results of the API Call
     * @throws JSONException if somthing goes wrong parsing. To quote the ever so specific docs "
     *      thrown "when things are amiss"
     */
    public void getNearestShootingStar(double longitude, double lat) throws JSONException {
        //api endpoint we are posting to w/params preformatted
        String address = "https://communities.socrata.com/resource/27eq-bd6d.json" +
                "?$order=distance_in_meters(%22POINT%20(" + longitude + "%20" + lat + ")%22,%20geolocation) ASC" +
                "&$limit=10&&$$app_token=4SmTmWiVWkhE7W839XKUeHurc";
        address = "https://communities.socrata.com/resource/27eq-bd6d.json?$order=distance_in_meters(%22POINT%20(" + longitude + "%20" + lat + ")%22,%20geolocation)%20ASC&$limit=1&&$$app_token=4SmTmWiVWkhE7W839XKUeHurc";
        //address = "https://google.com";01
        //String address = "https://google.com";
        String response = ""; //response contains the json
        //Log.w("starfinder", address);
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    new URL(address).openConnection(); //make request
            /**urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(30*1000);
            urlConnection.setDoInput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();**/
            Log.w("starfinder",urlConnection.getResponseMessage());
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line = "";
            Log.w("starfinder","startingloop");
            while ((line = rd.readLine()) != null) {
                response += line;
                Log.w("starfinder","reading");
            }
            rd.close();
            is.close();
//            String coords = new JSONObjectAr(response).getString("geolocation");
//            Log.w("starfinder", coords + " are coords");
//            Log.w("sf", "method end");
            bolideLong = Double.parseDouble(new JSONArray(response).getJSONObject(0).getJSONObject("geolocation").getJSONArray("coordinates").get(0).toString());
            bolideLat = Double.parseDouble(new JSONArray(response).getJSONObject(0).getJSONObject("geolocation").getJSONArray("coordinates").get(1).toString());
            Log.w("sf",bolideLong+"");
            Log.w("starfinder", response + " is response");
        } catch (Exception e) {
            //cry
            Log.w("starfinder",e.getMessage() + " was an error");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.w("starfinder","connected to API");
        request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(500);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void changeScreens(View v) {
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("long",longitude);
        intent.putExtra("lat",latitude);
        intent.putExtra("bolideLong",bolideLong);
        intent.putExtra("bolideLat",bolideLat);
        startActivity(intent);
    }
}

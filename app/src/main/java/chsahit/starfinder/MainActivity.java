package chsahit.starfinder;

import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            getNearestShootingStar(74, 40.7);
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
    public JSONObject getNearestShootingStar(double longitude, double lat) throws JSONException {
        //api endpoint we are posting to w/params preformatted
        String address = "https://communities.socrata.com/resource/27eq-bd6d.json" +
                "?$order=distance_in_meters(%22POINT%20(" + longitude + "%20" + lat + ")%22,%20geolocation) ASC" +
                "&$limit=1";
        String response = ""; //response contains the json
        Log.w("starfinder", address);
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    new URL(address).openConnection(); //make request

            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = rd.readLine()) != null) {
                response += line;
            }
            rd.close();
            is.close();
            Log.w("starfinder",response + " is response");
        } catch (Exception e) {
            //cry
            Log.w("starfinder",e.getMessage());
        }
        return new JSONObject(response); //return JSON
    }
}

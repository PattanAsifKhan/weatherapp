package asif.asifweatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    static TextView Locationview;
    static TextView Temperatureview;
    LocationManager locationManager;

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            function(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Locationview = (TextView) findViewById(R.id.Location);
        Temperatureview = (TextView) findViewById(R.id.Temperature);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,50,locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10,50,locationListener);
    }

    public void function(Location location) {
        final String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(location.getLatitude()) + "&lon=" + String.valueOf(location.getLongitude()) + "&APPID=c342e0dbbc053610b244670ed21d0b37&units=metric";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("onResponse: ", response.toString());
                    parsejson(response.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void parsejson(String s) throws IOException {
        JsonReader reader=new JsonReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
        reader.beginObject();
        while (reader.hasNext()){
            String name=reader.nextName();
            if (name.equals("main")) {
                reader.beginObject();
                while (reader.hasNext()){
                    String name1=reader.nextName();
                    if(name1.equals("temp")){
                        Temperatureview.setText(reader.nextString());
                    } else{
                        reader.skipValue();;

                    }
                }
                reader.endObject();

            } else  if(name.equals("name")){
                Locationview.setText(reader.nextString());
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,50,locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10,50,locationListener);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}


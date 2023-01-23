package com.assignment.handy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.assignment.handy.databinding.ActivityMain2Binding;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {
    int u_id;
    double lat,lang;
    public int get_u_id(){return u_id;}
    private LocationRequest locationRequest;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain2.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            u_id = bundle.getInt("u_id");
        }



        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        getCurrentLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }
public void move_maps() throws IOException {
    set_location_API locapi = new set_location_API();
    locapi.execute(String.valueOf(lat),String.valueOf(lang),String.valueOf(u_id));
    Geocoder gc=new Geocoder(this, Locale.getDefault());
    List<Address> my_add;
    my_add = gc.getFromLocation(lat,lang,1);
    if(my_add.size()!=0)
    {
        String city = my_add.get(0).getAddressLine(0);

        TextView tv = (TextView) findViewById(R.id.user_location);
        tv.setText(city);
    }

}
    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(Main2Activity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(Main2Activity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        lang=longitude;
                                        lat=latitude;
                                        try {
                                            move_maps();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        // AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(Main2Activity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Main2Activity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

        private boolean isGPSEnabled () {
            LocationManager locationManager = null;
            boolean isEnabled = false;

            if (locationManager == null) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }

            isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isEnabled;
        }

public void logout(View v)
{
    Intent login_screen = new Intent(this, MainActivity.class);
    startActivity(login_screen);
}

    public void ge_preappoint(View v)
    {
        Intent prereq_screen = new Intent(this, prev_requests.class);
        prereq_screen.putExtra("u_id",u_id);
        startActivity(prereq_screen);
    }

public void go_changepass(View v)
{
    Intent changepass_screen = new Intent(this, Changepassword.class);
    changepass_screen.putExtra("u_id",u_id);
    startActivity(changepass_screen);
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void go_tag_elect(View v){

        Intent tag_screen = new Intent(this, choose_tags.class);
        tag_screen.putExtra("job","Electricity");
        tag_screen.putExtra("u_id",u_id);
        startActivity(tag_screen);
    }
    public void go_tag_plum(View v){

        Intent tag_screen = new Intent(this, choose_tags.class);
        tag_screen.putExtra("job","Plumbing");
        tag_screen.putExtra("u_id",u_id);
        startActivity(tag_screen);
    }
    public void go_tag_carp(View v){

        Intent tag_screen = new Intent(this, choose_tags.class);
        tag_screen.putExtra("job","Carpentry");
        tag_screen.putExtra("u_id",u_id);
        startActivity(tag_screen);
    }
    public void go_tag_gard(View v){

        Intent tag_screen = new Intent(this, choose_tags.class);
        tag_screen.putExtra("job","Gardining");
        tag_screen.putExtra("u_id",u_id);
        startActivity(tag_screen);
    }
    public void go_tag_paint(View v){

        Intent tag_screen = new Intent(this, choose_tags.class);
        tag_screen.putExtra("job","Painting");
        tag_screen.putExtra("u_id",u_id);
        startActivity(tag_screen);
    }

    public void go_chat(View v)
    {
        get_prov_API provbyname = new get_prov_API();
        TextView tv = (TextView) v.findViewById(R.id.app_name);
        provbyname.execute(tv.getText().toString());
    }
public void start_chat(int p_id)
{
    Intent chat_screen = new Intent(this, chat_or_cancel.class);
    chat_screen.putExtra("p_id",p_id);
    chat_screen.putExtra("u_id",u_id);
    startActivity(chat_screen);
}



    private class set_location_API extends AsyncTask<String, String, Void> {
        InputStream in;
        JSONArray object;
        int id;
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/setlocation_u?lat="+strings[0]+"&lon="+strings[1]+"&u_id="+strings[2]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);

            }  catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class get_prov_API extends AsyncTask<String, String, Integer> {
        InputStream in;
        JSONArray object;
        int id;
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/getprov_name?name="+strings[0]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                id = object.getJSONObject(0).getInt("p_id");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return id;
        }
        @Override
        protected void onPostExecute(Integer ja) {
        start_chat(ja);
        }
    }

}
package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class User_location_chat extends AppCompatActivity {
int p_id,u_id;
double lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_chat);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            p_id = bundle.getInt("p_id");
            u_id = bundle.getInt("u_id");
            lat = bundle.getDouble("lat");
            lon = bundle.getDouble("lon");

        }

        Geocoder gc=new Geocoder(this, Locale.getDefault());
        List<Address> my_add;
        try {
            my_add = gc.getFromLocation(lat,lon,1);
            if(my_add.size()!=0)
            {
                String address = my_add.get(0).getAddressLine(0);

                TextView tv = (TextView) findViewById(R.id.apps_user_location);
                tv.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void go_to_maps(View v)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?z=12&t=p&q=loc:"+lat+"+"+lon)));
    }
    public void prov_go_chat(View v)
    {
        Intent chat_screen = new Intent(this, chat_provider.class);
        chat_screen.putExtra("p_id",p_id);
        chat_screen.putExtra("u_id",u_id);
        startActivity(chat_screen);
    }
}
package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class Provider_home extends AppCompatActivity {
    int p_id;
    String p_name, p_job;
    float p_rating;
    ArrayList<Appointment> apps = new ArrayList<Appointment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            p_id = bundle.getInt("p_id");
            p_name = bundle.getString("p_name");
            p_job = bundle.getString("p_job");
            p_rating = bundle.getFloat("p_rate");

        }
        Upcoming_apps_API upcoming = new Upcoming_apps_API();
        upcoming.execute(String.valueOf(p_id));
        TextView tv_name = (TextView) findViewById(R.id.prov_name);
        TextView tv_job = (TextView) findViewById(R.id.prov_job);
        RatingBar rb = (RatingBar) findViewById(R.id.prov_rate);
        tv_name.setText(p_name);
        tv_job.setText(p_job);
        rb.setRating(p_rating);

    }
    class  Appointment{
         public String name,date,hour;
         public double lat,lon;
         public int u_id;
         Appointment(String name,String date,String hour,double lat,double lon,int u_id)
         {
             this.name = name;
             this.date = date;
             this.hour = hour;
             this.lat = lat;
             this.lon = lon;
             this.u_id = u_id;
         }
        public String get_hour(){
            if(hour.equals("9"))
            {
                return "9Am - 11AM";
            }
            if(hour.equals("11"))
            {
                return "11Am - 1PM";
            }
            if(hour.equals("1"))
            {
                return "1PM - 3PM";
            }
            if(hour.equals("3"))
            {
                return "3PM - 5PM";
            }

            return "no string";
        }

    }


    public void adapt_prov(ArrayList<Appointment> apps) {
        this.apps = apps;
        Adapter_appointments adap = new Adapter_appointments(this, apps);
        ListView lv = (ListView) findViewById(R.id.coming_apps_list);
        lv.setAdapter(adap);

    }
    public void prov_go_settings(View v)
    {
        Intent setting_screen = new Intent(this, Prov_settings.class);
        setting_screen.putExtra("p_id",p_id);

        startActivity(setting_screen);
    }

    private class Adapter_appointments extends ArrayAdapter<Appointment> {
        public Adapter_appointments(Context context, ArrayList<Appointment> apps) {
            super(context, R.layout.upcoming_apps_list, apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Appointment apps = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.upcoming_apps_list, parent, false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.upcoming_app_name);
            TextView date = (TextView) convertView.findViewById(R.id.upcoming_app_date);
            TextView hour = (TextView) convertView.findViewById(R.id.upcoming_app_hours);
            name.setText(apps.name);
            date.setText(apps.date);
            hour.setText(apps.get_hour());
            return convertView;
        }
    }
    public void chat_location(View v)
    {
        TextView tv = (TextView) v.findViewById(R.id.upcoming_app_name);
        String name = tv.getText().toString();
        Intent loc_chat_screen = new Intent(this, User_location_chat.class);
        for(int i=0;i<apps.size();i++)
        {
            if(name.equals(apps.get(i).name))
            {
                loc_chat_screen.putExtra("p_id",p_id);
                loc_chat_screen.putExtra("u_id",apps.get(i).u_id);
                loc_chat_screen.putExtra("lat",apps.get(i).lat);
                loc_chat_screen.putExtra("lon",apps.get(i).lon);
                startActivity(loc_chat_screen);
            }
        }
    }

    private class Upcoming_apps_API extends AsyncTask<String, String, JSONArray> {
        InputStream in;
        JSONArray object, u_object;
        Integer signcode = 0;
        ArrayList<Appointment> apps_arr = new ArrayList<Appointment>();

        @SuppressLint("WrongThread")
        @Override
        protected JSONArray doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/getprov_coming_apps?id=" + strings[0]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                for (int i = 0; i < object.length(); i++) {
                    url = new String("http://10.0.2.2:3000/getuser_id?id=" + String.valueOf(object.getJSONObject(i).getInt("u_id")));

                    client = new DefaultHttpClient();
                    request = new HttpGet();
                    request.setURI(new URI(url));

                    response = client.execute(request);
                     in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                     inputLine = in.readLine();
                    u_object = new JSONArray(inputLine);
                    apps_arr.add(new Appointment(u_object.getJSONObject(0).getString("u_name"),object.getJSONObject(i).getString("day")+"/"+String.valueOf(Integer.parseInt(object.getJSONObject(i).getString("month"))+1),String.valueOf(object.getJSONObject(i).getInt("hour")),object.getJSONObject(i).getDouble("lat"),object.getJSONObject(i).getDouble("lon"),object.getJSONObject(i).getInt("u_id")));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return object;
        }

        @Override
        protected void onPostExecute(JSONArray ja) {
            adapt_prov(apps_arr);

        }
    }
}

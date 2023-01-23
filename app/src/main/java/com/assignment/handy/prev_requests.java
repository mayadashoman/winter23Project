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

import com.assignment.handy.ui.gallery.GalleryFragment;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class prev_requests extends AppCompatActivity {
int u_id;
    ArrayList<appointments> apps = new ArrayList<appointments>();
get_appoint_api getapp = new get_appoint_api();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_requests);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            u_id = bundle.getInt("u_id");
        }
        getapp.execute(String.valueOf(u_id));

    }

public void set_rate(View v)
{
    TextView tv= (TextView) v.findViewById(R.id.preapp_name);
    String name = tv.getText().toString();
    for(int i=0;i<apps.size();i++)
    {
        if(apps.get(i).get_name().equals(name))
        {
            Intent rate_screen = new Intent(this, rating_prov.class);
            rate_screen.putExtra("u_id",u_id);
            rate_screen.putExtra("p_name",name);
            rate_screen.putExtra("p_id",apps.get(i).get_pid());
            startActivity(rate_screen);
        }
    }

}



    class appointments{
        String p_name;
        int day,month,hour,p_id;
        appointments(String p_name,int day,int month,int hour,int p_id)
        {
            this.p_name = p_name;
            this.day = day;
            this.month = month;
            this.hour = hour;
            this.p_id = p_id;
        }
        public String get_name(){return p_name;}
        public int get_pid(){return p_id;}
        public String get_hour(){
            if(hour == 9)
            {
                return "9Am - 11AM";
            }
            if(hour == 11)
            {
                return "11Am - 1PM";
            }
            if(hour == 1)
            {
                return "1PM - 3PM";
            }
            if(hour ==3)
            {
                return "3PM - 5PM";
            }

            return "no string";
        }
        public String get_date(){return String.valueOf(day)+" / "+String.valueOf(month+1);}

    }






    public void adapt_apps(ArrayList<appointments> apps){
      this.apps = apps;
      Adapter_myappoint adap = new Adapter_myappoint(this, apps);
        ListView lv = (ListView) findViewById(R.id.user_pre_appoint_list);
        lv.setAdapter(adap);


    }



    private class Adapter_myappoint extends ArrayAdapter<appointments>
    {
        public Adapter_myappoint(Context context, ArrayList<appointments> apps)
        {
            super(context,R.layout.my_pre_appointments,apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
           appointments prov = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_pre_appointments,parent,false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.preapp_name);
            TextView date = (TextView) convertView.findViewById(R.id.preapp_date);
            name.setText(prov.get_name());
            date.setText(prov.get_date());
            return convertView;
        }
    }





    private class get_appoint_api extends AsyncTask<String, String, ArrayList<appointments>> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected ArrayList<appointments> doInBackground(String... strings) {
            String myData = "null";
            ArrayList<appointments> apps = new ArrayList<appointments>();
            try {
                String url = new String("http://10.0.2.2:3000/getpre_app_user?id="+strings[0]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                JSONArray ja;

                for(int i = 0;i<object.length();i++)
                {
                    url = new String("http://10.0.2.2:3000/Provlist?tag="+object.getJSONObject(i).getString("p_id"));

                    client = new DefaultHttpClient();
                    request = new HttpGet();
                    request.setURI(new URI(url));

                    response = client.execute(request);
                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    inputLine = in.readLine();
                    ja = new JSONArray(inputLine);
                    apps.add(new appointments(ja.getJSONObject(0).getString("p_name"),object.getJSONObject(i).getInt("day"),object.getJSONObject(i).getInt("month"),object.getJSONObject(i).getInt("hour"),ja.getJSONObject(0).getInt("p_id")));

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
            return apps;
        }
        @Override
        protected void onPostExecute(ArrayList<appointments> arr) {
            adapt_apps(arr);
        }
    }


}
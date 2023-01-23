package com.assignment.handy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class Book_screen extends AppCompatActivity{
tags_API tag_ap = new tags_API();
ArrayList<provider> prov = new ArrayList<provider>();
String Job="";
int tag_id,u_id;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_screen);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tag_id = bundle.getInt("tag");
             u_id = bundle.getInt("u_id");
        }
        tag_ap.execute(String.valueOf(tag_id),String.valueOf(u_id));
        /////////////////////////////////////////////////////////

    }
    public void go_time(View v)
    {
        TextView tag_text = (TextView) v.findViewById(R.id.p_name);
        String text = tag_text.getText().toString();
        Intent time_screen = new Intent(this, time_book.class);
        for(int i=0;i<prov.size();i++)
        {
            if(prov.get(i).get_name().equals(text))
            {
                time_screen.putExtra("p_id",prov.get(i).get_id());
                time_screen.putExtra("u_id",u_id);
                break;
            }
        }
        startActivity(time_screen);
    }

class provider {
  String name=new String();
  float rate;
  int p_id;
  provider(String name,float rate,int id)
  {
      this.p_id = id;
      this.name = name;
      this.rate = rate;
  }
  public int get_id(){return p_id;}
  public String get_name(){return name;}
  public float get_rate(){return rate;}


};

public void adapt_prov(ArrayList<provider> prov){
    this.prov = prov;
    Adapter_providers adap = new Adapter_providers(this, prov);
    ListView lv = (ListView) findViewById(R.id.prov_list);
    lv.setAdapter(adap);

}




    private class Adapter_providers extends ArrayAdapter<provider>
    {
        public Adapter_providers(Context context, ArrayList<provider> provs)
        {
            super(context,R.layout.provider_list,provs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            provider prov = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.provider_list,parent,false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.p_name);
            RatingBar rate = (RatingBar) convertView.findViewById(R.id.ratingBar);
            name.setText(prov.get_name());
            rate.setRating(prov.get_rate());
            return convertView;
        }
    }




    private class tags_API extends AsyncTask<String, String, JSONArray> {
        InputStream in;
        JSONArray object,u_object;
        Integer signcode = 0;
        ArrayList<provider> prov_arr = new ArrayList<provider>();
        @SuppressLint("WrongThread")
        @Override
        protected JSONArray doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/link_tag?tag="+strings[0]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);

                 url = new String("http://10.0.2.2:3000/getuser_id?id="+strings[1]);

                 client = new DefaultHttpClient();
                 request = new HttpGet();
                request.setURI(new URI(url));

                 response = client.execute(request);
                 in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                 inputLine = in.readLine();
                 u_object = new JSONArray(inputLine);
                 double lat,lang;
                 lat = u_object.getJSONObject(0).getDouble("lat");
                 lang = u_object.getJSONObject(0).getDouble("lon");
                 Location startPoint;
                 Location endPoint;
                LocationManager lc;
                JSONArray prov;
                for(int i=0;i<object.length();i++)
                {
                     url = new String("http://10.0.2.2:3000/Provlist?tag="+object.getJSONObject(i).getString("p_id"));

                     client = new DefaultHttpClient();
                     request = new HttpGet();
                     request.setURI(new URI(url));

                     response = client.execute(request);
                     in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                     inputLine = in.readLine();
                     prov=new JSONArray(inputLine);


                     startPoint=new Location("locationA");
                    startPoint.setLatitude(lat);
                    startPoint.setLongitude(lang);

                     endPoint=new Location("locationA");
                    endPoint.setLatitude(prov.getJSONObject(0).getDouble("lat"));
                    endPoint.setLongitude(prov.getJSONObject(0).getDouble("lon"));

                    double distance=startPoint.distanceTo(endPoint);
                     if(distance/1000.0<=16.0)
                        prov_arr.add(new provider(prov.getJSONObject(0).getString("p_name"), (float) prov.getJSONObject(0).getDouble("rating"),prov.getJSONObject(0).getInt("p_id")));

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
            adapt_prov(prov_arr);

        }
    }



}
package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
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

public class rating_prov extends AppCompatActivity {
    int u_id,p_id;
    String name=new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_prov);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            u_id = bundle.getInt("u_id");
            p_id = bundle.getInt("p_id");
            name = bundle.getString("p_name");
        }
        TextView tv =(TextView) findViewById(R.id.rate_pname);
        tv.setText(name);
    }
    public void set_rate(View v){
        RatingBar rb = (RatingBar) findViewById(R.id.pre_ratingBar);
        set_rating_API sr = new set_rating_API();

        sr.execute(String.valueOf(u_id),String.valueOf(p_id),String.valueOf(rb.getRating()));
    }

    private class set_rating_API extends AsyncTask<String, String, Void> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... strings) {
            String myData = "null";
            ArrayList<prev_requests.appointments> apps = new ArrayList<prev_requests.appointments>();
            try {
                String url = new String("http://10.0.2.2:3000/getup_rating?u_id=" + strings[0] + "&p_id=" + strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                if (object.length()!=0) {
                    url = new String("http://10.0.2.2:3000/up_rate?u_id=" + strings[0] + "&p_id=" + strings[1] + "&rate=" + strings[2]);
                }
                else
                {
                    url = new String("http://10.0.2.2:3000/newup_rating?u_id=" + strings[0] + "&p_id=" + strings[1] + "&rate=" + strings[2]);
                }

                client = new DefaultHttpClient();
                request = new HttpGet();
                request.setURI(new URI(url));
                response = client.execute(request);


                 url = new String("http://10.0.2.2:3000/getp_rating?p_id=" + strings[1]);

                 client = new DefaultHttpClient();
                 request = new HttpGet();
                request.setURI(new URI(url));
                 response = client.execute(request);
                 in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                 inputLine = in.readLine();
                object = new JSONArray(inputLine);
                double total_rate=0.0;
                for(int i = 0;i<object.length();i++)
                {
                    total_rate+=object.getJSONObject(i).getDouble("rate");

                }
                total_rate = total_rate/(double) object.length();


                url = new String("http://10.0.2.2:3000/setp_rate?p_id=" + strings[1]+"&rate="+String.valueOf(total_rate));

                client = new DefaultHttpClient();
                request = new HttpGet();
                request.setURI(new URI(url));
                response = client.execute(request);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;



        }
        @Override
        protected void onPostExecute(Void vo) {
            Toast toast;
            toast = Toast.makeText(getApplicationContext(), "Rating Updated", Toast.LENGTH_LONG);
            toast.show();

        }

    }
}
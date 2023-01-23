package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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

public class chat_or_cancel extends AppCompatActivity {
    int p_id,u_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_or_cancel);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            p_id = bundle.getInt("p_id");
            u_id = bundle.getInt("u_id");
        }

    }

    public void chat_start(View v)
    {
        Intent chat_screen = new Intent(this, Chat.class);
        chat_screen.putExtra("p_id",p_id);
        chat_screen.putExtra("u_id",u_id);
        startActivity(chat_screen);
    }
    public void Remove_appoint(View v)
    {
        remove_appoint_API re = new remove_appoint_API();
        re.execute(String.valueOf(u_id),String.valueOf(p_id));
        Intent main_screen = new Intent(this, Main2Activity.class);
        main_screen.putExtra("p_id",p_id);
        main_screen.putExtra("u_id",u_id);
        startActivity(main_screen);
    }





    private class remove_appoint_API extends AsyncTask<String, String, Void> {
        InputStream in;
        JSONArray object;

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/getapp_userprov?u_id="+strings[0]+"&p_id="+strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                String day = object.getJSONObject(0).getString("day");
                String month = object.getJSONObject(0).getString("month");
                String hour = object.getJSONObject(0).getString("hour");

                 url = new String("http://10.0.2.2:3000/newpre_app?u_id="+strings[0]+"&p_id="+strings[1]+"&day="+day+"&month="+month+"&hour="+hour);

                 client = new DefaultHttpClient();
                 request = new HttpGet();
                request.setURI(new URI(url));

                 response = client.execute(request);


                 url = new String("http://10.0.2.2:3000/remove_appoint?u_id="+strings[0]+"&p_id="+strings[1]);

                 client = new DefaultHttpClient();
                 request = new HttpGet();
                request.setURI(new URI(url));

                 response = client.execute(request);

                 url = new String("http://10.0.2.2:3000/remove_chat?u_id="+strings[0]+"&p_id="+strings[1]);

                 client = new DefaultHttpClient();
                 request = new HttpGet();
                 request.setURI(new URI(url));
                 response = client.execute(request);

            }
            catch (MalformedURLException e) {
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

    }
}
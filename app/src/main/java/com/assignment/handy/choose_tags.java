package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class choose_tags extends AppCompatActivity {
String job=new String();
int u_id;
ArrayList<Integer>tag_id=new ArrayList<Integer>();
ArrayList<String>tags = new ArrayList<String>();
tags_API ta = new tags_API();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tags);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            job = bundle.getString("job");
            u_id = bundle.getInt("u_id");
        }
        ta.execute(job);
    }

    public void go_appoint(View v)
    {
        Button tag_text = (Button) v;
        String text = tag_text.getText().toString();
        Intent tag_screen = new Intent(this, Book_screen.class);
        for(int i=0;i<tags.size();i++)
        {
            if(tags.get(i).equals(text))
            {
                tag_screen.putExtra("tag",tag_id.get(i));
                tag_screen.putExtra("u_id",u_id);
                break;
            }
        }
        startActivity(tag_screen);
    }

private void adapt(ArrayList<String>tags,ArrayList<Integer>tag_id)
{
    this.tag_id = tag_id;
    this.tags = tags;
    Adapter_tags adap = new Adapter_tags(this, tags);
    ListView lv = (ListView) findViewById(R.id.tag_list);
    lv.setAdapter(adap);
}

    private class Adapter_tags extends ArrayAdapter<String>
    {
        public Adapter_tags(Context context, ArrayList<String> tags)
        {
            super(context,R.layout.tag_list_layout,tags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            String tag = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_list_layout,parent,false);
            }
            Button name = (Button) convertView.findViewById(R.id.t_name);
            name.setText(tag);
            return convertView;
        }
    }




    private class tags_API extends AsyncTask<String, String, JSONArray> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected JSONArray doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/taglist?job="+strings[0]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);

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
            ArrayList<Integer> tag_id=new ArrayList<Integer>();
            ArrayList<String> tags=new ArrayList<String>();
            for(int i=0;i<ja.length();i++)
            {
                try {
                    tag_id.add(ja.getJSONObject(i).getInt("t_id"));
                    tags.add(ja.getJSONObject(i).getString("t_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapt(tags,tag_id);
        }
    }
}
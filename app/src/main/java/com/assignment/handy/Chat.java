package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class Chat extends AppCompatActivity {
int prov_id,u_id;

boolean empty=true;
Chat_API ch_api = new Chat_API();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            prov_id = bundle.getInt("p_id");
            u_id = bundle.getInt("u_id");
        }

    ch_api.execute(String.valueOf(u_id),String.valueOf(prov_id));
    }
    class chat{
       public String body;
       public int user_sent;
        chat(String body,int user_sent)
        {
            this.body = body;
            this.user_sent = user_sent;
        }

    }

    Adapter_chat adap;
    public void adapt_chat(ArrayList<chat> logs)

    {
        ListView lv = (ListView) findViewById(R.id.Chat_list);
         adap = new Adapter_chat(this, logs);
        lv.setAdapter(adap);
    }


    public void send_logs(View v)
    {

        send_Chat_API send_ch = new send_Chat_API();
        TextView tv = (TextView) findViewById(R.id.text_chat);
        String log = tv.getText().toString();
        ListView lv = (ListView) findViewById(R.id.Chat_list);
        if(!log.equals("")) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adap.add(new chat(log, 1));
                    send_ch.execute(String.valueOf(u_id), String.valueOf(prov_id), log);
                }
            }, 100);

        }
        }





    private class Adapter_chat extends ArrayAdapter<chat>
    {
        public Adapter_chat(Context context, ArrayList<chat> logs)
        {
            super(context,R.layout.list_chat,logs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            chat ch = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_chat,parent,false);
            }
            LinearLayout ll = (LinearLayout)  convertView.findViewById(R.id.layout_chat_list);
            LinearLayout ll2 = (LinearLayout)  convertView.findViewById(R.id.layout_gchat_container_me);
            TextView name = (TextView) convertView.findViewById(R.id.text_chatbox1);
            name.setText(ch.body);
            if(ch.user_sent==0)
            {
                ll.setGravity(Gravity.LEFT);
                ll.setBackgroundColor(getResources().getColor(R.color.white));
                ll2.setBackgroundColor(getResources().getColor(R.color.white));
                name.setBackgroundColor(getResources().getColor(R.color.white));
                name.setTextColor(getResources().getColor(R.color.purple_200));
            }
            return convertView;
        }
    }



    private class send_Chat_API extends AsyncTask<String, String, Void> {
        InputStream in;
        JSONArray object;
        boolean nine=true;
        boolean eleven=true;
        boolean one=true;
        boolean three=true;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/sendchat_u?u_id="+strings[0]+"&p_id="+strings[1]+"&body="+strings[2].replace(" ","%20"));

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            // go_to_home();

        }
    }


    private class Chat_API extends AsyncTask<String, String, ArrayList<chat>> {
        InputStream in;
        JSONArray object;
        boolean nine=true;
        boolean eleven=true;
        boolean one=true;
        boolean three=true;
        Integer signcode = 0;
        ArrayList<chat> chat_log=new ArrayList<chat>();
        @SuppressLint("WrongThread")
        @Override
        protected ArrayList<chat> doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/getchat?u_id="+strings[0]+"&p_id="+strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);

                for(int i=0;i<object.length();i++)
                {
                    chat_log.add(new chat(object.getJSONObject(i).getString("body"),object.getJSONObject(i).getInt("user_sent")));

                }

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
            return chat_log;
        }
        @Override
        protected void onPostExecute(ArrayList<chat> log) {
           adapt_chat(log);
        }
    }

}
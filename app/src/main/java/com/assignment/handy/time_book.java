package com.assignment.handy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class time_book extends AppCompatActivity {
    Button btn;
    set_appoint_API set_appoint = new set_appoint_API();
    boolean choosen = false;
    appoint_API app_api =new appoint_API();
    Integer year,month,day;
    int prov_id,u_id;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_book);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            prov_id = bundle.getInt("p_id");
            u_id = bundle.getInt("u_id");
        }
        //// Get date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        day=mDay;
        month=mMonth;
        year=mYear;
        // date picker dialog
        DatePickerDialog datepick = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        setdate(year,monthOfYear,dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datepick.getDatePicker().setMinDate(c.getTimeInMillis());
        c.add(Calendar.DAY_OF_MONTH,7);
        datepick.getDatePicker().setMaxDate(c.getTimeInMillis());
//        datepick.getDatePicker().setBackground(getDrawable(R.drawable.web_photo_editor));
        datepick.show();
    }
    public void set_appointment(View v){
        if(choosen) {
            if (btn.getText().toString().equals("9AM - 11AM"))
                set_appoint.execute(String.valueOf(day), String.valueOf(month), "9", String.valueOf(u_id), String.valueOf(prov_id));
            else if (btn.getText().toString().equals("11AM - 1PM"))
                set_appoint.execute(String.valueOf(day), String.valueOf(month), "11", String.valueOf(u_id), String.valueOf(prov_id));
            else if (btn.getText().toString().equals("1PM - 3PM"))
                set_appoint.execute(String.valueOf(day), String.valueOf(month), "1", String.valueOf(u_id), String.valueOf(prov_id));
            else if (btn.getText().toString().equals("3PM - 5PM"))
                set_appoint.execute(String.valueOf(day), String.valueOf(month), "3", String.valueOf(u_id), String.valueOf(prov_id));

        }
        }
    public void on_click_time(View v){
        v.setEnabled(false);
        if(choosen)
        {
            btn.setBackgroundColor(getResources().getColor(R.color.white));
            btn.setTextColor(getResources().getColor(R.color.purple_200));
        }
        choosen = true;
        btn = (Button) v;
        btn.setBackgroundColor(getResources().getColor(R.color.purple_200));
        btn.setTextColor(getResources().getColor(R.color.white));
        v.setEnabled(true);
    }


    public void setdate(int year,int month,int day)
    {
        this.year=year;
        this.day=day;
        this.month=month;
        app_api.execute(String.valueOf(prov_id),String.valueOf(day));
    }

    public void adapt_appoint(ArrayList<String> slots){
       // this.prov = prov;
        Adapter_appoint adap = new Adapter_appoint(this, slots);
        ListView lv = (ListView) findViewById(R.id.time_list);
        lv.setAdapter(adap);

    }




    private class Adapter_appoint extends ArrayAdapter<String>
    {
        public Adapter_appoint(Context context, ArrayList<String> slots)
        {
            super(context,R.layout.timeslots_list,slots);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            String prov = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.timeslots_list,parent,false);
            }
            Button name = (Button) convertView.findViewById(R.id.time_text);
            name.setText(prov);
            return convertView;
        }
    }
    public void go_to_home(){
//        Intent home_screen = new Intent(this, Main2Activity.class);
//        home_screen.putExtra("u_id",u_id);
//        startActivity(home_screen);
          finish();
    }
    private class set_appoint_API extends AsyncTask<String, String, Void> {
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
                String url = new String("http://10.0.2.2:3000/getuser_id?id="+strings[3]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);

                String lat=new String(),lon=new String();
                lat = String.valueOf(object.getJSONObject(0).getDouble("lat"));
                lon = String.valueOf(object.getJSONObject(0).getDouble("lon"));




                url = new String("http://10.0.2.2:3000/newapp?day="+strings[0]+"&month="+strings[1]+"&hour="+strings[2]+"&u_id="+strings[3]+"&p_id="+strings[4]+"&lat="+lat+"&lon="+lon);

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
        @Override
        protected void onPostExecute(Void v) {
            go_to_home();

        }
    }




    private class create_appoint_API extends AsyncTask<String, String, JSONArray> {
        InputStream in;
        JSONArray object;
        boolean nine=true;
        boolean eleven=true;
        boolean one=true;
        boolean three=true;
        Integer signcode = 0;
        ArrayList<Book_screen.provider> prov_arr = new ArrayList<Book_screen.provider>();
        @SuppressLint("WrongThread")
        @Override
        protected JSONArray doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/getapp_prov?id="+strings[0]+"&day="+strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                for(int i=0;i<object.length();i++)
                {
                    if(object.getJSONObject(i).getString("hour").equals("9"))
                    {
                        nine =false;
                    }
                    else
                    if(object.getJSONObject(i).getString("hour").equals("11"))
                    {
                        eleven =false;
                    }
                    else
                    if(object.getJSONObject(i).getString("hour").equals("1"))
                    {
                        one =false;
                    }
                    else
                    if(object.getJSONObject(i).getString("hour").equals("3"))
                    {
                        three =false;
                    }

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
            ArrayList<String> slots = new ArrayList<String>();
            if(nine)
            {
                slots.add("9AM - 11AM");
            }
            if(eleven)
            {
                slots.add("11AM - 1PM");
            }
            if(one)
            {
                slots.add("1PM - 3PM");
            }
            if(three)
            {
                slots.add("3PM - 5PM");
            }
            adapt_appoint(slots);

        }
    }

    private class appoint_API extends AsyncTask<String, String, JSONArray> {
        InputStream in;
        JSONArray object;
        boolean nine=true;
        boolean eleven=true;
        boolean one=true;
        boolean three=true;
        Integer signcode = 0;
        ArrayList<Book_screen.provider> prov_arr = new ArrayList<Book_screen.provider>();
        @SuppressLint("WrongThread")
        @Override
        protected JSONArray doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/getapp_prov?id="+strings[0]+"&day="+strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                for(int i=0;i<object.length();i++)
                {
                    if(object.getJSONObject(i).getString("hour").equals("9"))
                    {
                        nine =false;
                    }
                    else
                    if(object.getJSONObject(i).getString("hour").equals("11"))
                    {
                        eleven =false;
                    }
                    else
                    if(object.getJSONObject(i).getString("hour").equals("1"))
                    {
                        one =false;
                    }
                    else
                    if(object.getJSONObject(i).getString("hour").equals("3"))
                    {
                        three =false;
                    }

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
            ArrayList<String> slots = new ArrayList<String>();
            if(nine)
            {
                slots.add("9AM - 11AM");
            }
            if(eleven)
            {
                slots.add("11AM - 1PM");
            }
            if(one)
            {
                slots.add("1PM - 3PM");
            }
            if(three)
            {
                slots.add("3PM - 5PM");
            }
            adapt_appoint(slots);

        }
    }

}
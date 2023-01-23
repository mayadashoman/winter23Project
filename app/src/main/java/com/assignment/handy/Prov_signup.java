package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Prov_signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prov_signup);
    }
    public static boolean validatePhone(String phone)
    {
        return phone.matches("^\\d{11}$");
    }
    public void sign_up(View v)
    {
        Add_req_API newprov = new Add_req_API();
        EditText mail = (EditText) findViewById(R.id.prov_email);
        EditText username = (EditText) findViewById(R.id.prov_Name);
        EditText phone = (EditText) findViewById(R.id.phone);
        EditText city = (EditText) findViewById(R.id.city);

        String email = mail.getText().toString();
        String name = username.getText().toString();
        String phonenum = phone.getText().toString();
        String prov_city = city.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show();
        }
        else
        if(!validatePhone(phonenum))
        {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }
        else
            newprov.execute(name,email,phonenum,prov_city);








    }


    public void go_main()
    {
        Toast.makeText(this, "Request submitted!", Toast.LENGTH_SHORT).show();
        Intent login_screen = new Intent(this, MainActivity.class);
        startActivity(login_screen);
    }


    private class Add_req_API extends AsyncTask<String, String, ArrayList<prev_requests.appointments>> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected ArrayList<prev_requests.appointments> doInBackground(String... strings) {
            String myData = "null";
            ArrayList<prev_requests.appointments> apps = new ArrayList<prev_requests.appointments>();
            try {
                String url = new String("http://10.0.2.2:3000/newprovreq?name="+strings[0].replace(" ","%20")+"&mail="+strings[1]+"&phone="+strings[2]+"&city="+strings[3]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);




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
        protected void onPostExecute(ArrayList<prev_requests.appointments> arr) {
            go_main();
        }
    }

}
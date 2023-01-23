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
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Signup_user extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);
    }


    public void sign_up(View v)
    {
        Add_user_API newuser = new Add_user_API();
        EditText mail = (EditText) findViewById(R.id.email);
        EditText username = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.password);
        EditText confirm = (EditText) findViewById(R.id.confirm);
        String regexStr = "/d{11}";


        String email = mail.getText().toString();
        String name = username.getText().toString();
        String password = pass.getText().toString();
        String conf = confirm.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show();
        }
        else
            if(name.contains(" "))
            {
                Toast.makeText(this, "Invalid Username!", Toast.LENGTH_SHORT).show();
            }
            else
                if(!conf.equals(password))
                {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
                else
                    newuser.execute(name,password,email);






    }


  public void go_main()
  {
      Toast.makeText(this, "User Created!", Toast.LENGTH_SHORT).show();
      Intent login_screen = new Intent(this, MainActivity.class);
      startActivity(login_screen);
  }


    private class Add_user_API extends AsyncTask<String, String, ArrayList<prev_requests.appointments>> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected ArrayList<prev_requests.appointments> doInBackground(String... strings) {
            String myData = "null";
            ArrayList<prev_requests.appointments> apps = new ArrayList<prev_requests.appointments>();
            try {
                String url = new String("http://10.0.2.2:3000/newuser?name="+strings[0]+"&password="+strings[1]+"&email="+strings[2]);

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
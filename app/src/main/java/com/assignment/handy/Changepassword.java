package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class Changepassword extends AppCompatActivity {
    EditText curr_pass,new_pass,conf_pass;
    String password = new String(),new_password = new String(),confirm_password = new String();
    int u_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            u_id = bundle.getInt("u_id");
        }


    }
    public void change_pass(View v)
    {
        curr_pass = (EditText) findViewById(R.id.current_pass);
        new_pass = (EditText) findViewById(R.id.new_pass);
        conf_pass = (EditText) findViewById(R.id.new_pass_confirm);
         password = curr_pass.getText().toString();
         new_password = new_pass.getText().toString();
         confirm_password = conf_pass.getText().toString();
     Get_Pass_API getpass = new Get_Pass_API();
     getpass.execute(String.valueOf(u_id));
    }
public void check(String pass)
{
    Toast toast;


        if(pass.equals(password))
    {

        if(new_password.equals(confirm_password))
        {
            Update_Pass_API update_pass = new Update_Pass_API();
            update_pass.execute(String.valueOf(u_id),new_password);
        }
        else
        {
            toast = Toast.makeText(getApplicationContext(), "New password does not match", Toast.LENGTH_LONG);
            toast.show();
        }

    }
    else
        {
            toast = Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_LONG);
            toast.show();
        }

}


public void password_update_toast()
{
    Toast toast;
    toast = Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_LONG);
    toast.show();
}







    private class Get_Pass_API extends AsyncTask<String, String, JSONArray> {
        InputStream in;
        JSONArray object,u_object;
        Integer signcode = 0;
        ArrayList<Book_screen.provider> prov_arr = new ArrayList<Book_screen.provider>();
        @SuppressLint("WrongThread")
        @Override
        protected JSONArray doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/getuser_id?id="+strings[0]);

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
            try {
                check(ja.getJSONObject(0).getString("password"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private class Update_Pass_API extends AsyncTask<String, String, Void> {
        InputStream in;
        JSONArray object,u_object;
        Integer signcode = 0;
        ArrayList<Book_screen.provider> prov_arr = new ArrayList<Book_screen.provider>();
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/setpass_u?id="+strings[0]+"&password="+strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);

            }catch (MalformedURLException e) {
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
          password_update_toast();

        }
    }

}
package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText user,password;
    String mode = "User";
    int u_id;
    int p_id;
    String p_name=new String(),p_job=new String();
    float p_rate;
    Login_API login;
    @Override
    public void onBackPressed() {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
    }
    public void set_user_id(int id)
    {
        u_id = id;
    }
    public void new_signup(View v)
    {
        if(mode.equals("User")) {
            Intent signup_screen = new Intent(this, Signup_user.class);
            startActivity(signup_screen);
        }
        else
        {
            Intent signup_screen = new Intent(this, Prov_signup.class);
            startActivity(signup_screen);
        }
    }
    public void set_prov(int id,String name,String job,float rate)
    {
        p_id = id;
        p_name = name;
        p_job = job;
        p_rate = rate;
    }
    public void change_mode(View v)
    {
        Button btn = (Button) v;
        if(btn.getText().toString().equals("User"))
        {
            btn.setText("Provider");
            mode="Provider";
        }
        else
        {
            btn.setText("User");
            mode="User";
        }
    }
    public void login_handler(View v)
    {
        if(mode.equals("User")) {
            login = new Login_API();
            login.execute(user.getText().toString(), password.getText().toString());
        }
        else
        {
            Prov_Login_API prov_login = new Prov_Login_API();
            prov_login.execute(user.getText().toString(), password.getText().toString());
        }
    }
    public void log_correct(){
        Intent home_screen = new Intent(this, Main2Activity.class);
        home_screen.putExtra("u_id",u_id);
        startActivity(home_screen);
    }

    public void log_correct_prov(){
        Intent home_screen = new Intent(this, Provider_home.class);
        home_screen.putExtra("p_id",p_id);
        home_screen.putExtra("p_name",p_name);
        home_screen.putExtra("p_job",p_job);
        home_screen.putExtra("p_rate",p_rate);
        startActivity(home_screen);
    }
    private class Login_API extends AsyncTask<String, String, Integer> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/userlist?name="+strings[0]+"&pass="+strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                if(object.getJSONObject(0).has("u_name")){
                    set_user_id(object.getJSONObject(0).getInt("u_id"));
                    signcode=1;
                }
                else
                    signcode=0;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return signcode;
        }
        @Override
        protected void onPostExecute(Integer sc) {
            Toast toast;
           if(sc == 0)
           {
               toast = Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_LONG);
               toast.show();

           }
            else
               if(sc==1)
               {
                   log_correct();
               }
        }
    }


    private class Prov_Login_API extends AsyncTask<String, String, Integer> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(String... strings) {
            String myData = "null";
            try {
                String url = new String("http://10.0.2.2:3000/provlist_log?name="+strings[0]+"&pass="+strings[1]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                if(object.getJSONObject(0).has("p_name")){
                    set_prov(object.getJSONObject(0).getInt("p_id"),object.getJSONObject(0).getString("p_name"),object.getJSONObject(0).getString("job"),(float)object.getJSONObject(0).getDouble("rating"));
                    signcode=1;
                }
                else
                    signcode=0;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return signcode;
        }
        @Override
        protected void onPostExecute(Integer sc) {
            Toast toast;
            if(sc == 0)
            {
                toast = Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_LONG);
                toast.show();

            }
            else
            if(sc==1)
            {
                log_correct_prov();
            }
        }
    }
}


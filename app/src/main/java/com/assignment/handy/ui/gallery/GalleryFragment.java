package com.assignment.handy.ui.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.assignment.handy.Main2Activity;
import com.assignment.handy.R;
import com.assignment.handy.databinding.FragmentGalleryBinding;
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

public class GalleryFragment extends Fragment {
    View root;
    get_appoint_api get_appoint = new get_appoint_api();
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        Main2Activity mainact = (Main2Activity) getActivity();
        get_appoint.execute(String.valueOf(mainact.get_u_id()));
        return root;
    }

    class appointments{
        String p_name;
        int day,month,hour;
        appointments(String p_name,int day,int month,int hour)
        {
            this.p_name = p_name;
            this.day = day;
            this.month = month;
            this.hour = hour;
        }
        public String get_name(){return p_name;}
        public String get_hour(){
            if(hour == 9)
            {
            return "9Am - 11AM";
            }
            if(hour == 11)
            {
                return "11Am - 1PM";
            }
            if(hour == 1)
            {
                return "1PM - 3PM";
            }
            if(hour ==3)
            {
                return "3PM - 5PM";
            }

            return "no string";
            }
        public String get_date(){return String.valueOf(day)+" / "+String.valueOf(month+1);}

    }






    public void adapt_apps(ArrayList<appointments> apps){

       Adapter_myappoint adap = new Adapter_myappoint(getContext(), apps);
        ListView lv = (ListView) root.findViewById(R.id.user_appoint_list);
        lv.setAdapter(adap);


    }



    private class Adapter_myappoint extends ArrayAdapter<appointments>
    {
        public Adapter_myappoint(Context context, ArrayList<appointments> apps)
        {
            super(context,R.layout.my_appointments_list,apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            appointments prov = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_appointments_list,parent,false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.app_name);
            TextView hour = (TextView) convertView.findViewById(R.id.app_hours);
            TextView date = (TextView) convertView.findViewById(R.id.app_date);
            name.setText(prov.get_name());
            hour.setText(prov.get_hour());
            date.setText(prov.get_date());
            return convertView;
        }
    }





    private class get_appoint_api extends AsyncTask<String, String, ArrayList<appointments>> {
        InputStream in;
        JSONArray object;
        Integer signcode = 0;
        @SuppressLint("WrongThread")
        @Override
        protected ArrayList<appointments> doInBackground(String... strings) {
            String myData = "null";
            ArrayList<appointments> apps = new ArrayList<appointments>();
            try {
                String url = new String("http://10.0.2.2:3000/getapp_user?id="+strings[0]);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url));

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine = in.readLine();
                object = new JSONArray(inputLine);
                JSONArray ja;

                for(int i = 0;i<object.length();i++)
                {
                    url = new String("http://10.0.2.2:3000/Provlist?tag="+object.getJSONObject(i).getString("p_id"));

                    client = new DefaultHttpClient();
                    request = new HttpGet();
                    request.setURI(new URI(url));

                    response = client.execute(request);
                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    inputLine = in.readLine();
                    ja = new JSONArray(inputLine);
                    apps.add(new appointments(ja.getJSONObject(0).getString("p_name"),object.getJSONObject(i).getInt("day"),object.getJSONObject(i).getInt("month"),object.getJSONObject(i).getInt("hour")));

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
            return apps;
        }
        @Override
        protected void onPostExecute(ArrayList<appointments> arr) {
            adapt_apps(arr);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
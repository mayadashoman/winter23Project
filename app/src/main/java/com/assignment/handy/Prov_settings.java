package com.assignment.handy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Prov_settings extends AppCompatActivity {
int p_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prov_settings);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            p_id = bundle.getInt("p_id");
        }
    }

    public void prov_change_password(View v)
    {
        Intent pass_screen = new Intent(this, Prov_password.class);
        pass_screen.putExtra("p_id",p_id);
        startActivity(pass_screen);
    }
    public void log_out(View v)
    {
        Intent login_screen = new Intent(this, MainActivity.class);
        startActivity(login_screen);
    }
}
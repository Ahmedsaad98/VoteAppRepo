package com.ahmedalraziki.vote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            Intent i = new Intent(MainActivity.this, Dashboard.class);
            i.putExtra("res", "0");
            startActivity(i);
            finish();
    }
}
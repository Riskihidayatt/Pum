package com.example.pum.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pum.R;
import com.example.pum.login.LoginActivity;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SplashScreen", "onCreate started");
        setContentView(R.layout.activity_main);
        Log.d("SplashScreen", "ContentView set");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("SplashScreen", "Moving to LoginActivity");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

}

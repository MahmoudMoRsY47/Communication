package com.example.communication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    FirebaseAuth mAuth  = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thread=new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    if (mAuth.getCurrentUser()==null) {
                        Intent intent = new Intent(Splash.this, SignIn.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(Splash.this, HomeActivity.class);
                        startActivity(intent);


                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        thread.start();
    }
}

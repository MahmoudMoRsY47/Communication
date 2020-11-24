package com.example.communication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewProfile extends AppCompatActivity {
    SharedPreferences Myshared;
    private String userprofile;

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        ImageView profile=findViewById(R.id.profile);
        Myshared = getSharedPreferences("userData", MODE_PRIVATE);
        setUserprofile(Myshared.getString("userProfileLink", ""));
        Glide.with(ViewProfile.this)
                .load(getUserprofile())
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .centerCrop()
                .into(profile);

    }
    }

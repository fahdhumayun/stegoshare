// By Fahd Humayun

package com.example.fahd.stegoshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        initialization();

        //Intent i = new Intent(this, HideActivity.class);
        //Intent i = new Intent(this, SeedActivity.class);
        Intent i = new Intent(this, SelectImagesActivity.class);
        startActivity(i);
    }

    private void initialization(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Recover Seed List");
    }
}

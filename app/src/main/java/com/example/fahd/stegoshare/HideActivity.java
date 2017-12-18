// By Fahd Humayun

package com.example.fahd.stegoshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HideActivity extends AppCompatActivity {

    ImageView stepsImageView;
    ImageButton nextStepButton;
    ImageButton skipStepsButton;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide);

    }
}

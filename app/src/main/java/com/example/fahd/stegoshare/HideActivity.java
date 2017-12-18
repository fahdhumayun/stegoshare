// By Fahd Humayun
// HideActivity (Activity) - Used in the beginning of the hide seed list process.
// Handles the steps i.e. the walkthrough/tutorial of the hiding list procedure.

package com.example.fahd.stegoshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HideActivity extends AppCompatActivity {

    ImageView stepsImageView;
    ImageButton nextStepButton, skipStepsButton;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide);

        initialization();

        stepsImageView = (ImageView) findViewById(R.id.id_hideStepsImageView);
        nextStepButton = (ImageButton) findViewById(R.id.id_hideNextStepButton);
        skipStepsButton = (ImageButton) findViewById(R.id.id_hideSkipButton);

        counter = 1;

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;

                if(counter > 12){
                    startSeedActivity();
                    finish();
                }

                if(counter == 12){
                    nextStepButton.setImageResource(R.drawable.finish_button);
                    skipStepsButton.setBackground(null);
                }

                stepsImageView.setBackground(null);

                switch(counter){
                    case 1: stepsImageView.setImageResource(R.drawable.hide_steps_1);
                        break;
                    case 2:
                        stepsImageView.setImageResource(R.drawable.hide_steps_2);
                        break;
                    case 3:
                        stepsImageView.setImageResource(R.drawable.hide_steps_3);
                        break;
                    case 4:
                        stepsImageView.setImageResource(R.drawable.hide_steps_4);
                        break;
                    case 5:
                        stepsImageView.setImageResource(R.drawable.hide_steps_5);
                        break;
                    case 6:
                        stepsImageView.setImageResource(R.drawable.hide_steps_6);
                        break;
                    case 7:
                        stepsImageView.setImageResource(R.drawable.hide_steps_7);
                        break;
                    case 8:
                        stepsImageView.setImageResource(R.drawable.hide_steps_8);
                        break;
                    case 9:
                        stepsImageView.setImageResource(R.drawable.hide_steps_9);
                        break;
                    case 10:
                        stepsImageView.setImageResource(R.drawable.hide_steps_10);
                        break;
                    case 11:
                        stepsImageView.setImageResource(R.drawable.hide_steps_11);
                        break;
                    case 12:
                        stepsImageView.setImageResource(R.drawable.hide_steps_12);
                        break;
                    default:
                        stepsImageView.setBackground(null);
                        stepsImageView.setImageResource(R.drawable.hide_steps_1);
                }

            }
        });

        skipStepsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startSeedActivity();
            }
        });
    }

    private void initialization(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Hide Seed List Steps");
    }

    private void startSeedActivity(){
        Intent i = new Intent(this, SeedActivity.class);
        startActivity(i);
        finish();
    }
}

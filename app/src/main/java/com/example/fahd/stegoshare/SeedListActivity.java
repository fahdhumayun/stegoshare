// By Fahd Humayun

package com.example.fahd.stegoshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class SeedListActivity extends AppCompatActivity {

    ArrayList<String> seedArrayList;
    int user_selected_shares_n;

    private ListView wordsListView;
    private CustomAdapter ca;

    private ImageButton nextButton;

    private CustomTextView ctv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Hide Seed List - Step 1: Input words list");

        seedArrayList = new ArrayList<String>(getIntent().getStringArrayListExtra("seedArrayList"));
        user_selected_shares_n = getIntent().getIntExtra("user_selected_shares_n", -1);

        wordsListView = (ListView) findViewById(R.id.id_wordsListView);
        nextButton = (ImageButton) findViewById(R.id.id_nextButtonList);

        ca = new CustomAdapter(this, seedArrayList);

        wordsListView.setAdapter(ca);

        wordsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l){

                showAlertDialog(i, wordsListView.getItemAtPosition(i).toString());

                return true;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startSelectImagesActivity();
            }
        });

        ctv = (CustomTextView) findViewById(R.id.nextButtonTvId);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        ctv.startAnimation(animation);

    }

    private void showAlertDialog(final int i, String wordSelected){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText wordEditText = new EditText(this);

        wordSelected = wordSelected.substring(2,wordSelected.length());

        alert.setMessage("Enter a new word to replace the word '" + wordSelected + "'.");
        alert.setTitle("Make a change.");

        alert.setView(wordEditText);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String word = (i+1)+"."+wordEditText.getText().toString();
                seedArrayList.set(i, word);
                ca = new CustomAdapter(SeedListActivity.this, seedArrayList);
                wordsListView.setAdapter(ca);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    @Override
    public void onBackPressed(){

        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to go back?")
                .setMessage("You will lose the data entered and you will need to enter the data again.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent i = new Intent(SeedListActivity.this, SeedActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    public void startSelectImagesActivity(){
        Intent i = new Intent(this, SelectImagesActivity.class); // line 247

        i.putExtra("user_selected_shares_n", user_selected_shares_n); // send n to SelectImagesActivity

        startActivity(i);
    }

}

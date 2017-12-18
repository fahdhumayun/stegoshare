// By Nathan Morgenstern
// SeedActivity (Activity) - This is the first activity of the hide seed list process.
// It handles the input of the seeds (words), and then starts the SeedListActivity to display the list.

package com.example.fahd.stegoshare;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

public class SeedActivity extends AppCompatActivity {

    int SEED_SIZE = 3;
    int counter;

    int user_selected_shares_n;
    int user_selected_shares_m;

    ImageButton leftButton;
    ImageButton rightButton;
    ImageButton confirmButton;
    EditText    seedWord;
    TextView    seedCount;
    String[]    seedList;
    Spinner     totalWords;
    Spinner     totalShares;
    Spinner     requiredShares;

    ArrayList<String> seedArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Hide Seed List - Step 1: Input words");

        totalWords     = (Spinner) findViewById(R.id.wordLength);
        totalShares    = (Spinner) findViewById(R.id.totalShares);
        requiredShares = (Spinner) findViewById(R.id.requiredShares);
        rightButton    = (ImageButton) findViewById(R.id.rightButton);
        leftButton     = (ImageButton) findViewById(R.id.leftButton);
        seedWord       = (EditText) findViewById(R.id.seedWord);
        seedCount      = (TextView) findViewById(R.id.seedCount);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/CenturyGothic.ttf");
        seedCount.setTypeface(typeface);
        confirmButton  = (ImageButton) findViewById(R.id.confirmButton);

        user_selected_shares_m = 2;
        user_selected_shares_n = 2;
        if (savedInstanceState != null) {
            user_selected_shares_m = savedInstanceState.getInt("user_selected_shares_m");
            user_selected_shares_n = savedInstanceState.getInt("user_selected_shares_n");
            SEED_SIZE              = savedInstanceState.getInt("SEED_SIZE");
        }

        confirmButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onConfirm(view);
            }
        });


        setAdapters();
        if (savedInstanceState != null)
        {
            seedWord.setText(savedInstanceState.getString("seedWord"));
            seedList = savedInstanceState.getStringArray("seedList");
            counter  = savedInstanceState.getInt("counter");

            seedCount.setText(counter + ".");
            seedWord.setHint("word " + counter);

            setButtonVisibility();
        }
        else
            initialize(SEED_SIZE);

    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        outState.putString("seedWord", seedWord.getText().toString());
        outState.putStringArray("seedList", seedList);
        outState.putInt("counter", counter);
        outState.putInt("user_selected_shares_m", user_selected_shares_m);
        outState.putInt("user_selected_shares_n", user_selected_shares_n);
        outState.putInt("SEED_SIZE", SEED_SIZE);
        super.onSaveInstanceState(outState);
    }

    public void setAdapters(){

        Resources res = getResources();
        //String Arrays
        String[] totalWordsArray     = res.getStringArray(R.array.total_words_array);
        String[] totalSharesArray    = res.getStringArray(R.array.total_shares_array);
        String[] requiredSharesArray = res.getStringArray(R.array.required_shares_array);

        // Getting the arrays in array list format for the adapters
        ArrayList<String> wordsArrayList          = new ArrayList<String>(Arrays.asList(totalWordsArray));
        ArrayList<String> totalSharesArrayList    = new ArrayList<String>(Arrays.asList(totalSharesArray));
        ArrayList<String> requiredSharesArrayList = new ArrayList<String>(Arrays.asList(requiredSharesArray));

        ArrayAdapter<String> wordsAdapter;
        ArrayAdapter<String> totalSharesAdapter;
        ArrayAdapter<String> requiredSharesAdapter;

        wordsAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, wordsArrayList);

        totalSharesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, totalSharesArrayList);

        requiredSharesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, requiredSharesArrayList);


        //Adding action listeners
        TotalWordsSpinnerInteractionListener t_listener     = new TotalWordsSpinnerInteractionListener();
        totalWords.setOnTouchListener(t_listener);
        totalWords.setOnItemSelectedListener(t_listener);

        RequiredSharesSpinnerInteractionListener r_listener = new RequiredSharesSpinnerInteractionListener();
        requiredShares.setOnTouchListener(r_listener);
        requiredShares.setOnItemSelectedListener(r_listener);

        TotalSharesSpinnerInteractionListener ts_listener   = new TotalSharesSpinnerInteractionListener();
        totalShares.setOnTouchListener(ts_listener);
        totalShares.setOnItemSelectedListener(ts_listener);



        totalWords.setAdapter(wordsAdapter);
        totalShares.setAdapter(totalSharesAdapter);
        requiredShares.setAdapter(requiredSharesAdapter);

    }

    public void initialize(int seedSize){

        SEED_SIZE = seedSize;
        counter   = 1;

        seedArrayList = new ArrayList<String>();
        seedList = new String[SEED_SIZE];
        Arrays.fill(seedList, "");

        leftButton.setVisibility(View.INVISIBLE);
        seedCount.setText(counter + ".");
        seedWord.setHint("word " + counter);

    }

    public void setButtonVisibility(){

        if (counter == 1)
            leftButton.setVisibility(View.INVISIBLE);
        else
            leftButton.setVisibility(View.VISIBLE);

        if(counter == SEED_SIZE)
            rightButton.setVisibility(View.INVISIBLE);
        else
            rightButton.setVisibility(View.VISIBLE);

    }

    public void onRightClick(View view){
        if(seedWord.getText().toString().equals("")) {
            showToast("Please enter the word to go to the next word.");
            return;
        }

        if(!seedWord.getText().toString().equals("")) {
            seedList[counter - 1] =  counter + ". " + seedWord.getText().toString();
        }

        counter++;
        //Clearing the text field and setting the word number
        seedWord.setText("");
        seedCount.setText(counter + ".");

        //Checking the next index for null
        if (!seedList[counter - 1].toString().equals(""))
            seedWord.setText(seedList[counter - 1].replace(".","").replaceAll("\\d",""));
        else
            seedWord.setHint("word " + counter);

        setButtonVisibility();

    }

    public void onLeftClick(View view){

        counter--;

        seedWord.setText("");
        seedCount.setText(counter +  ".");

        if (!seedList[counter - 1].toString().equals(""))
            seedWord.setText(seedList[counter - 1].replace(".","").replaceAll("\\d",""));
        else
            seedWord.setHint("word " + counter);

        setButtonVisibility();
    }

    public void onConfirm(View view){
        seedList[counter - 1] = counter + ". " + seedWord.getText().toString();
        if(!seedWord.getText().toString().equals("") && !containsNullStr(seedList) && (user_selected_shares_m <= user_selected_shares_n)) {
            int counter = 1;
            for (String s : seedList) {
                counter++;
            }
            seedArrayList = new ArrayList<String>(Arrays.asList(seedList));



            Intent i = new Intent(this, SeedListActivity.class); // line 247

            i.putExtra("user_selected_shares_n", user_selected_shares_n); // send n to SelectImagesActivity
            i.putExtra("user_selected_shares_m", user_selected_shares_m); // send m to SelectImagesActivity
            i.putExtra("seedArrayList", seedArrayList);
            i.putExtra("recoverActivityFlag", false);

            startActivity(i);
            finish();
        }
        else if(!(user_selected_shares_m <= user_selected_shares_n))
            showToast("Required shares should be less than or equal to total shares.");
        else
            showToast("Not enough words entered.");

    }

    public boolean containsNullStr(String[] str){
        for(String s : str){
            if(s.equals(""))
                return true;
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public class TotalSharesSpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            if (userSelect) {
                user_selected_shares_n = Integer.parseInt(totalShares.getItemAtPosition(position).toString());
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    }

    public class RequiredSharesSpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                user_selected_shares_m = Integer.parseInt(requiredShares.getItemAtPosition(position).toString());
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    }

    public class TotalWordsSpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                initialize(Integer.parseInt(totalWords.getItemAtPosition(position).toString()));
                setButtonVisibility();
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

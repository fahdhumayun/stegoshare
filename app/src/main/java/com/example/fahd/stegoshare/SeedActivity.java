// By Nathan Morgenstern

package com.example.fahd.stegoshare;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SeedActivity extends AppCompatActivity {

    int SEED_SIZE;
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

        setAdapters();

        confirmButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onConfirm(view);
            }
        });
    }

    public void setAdapters(){

        Resources res = getResources();
        //String Arrays
        String[] totalWordsArray     = res.getStringArray(R.array.total_words_array);
        String[] totalSharesArray    = res.getStringArray(R.array.total_shares_array);
        String[] requiredSharesARray = res.getStringArray(R.array.required_shares_array);

        // Getting the arrays in array list format for the adapters
        ArrayList<String> wordsArrayList          = new ArrayList<String>(Arrays.asList(totalWordsArray));
        ArrayList<String> totalSharesArrayList    = new ArrayList<String>(Arrays.asList(totalSharesArray));
        ArrayList<String> requiredSharesArrayList = new ArrayList<String>(Arrays.asList(requiredSharesARray));

        ArrayAdapter<String> wordsAdapter;
        ArrayAdapter<String> totalSharesAdapter;
        ArrayAdapter<String> requiredSharesAdapter;

        wordsAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, wordsArrayList);

        totalSharesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, totalSharesArrayList);

        requiredSharesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, requiredSharesARray);


        //Adding action listeners
        totalWords.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.v("WORDS", "Change detected! Selected: " + totalWords.getItemAtPosition(position).toString() );
                initialize(Integer.parseInt(totalWords.getItemAtPosition(position).toString()));
                setButtonVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        totalShares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.v("TOT_SHARES", "Change detected! Selected: " + totalShares.getItemAtPosition(position).toString() );
                user_selected_shares_n = Integer.parseInt(requiredShares.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        requiredShares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.v("REQ_SHARES", "Change detected! Selected: " + requiredShares.getItemAtPosition(position).toString() );
                user_selected_shares_m = Integer.parseInt(requiredShares.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


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

        seedList[counter - 1] =  seedWord.getText().toString();

        counter++;
        //Clearing the text field and setting the word number
        seedWord.setText("");
        seedCount.setText(counter + ".");

        //Checking the next index for null
        if (!seedList[counter - 1].toString().equals(""))
            seedWord.setText(seedList[counter - 1]);
        else
            seedWord.setHint("word " + counter);

        setButtonVisibility();

    }

    public void onLeftClick(View view){

        counter--;

        seedWord.setText("");
        seedCount.setText(counter +  ".");

        if (!seedList[counter - 1].toString().equals(""))
            seedWord.setText(seedList[counter - 1]);
        else
            seedWord.setHint("word " + counter);


        setButtonVisibility();

    }

    public void onConfirm(View view){
        seedList[counter - 1] = counter + "." + seedWord.getText().toString();
        if(!seedWord.getText().toString().equals("") && !containsNullStr(seedList) && (user_selected_shares_m <= user_selected_shares_n)) {
            int counter = 1;
            for (String s : seedList) {
                Log.v("SEEDLIST", counter + ". " + s);
                counter++;
            }
            seedArrayList = new ArrayList<String>(Arrays.asList(seedList));

            buildShares(buildString(),user_selected_shares_n,user_selected_shares_m);

            Intent i = new Intent(this, SelectImagesActivity.class);
            startActivity(i);
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

    public String buildString(){
        String res = "";
        int index = 1;
        for(String s:seedList) {
            res += s + "\n";
            index++;
        }
        return res;
    }

    public void buildShares(String s, int N, int M){

        final int CERTAINTY = 256;
        final SecureRandom random = new SecureRandom();
        Random randy = new Random();

        String seedListString = s;
        byte[] byteArray = seedListString.getBytes();
        String reconstitutedString = new String(byteArray);
        System.out.println("byteArray: " + byteArray);
        System.out.println("reconstructed: \n" + reconstitutedString);

        System.out.println("------------------------------------------------------");
        final BigInteger secret = new BigInteger(1,byteArray);
        System.out.println("BigInteger: " + secret);
        System.out.println("BigInteger.toByteArray: " + secret.toByteArray());

        String recon = new String(byteArray);
        System.out.println("reconstructed:\n" + recon + "\n");



        // prime number must be longer then secret number
        final BigInteger prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);

        /*
        // 2 - at least 2 secret parts are needed to view secret
        // 5 - there are 5 persons that get secret parts
        final SecretShare[] shares = Shamir.split(secret, M, N, prime, random);
        // we can use any combination of 2 or more parts of secret
        SecretShare[] sharesToViewSecret = new SecretShare[] {shares[4],shares[1], shares[2]}; // 4 & 1 & 2
        BigInteger result = Shamir.combine(sharesToViewSecret, prime);
        //printMultiSecretShare(stringBuilder(shares[1].getShare().toByteArray()), stringBuilder(shares[4].getShare().toByteArray()), stringBuilder(shares[0].getShare().toByteArray()));
        sharesToViewSecret = new SecretShare[] {shares[1],shares[4],shares[0]}; // 1 & 4 & 0
        result = Shamir.combine(sharesToViewSecret, prime);
        */


        final SecretShare[] shares = Shamir.split(secret, M, N, prime, random);


        // we can use any combination of 2 or more parts of secret
        ArrayList<SecretShare> sharesToViewSecretArrayList = new ArrayList<SecretShare>();
        sharesToViewSecretArrayList = generateRandomSecretShareArrayList(shares);
        SecretShare[] sharesToViewSecret = sharesToViewSecretArrayList.toArray(new SecretShare[sharesToViewSecretArrayList.size()]);

        BigInteger result = Shamir.combine(sharesToViewSecret, prime);


        sharesToViewSecretArrayList = generateRandomSecretShareArrayList(shares);
        sharesToViewSecret          = sharesToViewSecretArrayList.toArray(new SecretShare[sharesToViewSecretArrayList.size()]);
        result = Shamir.combine(sharesToViewSecret, prime);
    }


    public ArrayList<SecretShare> generateRandomSecretShareArrayList(SecretShare[] shares){
        Random randy = new Random();
        ArrayList<SecretShare> ss_arr_list = new ArrayList<SecretShare>();
        ArrayList<Integer> closedList = new ArrayList<Integer>();

        while(closedList.size() < user_selected_shares_m) {
            int randNum = randy.nextInt((user_selected_shares_n - user_selected_shares_m) + user_selected_shares_m);
            if(!isInClosedList(closedList,randNum)) {
                closedList.add(randNum);
                ss_arr_list.add(shares[randNum]);
            }

        }

        return ss_arr_list;
    }

    public Boolean isInClosedList(ArrayList<Integer> closed, int item){
        for(Integer i: closed) {
            if (i == item)
                return true;
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

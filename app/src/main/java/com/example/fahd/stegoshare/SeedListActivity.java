// By Fahd Humayun

package com.example.fahd.stegoshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class SeedListActivity extends AppCompatActivity {

    ArrayList<String> seedArrayList;
    int user_selected_shares_n;
    int user_selected_shares_m;

    private ListView wordsListView;
    private CustomAdapter ca;

    private ImageButton nextButton;

    private CustomTextView ctv;

    private DBHelper myDBHelper;

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
        user_selected_shares_m = getIntent().getIntExtra("user_selected_shares_m", -1);

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
                SecretShare[] ss = buildShares(buildString(),user_selected_shares_n,user_selected_shares_m);
                try {
                    addSharesToDatabase(ss);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                try {
                    String hash = getSeedListHash(buildString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

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

    public String buildString(){
        String res = "";
        int index = 1;
        for(int i = 0; i < seedArrayList.size(); i++) {
            res += seedArrayList.get(i) + "\n";
            index++;
        }
        return res;
    }

    public String getSeedListHash(String seedList) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(seedList.getBytes(StandardCharsets.UTF_8 )); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();

        String hash = String.format("%064x", new java.math.BigInteger(1, digest));

        System.out.println("Hash: " + hash);
        return hash;
    }

    public SecretShare[] buildShares(String seedList, int N, int M){


        final int CERTAINTY = 256;
        final SecureRandom random = new SecureRandom();

        //Convert the seed list into a bytearray, for conversion to BigInteger
        byte[] byteArray = seedList.getBytes();
        final BigInteger secret = new BigInteger(1,byteArray);


        // prime number must be longer then secret number
        final BigInteger prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);

        // N secret shares are created
        final SecretShare[] shares = Shamir.split(secret, M, N, prime, random);

        //------------------------------------------------------------------------------------------
        //--------------------------------------TESTING---------------------------------------------
        //------------------------------------------------------------------------------------------
        //Testing the secret shares
        ArrayList<SecretShare> sharesToViewSecretArrayList = new ArrayList<SecretShare>();
        sharesToViewSecretArrayList = generateRandomSecretShareArrayList(shares);
        SecretShare[] sharesToViewSecret = sharesToViewSecretArrayList.toArray(new SecretShare[sharesToViewSecretArrayList.size()]);

        BigInteger result = Shamir.combine(sharesToViewSecret, prime);

        sharesToViewSecretArrayList = generateRandomSecretShareArrayList(shares);
        sharesToViewSecret          = sharesToViewSecretArrayList.toArray(new SecretShare[sharesToViewSecretArrayList.size()]);
        result = Shamir.combine(sharesToViewSecret, prime);
        //------------------------------------------------------------------------------------------
        //--------------------------------------TESTING---------------------------------------------
        //------------------------------------------------------------------------------------------


        return shares;
    }

    public void addSharesToDatabase(SecretShare[] ss) throws NoSuchAlgorithmException {
        // create a calendar
        Calendar cal = Calendar.getInstance();

        myDBHelper = new DBHelper(this);

        String date     = cal.getTime() + "";
        String hashList = getSeedListHash(buildString());
        myDBHelper.addDateAndHash(date, hashList);
        //myDBHelper.addListHash(getSeedListHash(buildString()));

        int date_primarykey = myDBHelper.getDatePrimaryKey(date);

        for(SecretShare secret:ss)
            myDBHelper.addShare(secret.getShare().toString() + secret.getNumber(), date_primarykey);

        ArrayList<String> testList = myDBHelper.getSecretSharesStringList();

    }

    public ArrayList<SecretShare> generateRandomSecretShareArrayList(SecretShare[] shares){
        Random randy = new Random();
        ArrayList<SecretShare> ss_arr_list = new ArrayList<SecretShare>();
        ArrayList<Integer> closedList = new ArrayList<Integer>();

        while(closedList.size() < user_selected_shares_m) {
            int randNum = randy.nextInt((user_selected_shares_n - user_selected_shares_m) + user_selected_shares_m);
            if(!isInClosedList(closedList,randNum)) {
                /*System.out.println("The Share is: " + shares[randNum].getShare());*/
                //System.out.println("The share concatenated is: " + temp + shares[randNum].getNumber());

                String temp = new String(shares[randNum].getShare().toString());
                SecretShare test = new SecretShare(temp + shares[randNum].getNumber());

                /*BigInteger bg  = new BigInteger(temp + shares[randNum].getNumber());

                String bg_str  = bg.toString();
                char lastDigit     = bg.toString().charAt(bg.toString().length() - 1);
                String removeConcate = bg.toString().substring(0,bg.toString().length() - 1);

                System.out.println("lastDigit: " + lastDigit);
                System.out.println("removeConcate: " + removeConcate);

                System.out.println("BigInteger.toByteArray: " + shares[randNum].getShare().toByteArray());
                System.out.println("The concatenated.toByteArray: " + bg.toByteArray());
                */
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

}

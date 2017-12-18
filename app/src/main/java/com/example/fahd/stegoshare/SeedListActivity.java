// By Fahd Humayun
//Encryption and decryption popups implemented by Nathan Morgenstern

package com.example.fahd.stegoshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import org.jasypt.util.text.BasicTextEncryptor;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class SeedListActivity extends AppCompatActivity {

    ArrayList<String> seedArrayList;
    int user_selected_shares_n;
    int user_selected_shares_m;
    private BigInteger prime;
    private Boolean encryptShares = false;
    private String  userPassword  = "NULL";

    private ListView wordsListView;
    private CustomAdapter ca;

    private ImageButton nextButton;

    private CustomTextView ctv;

    private DBHelper myDBHelper;

    private boolean recoverActivityFlag;
    private ArrayList<String> shareArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_list);

        recoverActivityFlag = getIntent().getBooleanExtra("recoverActivityFlag", false);


        wordsListView = (ListView) findViewById(R.id.id_wordsListView);
        nextButton = (ImageButton) findViewById(R.id.id_nextButtonList);

        ctv = (CustomTextView) findViewById(R.id.nextButtonTvId);

        if(recoverActivityFlag){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("  Stegoshare");
            getSupportActionBar().setSubtitle("    Recover Seed List - Step 2: Recovered words list");

            shareArrayList = getIntent().getStringArrayListExtra("shareArrayList");

            nextButton.setImageResource(R.drawable.finish_button);

            ctv.setText("Tap on the FINISH button to go back to the Main Menu");

        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("  Stegoshare");
            getSupportActionBar().setSubtitle("    Hide Seed List - Step 1: Input words list");

            nextButton.setImageResource(R.drawable.next_step_button);

            seedArrayList = new ArrayList<String>(getIntent().getStringArrayListExtra("seedArrayList"));
        }

        user_selected_shares_n = getIntent().getIntExtra("user_selected_shares_n", -1);
        user_selected_shares_m = getIntent().getIntExtra("user_selected_shares_m", -1);


        if(!recoverActivityFlag) {
            ca = new CustomAdapter(this, seedArrayList);
            wordsListView.setAdapter(ca);

            wordsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l){

                    showAlertDialog(i, wordsListView.getItemAtPosition(i).toString());

                    return true;
                }
            });
        } else {
            DBHelper dbHelper = new DBHelper(this);

            Log.v("LENGTH", "shareArrayList.get(0).split(\",\").length: " + shareArrayList.get(0).split(",").length);
            if(shareArrayList.get(0).split(",").length < 4){
                popupDecryptMessage();
            }
            else
                handle();
        }

        nextButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(!recoverActivityFlag){
                    popupEncryptMessage();
                } else {
                    popupFinishMessage();
                }

            }
        });


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

        alert.setIcon(android.R.drawable.ic_menu_edit);

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
                //do nothing
            }
        });

        alert.show();
    }

    @Override
    public void onBackPressed(){

        if(!recoverActivityFlag){
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to go back?")
                    .setMessage("You will lose the data entered and you will need to enter the data again.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent i = new Intent(SeedListActivity.this, SeedActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }).create().show();
        } else {
            popupFinishMessage();
        }

    }

    public void startSelectImagesActivity(){
        Intent i = new Intent(this, SelectImagesActivity.class); // line 247

        i.putExtra("user_selected_shares_n", user_selected_shares_n); // send n to SelectImagesActivity
        i.putExtra("callingActivity", "SeedListActivity");

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

    public String getHash(String seedList) throws NoSuchAlgorithmException {
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
        prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);

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
        String hashList = getHash(buildString());
        myDBHelper.addShareInfo(date, hashList, userPassword);
        //myDBHelper.addListHash(getSeedListHash(buildString()));

        int date_primarykey = myDBHelper.getDatePrimaryKey(date);

        Boolean hasPass = myDBHelper.hasPassword();
        if(hasPass)
            Log.v("TEST", "IT HAS A PASSWORD");
        else
            Log.v("TEST", "IT DOESNT");

        for(SecretShare secret:ss)
            myDBHelper.addShare(shareBuilder(secret.getShare(),secret.getNumber(),
                    prime,user_selected_shares_n,user_selected_shares_m),date_primarykey);

        ArrayList<String> testList = myDBHelper.getSecretSharesStringList();

    }

    public String shareBuilder(BigInteger share, int shareNumber, BigInteger prime, int n, int m){
        String share_string = prime.toString() + "," + share.toString() + "," + shareNumber + "," + n + "," +  m;

        System.out.println(share_string);
        return share_string;
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

    private ArrayList<SecretShareHelper> ssh;
    private SecretShare[] secretShareArray;

    public void handle(){
        try {
            handleRecoverList();
            ca = new CustomAdapter(this, recoverSeedList);
            wordsListView.setAdapter(ca);
        }catch(Exception e){popupRetryMessage();}
    }

    public void handleRecoverList(){
        //TODO split the share
        ssh = new ArrayList<SecretShareHelper>();
        for(int i = 0; i < shareArrayList.size(); i++){
            Log.v("TEST", "shareArrayList Item at " + i + " " + shareArrayList.get(0));
            ssh.add(new SecretShareHelper(shareArrayList.get(i)));
        }

        if(isValidHash()){

            if(isEnoughShares()) {

                secretShareArray = getSecretShares().toArray(new SecretShare[ssh.size()]);
                Shamir shamir = new Shamir();
                BigInteger result = shamir.combine(secretShareArray, ssh.get(0).getPrime());

                System.out.println("result: " + result);
                byte[] byteArr = result.toByteArray();
                String wordList = new String(byteArr);

                wordListToArrList(wordList);
            } else {
                popupRetryMessage();
            }
        }

        else {
            popupRetryMessage();
        }

    }

    public boolean isValidHash( ){
        for(int i = 0; i < ssh.size() - 1; i++){
            if(!ssh.get(i).getHash().equals(ssh.get(i+1).getHash()))
                return false;
        }
        return true;
    }

    public ArrayList<SecretShare> getSecretShares(){
        ArrayList<SecretShare> secretSharesArrayList = new ArrayList<SecretShare>();
        for(int i = 0; i < ssh.size(); i++){
            secretSharesArrayList.add(new SecretShare(ssh.get(i).getShareNumber(), ssh.get(i).getShare()));
        }

        return secretSharesArrayList;
    }

    private ArrayList<String> recoverSeedList = new ArrayList<String>();
    
    public void wordListToArrList(String wordList){
        String[] recover = wordList.split("\n");

        recoverSeedList = new ArrayList<String>(Arrays.asList(recover));
    }

    public boolean isEnoughShares(){
        return (ssh.size() >= ssh.get(0).getRequiredShares());
    }

    private void popupFinishMessage(){

        new AlertDialog.Builder(this)
                .setTitle("Important Information.")
                .setMessage("Please delete any encoded images from the phone gallery. For security purposes do not keep the record of your encoded pictures on the phone.")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    private void popupRetryMessage(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage("Would you like to try again?");
        alert.setTitle("No shares found.");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                startSelectImagesActivityForRetry();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });

        alert.show();
    }

    private void popupEncryptMessage(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Enter password below or select no to skip");
        alert.setTitle("Encrypt Secret Shares?");

        final EditText edittext = new EditText(this);

        alert.setView(edittext);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //OR
                String textValue = edittext.getText().toString();

                if(!textValue.equals("")) {
                    encryptShares = true;
                    try {
                        userPassword = getHash(textValue);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    onFinish();
                }
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onFinish();
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void popupDecryptMessage(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Enter password below");

        final EditText edittext = new EditText(this);

        alert.setView(edittext);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //OR
                String textValue = edittext.getText().toString();

                if(!textValue.equals("")) {
                    try {
                        userPassword = getHash(textValue);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                        try {
                            shareArrayList = decryptMessage(shareArrayList,getHash(textValue));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                   handle();
            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void onFinish(){
        if(!recoverActivityFlag){
            SecretShare[] ss = buildShares(buildString(),user_selected_shares_n,user_selected_shares_m);
            try {
                addSharesToDatabase(ss);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                String hash = getHash(buildString());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            startSelectImagesActivity();

        } else {
            popupFinishMessage();
        }
    }

    private void startSelectImagesActivityForRetry(){
        Intent i = new Intent(this, SelectImagesActivity.class);
        i.putExtra("callingActivity", "RecoverActivity");
        startActivity(i);
        finish();
    }

    private ArrayList<String> decryptMessage(ArrayList<String> encryptedList, String pass){
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(pass);
        ArrayList<String> plainTextList  = new ArrayList<String>();

        for(int i = 0; i < encryptedList.size(); i++) {
            try {
                plainTextList.add(textEncryptor.decrypt(encryptedList.get(i)));
            } catch(Exception e){
                //popupRetryMessage();
            }
        }
        return plainTextList;
    }

}

// By Guy Rubinstein
// All the methods actually implemented in this activity by Guy Rubinstein has been transferred
// to the SteganographyAsyncTask for asynchronous execution of the encoding.
// encryptShares() method implemented by Nathan Morgenstern.

package com.example.fahd.stegoshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.jasypt.util.text.BasicTextEncryptor;
import java.util.ArrayList;

public class UploadImagesActivity extends AppCompatActivity {

    private ArrayList<String> imagePaths;
    private ArrayList<String> sharesList;
    private DBHelper dbHelper;
    private Boolean hasPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Hide Seed List - Step 3: Store images");

        imagePaths = (ArrayList<String>) getIntent().getSerializableExtra("imagePaths");
        dbHelper = new DBHelper(this);
        sharesList  = dbHelper.getSecretSharesStringList();
        hasPassword = dbHelper.hasPassword();


        if(hasPassword)
            sharesList = encryptShares(sharesList, dbHelper.getPassword());


        SteganographyAsyncTask asyncTask = new SteganographyAsyncTask(this, sharesList, imagePaths);
        asyncTask.execute();

    }

    //MAY NEED TO INSTALL : Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
    //Problem Using StrongTextEncryptor...obviously better, although basic is fine for now
    public ArrayList<String> encryptShares(ArrayList<String> shares, String pass){
        ArrayList<String> newList = new ArrayList<String>();
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(pass);

        for(int i = 0; i < shares.size(); i++) {
            newList.add(textEncryptor.encrypt(shares.get(i)));
        }

        return newList;
    }


}

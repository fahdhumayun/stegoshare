// By Guy Rubinstein
// The other methods actually implemented in this activity by Guy has been transferred
// to the SteganographyAsyncTask.java for asynchronous execution of the encoding.

package com.example.fahd.stegoshare;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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
            System.out.println("Encrypted: " + newList.get(i));
        }

        //...
        //String plainText = textEncryptor.decrypt(myEncryptedText);

        return newList;
    }


}

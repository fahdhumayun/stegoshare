// By Guy Rubinstein
// The other methods actually implemented in this activity by Guy has been transferred
// to the SteganographyAsyncTask for asynchronous execution of the encoding.

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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class UploadImagesActivity extends AppCompatActivity {

    private ArrayList<String> imagePaths;
    private ArrayList<String> sharesList;
    private DBHelper dbHelper;

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
        sharesList = dbHelper.getSecretSharesStringList();

        SteganographyAsyncTask asyncTask = new SteganographyAsyncTask(this, sharesList, imagePaths);
        asyncTask.execute();

    }

}

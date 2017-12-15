package com.example.fahd.stegoshare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class UploadImagesActivity extends AppCompatActivity {

    private GridView grdImages;

    private ImageAdapter imageAdapter;
    private ArrayList<String> imagePaths;
    private int count;
    private boolean[] thumbnailsselection;
    private int ids[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Hide Seed List - Step 3: Store images");

        imagePaths = (ArrayList<String>) getIntent().getSerializableExtra("encodedImagePaths");
        count = imagePaths.size();

        thumbnailsselection = new boolean[count];

        imageAdapter = new ImageAdapter(this, thumbnailsselection, imagePaths, count);
        grdImages= (GridView) findViewById(R.id.grdImages);
        grdImages.setAdapter(imageAdapter);

        ImageButton emailButton = (ImageButton) findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        //TODO: finished button to clear all data
    }

    private void share(){
        final int len = thumbnailsselection.length;
        ArrayList<String> selectedImagePaths = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (thumbnailsselection[i]) {
                selectedImagePaths.add(imagePaths.get(i));
            }
        }
        if (!selectedImagePaths.isEmpty()) {
            ArrayList<Uri> selectedImageUris = new ArrayList<>();
            for (String path : selectedImagePaths){
                selectedImageUris.add(FileProvider.getUriForFile(this,
                        "com.example.guyrubinstein.finalproject.fileprovider", //TODO CHANGE ME
                        new File(path)));
            }

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedImageUris);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Send..."));
        }
    }
}

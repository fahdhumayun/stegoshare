// By Guy Rubinstein
// By Fahd Humayun - Additional methods: encoding(), saveImageTemporary(), and requestPermission()

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

    private GridView grdImages;

    private ImageAdapter imageAdapter;
    private ArrayList<String> imagePaths;
    private int count;
    private boolean[] thumbnailsselection;
    private int ids[];

    private ArrayList<String> tempImagePaths;
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
        count = imagePaths.size();

        tempImagePaths = new ArrayList<String>();
        dbHelper = new DBHelper(this);
        sharesList = dbHelper.getSecretSharesStringList();

        encoding();

        imagePaths = tempImagePaths;

        thumbnailsselection = new boolean[count];

        imageAdapter = new ImageAdapter(this, thumbnailsselection, imagePaths, count);
        grdImages= (GridView) findViewById(R.id.grdImages);
        grdImages.setAdapter(imageAdapter);

        ImageButton uploadButton = (ImageButton) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        ImageButton doneButton = (ImageButton) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UploadImagesActivity.this)
                        .setTitle("Finished")
                        .setMessage("Are you done? All data will be deleted and cannot be recovered.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                cleanup();
                            }})
                        .setNegativeButton(android.R.string.no
                                , null).show();
            }
        });

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
                        "com.example.fahd.stegoshare.fileprovider", // <-- changed this
                        new File(path)));
            }

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedImageUris);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Send..."));
        }
        if (selectedImagePaths.isEmpty()) {
            Toast.makeText(this, "Please select the images to be uplaoded/stored.", Toast.LENGTH_LONG).show();
        }

    }

    private void cleanup(){
        this.deleteDatabase("stegoshareDB"); //delete database

        // delete all of the temporary encoded files
        for(int i = 0; i < imagePaths.size(); i++){
            File file = new File(imagePaths.get(i));
            if (file.exists()) {
                file.delete();
            }
        }

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent); //return to MainActivity
    }

    private void encoding(){

        requestPermission(this);

        for(int i = 0; i < imagePaths.size(); i++){
            File file = new File(imagePaths.get(i));
            Log.v("TEST", "encoding file: " + file);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            String share = sharesList.get(i);
            Log.v("TEST", "share " + (i+1) + " " + share);
            byte[] bytes = share.getBytes();
            Bitmap tempBitmap = BitmapEncoder.encode(bitmap, bytes);
            Log.v("TEST", "encoding bitmap: " + bitmap);
            saveImageTemporary(tempBitmap, i+1);
        }

        /*
        File file = new File(imagePaths.get(0));
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        String share = "Hello this is the share.";
        byte[] bytes = share.getBytes();
        Bitmap bitmap1 = BitmapEncoder.encode(bitmap, bytes);
        byte[] returnBytes = BitmapEncoder.decode(bitmap1);
        String retrievedShare = new String(returnBytes);

        Log.v("TEST", "retreivedShare: " + retrievedShare);
        */
    }

    private void saveImageTemporary(Bitmap tempBitmap, int imageNumber){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "ImageEncoded-"+imageNumber+".png";
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }
        //Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.v("TEST", "saving file: " + file);
            Log.v("TEST", "saving bitmap: " + tempBitmap);
            tempImagePaths.add(file.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int REQUEST_WRITE_STORAGE = 112;

    private void requestPermission(Activity context) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            //requestPermission(this);
        }
    }
}

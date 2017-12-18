package com.example.fahd.stegoshare;

//Async task implemented by nathan morgenstern including dialog and progress update
//All logic by Guy Rubinstein except those mentioned below
//Fahd Humayun: Additional methods: encoding(), saveImageTemporary(), and requestPermission()

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by nathanmorgenstern on 12/1/17.
 */

public class SteganographyAsyncTask extends AsyncTask<Object,Object,Object>{

    Activity a;
    private int count;
    private int total;
    private ArrayList<String> imagePaths;
    private ArrayList<String> tempImagePaths = new ArrayList<String>();;
    private ArrayList<String> sharesList;
    private ProgressDialog dialog;
    private GridView grdImages;
    private ImageAdapter imageAdapter;
    private boolean[] thumbnailsselection;



    public static final int REQUEST_WRITE_STORAGE = 112;

    SteganographyAsyncTask(Activity a, ArrayList<String> sharesList, ArrayList<String> imagePaths){
        this.a = a;
        this.sharesList = sharesList;
        this.imagePaths = imagePaths;
        this.total      = sharesList.size();
        this.count      = imagePaths.size();
    }

    @Override
    protected Object doInBackground(Object... objects) {

        encoding();

        return new Long(0);
    }

    //Everything below will run on UI thread
    @Override
    protected void onPostExecute(Object counter){
        dialog.dismiss();

        imagePaths   = tempImagePaths;
        thumbnailsselection = new boolean[count];
        imageAdapter = new ImageAdapter(a, thumbnailsselection, imagePaths, count);
        grdImages    =  a.findViewById(R.id.grdImages);
        grdImages.setAdapter(imageAdapter);

        onFinish();
    }

    protected void onPreExecute() {
        dialog = new ProgressDialog(a);
        dialog.setMessage("Encoding Image...1/4");
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Object... o){
        dialog.setMessage("Encoding Image... " + o[0] + "/" + total);
        // Show the result
        //TextView tv = (TextView)a.findViewById(R.id.hworld);
        //tv.setText("" + o[0] + "/4: " + o[1]);
    }

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

    private void encoding() {
        requestPermission(a);

        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            Log.v("TEST", "encoding file: " + file);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            String share = sharesList.get(i);
            Log.v("TEST", "share " + (i + 1) + " " + share);
            byte[] bytes = share.getBytes();
            Bitmap tempBitmap = BitmapEncoder.encode(bitmap, bytes);
            Log.v("TEST", "encoding bitmap: " + bitmap);
            saveImageTemporary(tempBitmap, i + 1);

            publishProgress(i);
        }
    }

    private void onFinish(){
        thumbnailsselection = new boolean[count];

        imageAdapter = new ImageAdapter(a, thumbnailsselection, imagePaths, count);
        grdImages= (GridView) a.findViewById(R.id.grdImages);
        grdImages.setAdapter(imageAdapter);

        ImageButton uploadButton = (ImageButton) a.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        ImageButton doneButton = (ImageButton) a.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(a)
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

    private void cleanup(){
        a.deleteDatabase("stegoshareDB"); //delete database

        // delete all of the temporary encoded files
        for(int i = 0; i < imagePaths.size(); i++){
            File file = new File(imagePaths.get(i));
            if (file.exists()) {
                file.delete();
            }
        }

        Intent intent = new Intent(a,MainActivity.class);
        a.startActivity(intent); //return to MainActivity
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
                selectedImageUris.add(FileProvider.getUriForFile(a,
                        "com.example.fahd.stegoshare.fileprovider", // <-- changed this
                        new File(path)));
            }

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedImageUris);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            a.startActivity(Intent.createChooser(shareIntent, "Send..."));
        }
        if (selectedImagePaths.isEmpty()) {
            Toast.makeText(a, "Please select the images to be uplaoded/stored.", Toast.LENGTH_LONG).show();
        }

    }

}

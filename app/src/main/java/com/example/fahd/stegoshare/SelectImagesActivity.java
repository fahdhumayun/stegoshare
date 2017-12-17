// By Guy Rubinstein

package com.example.fahd.stegoshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Hashtable;

public class SelectImagesActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_EXT_STORAGE = 472;
    private static final int PICK_IMAGES = 985; //request code for selecting images

    private ArrayList<String> imagesPathList;
    private LinearLayout lnrImages;
    private Bitmap bitmap;
    private int numberOfParts;
    private Hashtable<String, Integer> pathToID;
    private TextView tv;
    private CustomTextView ctv;

    boolean recoverActivityFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        String callingActivity = getIntent().getStringExtra("callingActivity");

        Log.v("TEST", "Calling activity: " + callingActivity);

        if(callingActivity.equals("RecoverActivity")){
            recoverActivityFlag = true;
            handleRecoverActivity();
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Hide Seed List - Step 2: Select images");

        //TODO get n from intent
        numberOfParts = getIntent().getIntExtra("user_selected_shares_n",-1); //line 52

        tv = (TextView) findViewById(R.id.textView);
        tv.setText("Add " + numberOfParts + " images that are needed to be encrypted with the generated shares.");
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/CenturyGothic.ttf");
        tv.setTypeface(typeface);

        ctv = (CustomTextView) findViewById(R.id.galleryButtonTvId);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        ctv.startAnimation(animation);

        lnrImages = (LinearLayout) findViewById(R.id.lnrImages);

        ImageButton galleryButton = (ImageButton) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        ImageButton nextButton = (ImageButton) findViewById((R.id.nextButton));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO encode data into photos using imagesPathList

                Intent uploadIntent = new Intent(SelectImagesActivity.this,UploadImagesActivity.class);
                uploadIntent.putExtra("encodedImagePaths", imagesPathList);//TODO add actual encoded images
                startActivity(uploadIntent);
            }
        });

    }

    private void openGallery(){
        //request permission to read storage
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXT_STORAGE);
            return;
        }

        Intent intent = new Intent(SelectImagesActivity.this,CustomPhotoGalleryActivity.class);
        intent.putExtra("numberOfParts", numberOfParts);
        startActivityForResult(intent,PICK_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGES) {
            imagesPathList = new ArrayList<>();
            String[] imagesPath = data.getStringExtra("data").split("\\|");
            try {
                lnrImages.removeAllViews();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            tv.setText("Images added, please review the images and then tap NEXT on the bottom.");

            for (int i = 0; i < imagesPath.length; i++) {
                imagesPathList.add(imagesPath[i]);
                bitmap = BitmapFactory.decodeFile(imagesPath[i]);
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(10, 10, 10, 20);
                imageView.setLayoutParams(lp);
                lnrImages.addView(imageView);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_EXT_STORAGE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Access Granted. Tap Gallery button again and select images.", Toast.LENGTH_LONG).show();

            } else {
                //TODO: what to do if cant access images
                Toast.makeText(getApplicationContext(), "Need access to Gallery for selecting the images.", Toast.LENGTH_LONG).show();
                //request again if permission not granted
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_EXT_STORAGE);
            }
        }
    }

    public void handleRecoverActivity(){

    }
}

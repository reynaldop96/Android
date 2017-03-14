package edu.amherst.photogalleryapp;

/**
 * Created by Hutomo Limanto on 3/4/2017.
 */

/**
 * Created by Hutomo Limanto on 3/4/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.amherst.photogalleryapp.MainActivity;
import edu.amherst.photogalleryapp.R;

import static android.content.ContentValues.TAG;

public class CameraActivity extends Activity implements OnClickListener {

    Button btnTackPic;
    TextView tvHasCamera, tvHasCameraApp;
    ImageView ivThumbnailPhoto;
    Bitmap bitMap;
    static int TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraimagesave);

        btnTackPic = (Button) findViewById(R.id.btnTakePic);
        btnTackPic.setOnClickListener(this);

    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    // on button "btnTackPic" is clicked
    public void onClick(View view) {

        // create intent with ACTION_IMAGE_CAPTURE action
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                //getStringFromBitmap(photoFile);
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //Uri photoURI = FileProvider.getUriForFile(CameraActivity.this, BuildConfig.APPLICATION_ID +".provider", photoFile);
                Uri photoURI = FileProvider.getUriForFile(CameraActivity.this, CameraActivity.this.getApplicationContext().getPackageName() + ".provider", photoFile);
                //Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d(TAG, "Stored Camera Image in Phone");
                Intent intent4 = new Intent(this, MainActivity.class);
                startActivityForResult(intent4, 1);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }



//        // this part to save captured image on provided path
//        File file = new File(Environment.getExternalStorageDirectory(),
//                "my_images.jpg");
//        Uri photoPath = FileProvider.getUriForFile(CameraActivity.this,"com.example.android.fileprovider",file);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);

//        // start camera activity
//        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == TAKE_PICTURE && resultCode== RESULT_OK && intent != null){
            // get bundle
            Bundle extras = intent.getExtras();

            // get bitmap
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivThumbnailPhoto.setImageBitmap(imageBitmap);
            String encodedImage = getStringFromBitmap(imageBitmap);
            try{
                JSONObject jsonObj = new JSONObject("{\"image\":\" + encodedImage + \"}");
                }
            catch (JSONException jExcept){
                Log.e(TAG, "Catching JSONException", jExcept);
            }
        }
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
 /*
 * This functions converts Bitmap picture to a string which can be
 * JSONified.
 * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    // method to check if you have a Camera
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    // method to check you have Camera Apps
    private boolean hasDefualtCameraApp(String action){
        final PackageManager packageManager = getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;

    }
}



package com.example.assessment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.assessment.utilities.DeleteDiaryDialogue;
import com.example.assessment.utilities.DeleteMonitoringDialogue;
import com.example.assessment.utilities.DeleteSPDialogue;

import java.io.ByteArrayOutputStream;

public class OptionsActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final String fileName = "ImageTaken";
    ImageView profilePictureOptions;

    AppCompatEditText username;
    AppCompatButton takeProfilePicture;
    AppCompatButton saveSettingsButton;

    /**
     * The onCreate method that generates the application on startup.
     *
     * @param savedInstanceState - creates the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // Adds a toolbar to the options activity.
        setupNavigation();
        //Initializes the shared preferences and editor.
        sharedPreferences = getSharedPreferences("SleepTrack", MODE_PRIVATE);

        //Set all variables for use.
        username = findViewById(R.id.username);
        takeProfilePicture = findViewById(R.id.photos);
        saveSettingsButton = findViewById(R.id.save_settings_button);
        profilePictureOptions = findViewById(R.id.imageView1);

        //Checks to see if username and profile picture have previously been set and display them.
        getSharedPreferenceData();
        // Asks for read and write permission.
        askPermission();
        // onClickListener to start the camera intent.
        takeProfilePicture();
        // onClickListener that saves all settings and returns to the dashboard.
        saveSettings();
        //onClickListeners to delete settings and data and return to the dashboard.
        resetOptionsSetup();
    }

    /**
     * Initialises the toolbar.
     */
    public void setupNavigation() {
        // Sets the custom toolbar at the top of the application with the app title.
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sleep Track Options:");
        setSupportActionBar(toolbar);
    }

    /**
     * Checks to see if username and profile picture have previously been set and display them.
     */
    public void getSharedPreferenceData() {
        String usernamePreferences = sharedPreferences.getString("username", null);
        String committed = sharedPreferences.getString(fileName, null);
        // Checks if username has been set already.
        if (usernamePreferences != null) username.setText(usernamePreferences);

        //REFERENCE accessed 02/05/2021
        //https://stackoverflow.com/a/28864904
        // Used to set the profile picture to the saved string in shared preferences.
        if (committed != null) profilePictureOptions.setImageURI(Uri.parse(committed));
        //END REFERENCE
    }

    /**
     * The onClickListener to save all settings and return to the dashboard.
     */
    public void saveSettings() {
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText() != null) {
                    editor = sharedPreferences.edit();
                    editor.putString("username", username.getText().toString());
                    editor.apply();

                    Toast.makeText(OptionsActivity.this, "Your settings have been saved", Toast.LENGTH_SHORT).show();

                    Intent refreshIntent = new Intent(OptionsActivity.this, MainActivity.class);
                    OptionsActivity.this.startActivity(refreshIntent);
                }
            }
        });
    }

    /**
     * The onClickListeners to delete all settings and return to the dashboard.
     */
    public void resetOptionsSetup() {
        AppCompatButton delSharedPreferences = findViewById(R.id.delete_shared_preferences);
        AppCompatButton delMonitoring = findViewById(R.id.delete_monitoring);
        AppCompatButton delDiary = findViewById(R.id.delete_diary);

        DialogFragment deleteSPDialogue = new DeleteSPDialogue();
        DialogFragment deleteMonitoringDialogue = new DeleteMonitoringDialogue();
        DialogFragment deleteDiaryDialogue = new DeleteDiaryDialogue();

        delSharedPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSPDialogue.show(OptionsActivity.this.getSupportFragmentManager(), "simple");
            }
        });
        delMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMonitoringDialogue.show(OptionsActivity.this.getSupportFragmentManager(), "simple");
            }
        });
        delDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiaryDialogue.show(OptionsActivity.this.getSupportFragmentManager(), "simple");
            }
        });
    }

    /*
     * CAMERA SETUP AND SAVING OF PICTURE.
     */

    /**
     * Checks if the profile picture button has been pressed and opens the camera.
     */
    public void takeProfilePicture() {
        takeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // REFERENCE_1 accessed 29/04/2021
                // https://developer.android.com/training/camera/photobasics
                // Used to gain access to the camera to take a profile picture.

                // REFERENCE_2 accessed 16/05/2021
                // https://stackoverflow.com/a/38552193
                // Used to ask user for permission to use the camera.
                if (ContextCompat.checkSelfPermission(OptionsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(OptionsActivity.this, new String[]{Manifest.permission.CAMERA}, 404);
                    // END REFERENCE_2
                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        OptionsActivity.this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } catch (ActivityNotFoundException e) {
                        Log.d("picture_error", String.valueOf(e));
                    }
                }
            }
        });
        //END REFERENCE_1
    }

    /**
     * Deals with the camera capture and saves it to shared preferences for use in the
     * navigation menu.
     *
     * @param requestCode - the request code
     * @param resultCode  - the result code
     * @param data        - the intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // REFERENCE accessed 29/04/2021
        // https://developer.android.com/training/camera/photobasics
        // Used to get the result of the camera capture.
        if (data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                //END REFERENCE

                // Gets the uri of the image to save into shared preferences.
                Uri imageUri = getImageUri(this, imageBitmap);
                editor = sharedPreferences.edit();
                editor.putString(fileName, imageUri.toString());
                editor.apply();

                // Sets the camera capture to the profile picture image view.
                profilePictureOptions.setImageBitmap(imageBitmap);
                profilePictureOptions.setImageURI(imageUri);
            }
        }
    }

    /**
     * REFERENCE accessed 02/05/2021
     * https://stackoverflow.com/a/16167993
     * Used to get the URI from the bitmap image created from the camera.
     *
     * @param inContext - the context.
     * @param inImage   - the bitmap image.
     * @return returns the image URI.
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }
    //END REFERENCE

    /**
     * Asks the user for read and write permissions to take and save a profile picture.
     */
    public void askPermission() {
        // REFERENCE accessed 17/05/2021
        // https://stackoverflow.com/a/47735380
        // Used to ensure that read and write permissions are granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        // END REFERENCE
    }
}

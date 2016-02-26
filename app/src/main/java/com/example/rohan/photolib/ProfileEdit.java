package com.example.rohan.photolib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileEdit extends AppCompatActivity {

    String userName, push;
    Boolean profileAlreadyExists, imageChanged;
    ParseObject profile;
    byte[] bitMapData, customBitMapData;
    Bitmap customeImage;
    private Uri outputFileUri;
    final private int PICTURE_REQUEST = 1;
    ImageView ivProfile;
    EditText etName;
    Switch swGender, swPublic, swPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        userName = ParseUser.getCurrentUser().getUsername();
        Log.d("demo", "Current user is " + userName);
        profileAlreadyExists = false;
        imageChanged = false;
        etName = (EditText) findViewById(R.id.editTextEditName);
        swGender = (Switch) findViewById(R.id.switchGender);
        swPublic = (Switch) findViewById(R.id.switchPublic);
        swPush = (Switch) findViewById(R.id.swPush);
        ivProfile = (ImageView) findViewById(R.id.imageViewProfileEdit);
        swPush.setChecked(true);
        swPublic.setChecked(true);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        push = installation.getString("privacy");
        if(Objects.equals(push, "no")){
            swPush.setChecked(true);
        }else if(Objects.equals(push, "yes")){
            swPush.setChecked(false);
        }

        //See if profile exists yet, if not create a new one
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> profileList, ParseException e) {
                if (e == null) {
                    int size = profileList.size();
                    Log.d("demo", "Retrieved " + size + " profiles");
                    if (size > 0) {
                        profileAlreadyExists = true;
                        profile = profileList.get(0);
                    } else {
                        profile = new ParseObject("Profile");
                        profile.put("createdBy", ParseUser.getCurrentUser());
                    }

                    //Populate fields with profile info or defaults from user account
                    fillFields();
                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });

        Button btnImage = (Button) findViewById(R.id.buttonAddImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICTURE_REQUEST);

            }
        });

        Button btnSave = (Button) findViewById(R.id.buttonSaveProfile);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for valid entry
                if (isValidCreds(etName.getText().toString())) {

                    //Update profile values and save to parse
                    ParseInstallation tempInstall = ParseInstallation.getCurrentInstallation();
                    profile.put("Name", etName.getText().toString());
                    if (swGender.isChecked()) {
                        profile.put("Gender", "Male");
                    } else {
                        profile.put("Gender", "Female");
                    }

                    if (swPublic.isChecked()) {
                        profile.put("Public", true);
                    } else {
                        profile.put("Public", false);
                    }

                    if (swPush.isChecked()) {
                        tempInstall.put("privacy", "no");
                        tempInstall.saveInBackground();
                    } else {
                        tempInstall.put("privacy", "yes");
                        tempInstall.saveInBackground();
                    }
                    //upload chosen image or default image
                    if (imageChanged) {
                        //upload new image
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        customeImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        bitMapData = stream.toByteArray();
                        //insert default image
                        final ParseFile file = new ParseFile("profile.jpg", bitMapData);
                        file.saveInBackground(new SaveCallback() {

                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(ProfileEdit.this,
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    profile.put("Image", file);
                                    profile.saveInBackground();
                                    Toast.makeText(ProfileEdit.this, "Profile Saved Successfully!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        });
                    } else if (profileAlreadyExists){
                        //Don't mess with image
                        profile.saveInBackground();
                        finish();
                    } else {
                        //Generate NoImage for default profile pic
                        //Code to make picture into a file format for parse
                        Drawable d = getResources().getDrawable(R.drawable.no_image);
                        Bitmap picture = ((BitmapDrawable) d).getBitmap();
                        // get byte array here
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        bitMapData = stream.toByteArray();

                        //insert default image
                        final ParseFile file = new ParseFile("profile.jpg", bitMapData);
                        file.saveInBackground(new SaveCallback() {

                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(ProfileEdit.this,
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    profile.put("Image", file);
                                    profile.saveInBackground();
                                    Toast.makeText(ProfileEdit.this, "Profile Saved Successfully!", Toast.LENGTH_LONG).show();
                                    finish();

                                    Log.d("demo","default photo submitted");
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    public void fillFields() {
        //pre populate with current profile info or make new profile
        if (profileAlreadyExists){
            etName.setText(profile.getString("Name"));
            if (profile.getString("Gender").equals("Male")){
                swGender.setChecked(true);
            } else {
                swGender.setChecked(false);
            }
            Log.d("demo", "Should be loading profile pic0");
            Picasso.with(ProfileEdit.this).load(profile.getParseFile("Image").getUrl()).into(ivProfile);
        } else {
            Log.d("demo", "Does not think profile exists");
            //save user to extract default values
            ParseUser createdBy = ParseUser.getCurrentUser();
            etName.setText(createdBy.getString("name"));
            swGender.setChecked(true);
            //insert default image
            Drawable defaultImg = getResources().getDrawable(R.drawable.no_image);
            ivProfile.setImageDrawable(defaultImg);
        }
    }

    public boolean isValidCreds(String username) {

        if (username.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Username cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == PICTURE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            customeImage = BitmapFactory.decodeFile(imagePath, options);

            // Do something with the bitmap
            ivProfile.setImageBitmap(customeImage);
            imageChanged = true;

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }
}

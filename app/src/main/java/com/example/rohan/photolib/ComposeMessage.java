package com.example.rohan.photolib;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ComposeMessage extends AppCompatActivity {

    ArrayList<Profile> profiles = new ArrayList<>();
    ArrayList<String> setPrivate = new ArrayList<>();
    ArrayList<String> userNames = new ArrayList<>();
    EditText editTextTo, editTextSubject, editTextMessage;
    Button butAddImage, butSend;
    String to, subject, curMessage;
    ParseObject message;
    byte[] bitMapData;
    ParseUser currentUser;
    String userTo, tempUser;
    final private int PICTURE_REQUEST = 1;
    Bitmap customeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);

        //Get user
        currentUser = ParseUser.getCurrentUser();
        userTo = "";
        tempUser = "";

        //receive list
        if (getIntent().getExtras() != null){
            profiles = (ArrayList<Profile>) getIntent().getExtras().getSerializable("PROFILES");
            userTo = getIntent().getExtras().getString("TOUSER");
        }

        //receive privacy settings
        //Get listing of all user profiles for privacy
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> profileList, ParseException e) {
                if (e == null) {
                    for (ParseObject p : profileList) {
                            //get ParseUser who owns profile
                            ParseUser pu = p.getParseUser("createdBy");
                            try {
                                tempUser = (pu.fetchIfNeeded().getUsername());
                                userNames.add(tempUser);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                            if (!(p.getBoolean("Public"))) {
                                setPrivate.add(tempUser);
                            }
                    }
                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });

        editTextTo = (EditText) findViewById(R.id.editTextComposeTo);
        if (!(userTo.equals(""))) {
            editTextTo.setText(userTo);
        }
        editTextSubject = (EditText) findViewById(R.id.editTextComposeSubject);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        butAddImage = (Button) findViewById(R.id.buttonAddImage);
        butSend = (Button) findViewById(R.id.buttonSendMessage);

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                to = editTextTo.getText().toString();
                subject = editTextSubject.getText().toString();
                curMessage = editTextMessage.getText().toString();

                message = new ParseObject("Messages");
                message.put("From", currentUser.getUsername());
                message.put("To", to);
                message.put("Body", curMessage);
                message.put("Subject", subject);
                message.put("Read", false);

                //Only send message if not a private profile

                if (isValidCreds(to, subject)) {
                    //Save message in parse

                    //check for image added
                    if (bitMapData != null) {
                        final ParseFile file = new ParseFile("message.jpg", bitMapData);
                        file.saveInBackground(new SaveCallback() {

                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(ComposeMessage.this,
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    message.put("Image", file);
                                    message.saveInBackground();
                                    pushNotification();
                                    finish();
                                }
                            }
                        });

                    } else { //no image
                        message.saveInBackground();
                        pushNotification();
                        finish();
                    }
                }
            }
        });

        butAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICTURE_REQUEST);

            }
        });

        Button toList = (Button) findViewById(R.id.buttonToList);
        toList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(ComposeMessage.this);
                builderSingle.setTitle("Choose Receipient");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        ComposeMessage.this,
                        android.R.layout.select_dialog_singlechoice);
                for (int i=0; i<userNames.size(); i++){
                    arrayAdapter.add(userNames.get(i));
                }

                builderSingle.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                        ComposeMessage.this);
                                builderInner.setMessage(strName);
                                builderInner.setTitle("Your Selected Item is");
                                builderInner.setPositiveButton(
                                        "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                                editTextTo.setText(strName);
                                            }
                                        });
                                builderInner.show();
                            }
                        });
                builderSingle.show();
            }
        });

    }

    public boolean isValidCreds(String username, String subject) {

        if (username.trim().equals("") || subject.trim().equals("")) {
            Toast.makeText(getBaseContext(), "To and Subject Fields are required", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(userNames.contains(username.trim()))) {
            Toast.makeText(getBaseContext(), "User not found in system", Toast.LENGTH_LONG).show();
            return false;
        } else if (setPrivate.contains(to)) {
            Toast.makeText(getBaseContext(), "User profile is set to private", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public void pushNotification(){
        ParseObject sent = new ParseObject("MessageSent");
        sent.put("to", to);
        sent.saveInBackground();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICTURE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Read picked image data - its URI
            Uri pickedImage = data.getData();
            // Read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            customeImage = BitmapFactory.decodeFile(imagePath, options);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            customeImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitMapData = stream.toByteArray();

            cursor.close();


            //change button option to change image
            butAddImage.setText("Change Image");
        }
    }

}

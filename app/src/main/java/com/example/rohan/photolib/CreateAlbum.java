package com.example.rohan.photolib;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class CreateAlbum extends AppCompatActivity {

    ParseUser user;
    Boolean imageChanged;
    ParseObject albums, photos;
    byte[] bitMapData;
    Bitmap customImage;
    private Uri outputFileUri;
    final private int PICTURE_REQUEST = 1;
    ImageView ivAlbum;
    EditText etTitle;
    Switch swPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        etTitle = (EditText) findViewById(R.id.editTextEditTitle);
        swPrivacy = (Switch) findViewById(R.id.switchPrivacy);
        ivAlbum = (ImageView) findViewById(R.id.imageViewAlbumImage);
        swPrivacy.setChecked(true);
        imageChanged = false;

        Button addAlbumImage = (Button) findViewById(R.id.buttonAddAlbumImage);
        addAlbumImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumPickerIntent = new Intent(Intent.ACTION_PICK);
                albumPickerIntent.setType("image/*");
                startActivityForResult(albumPickerIntent, PICTURE_REQUEST);
                imageChanged = true;
            }
        });

        Button save = (Button) findViewById(R.id.buttonSaveAlbum);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update profile values and save to parse
                albums = new ParseObject("Albums");
                photos = new ParseObject("Photos");
                albums.put("creatorID", ParseUser.getCurrentUser());
                user = ParseUser.getCurrentUser();
                albums.put("creatorName", ParseUser.getCurrentUser().getUsername());
                albums.addAll("allowedUsers", Arrays.asList(ParseUser.getCurrentUser().getUsername()));
                albums.put("title", etTitle.getText().toString());
                photos.put("albumTitle", etTitle.getText().toString());
                if (swPrivacy.isChecked()) {
                    albums.put("public", true);
                } else {
                    albums.put("public", false);
                }

                //upload chosen image or default image
                if (imageChanged) {
                    //upload new image
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    customImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bitMapData = stream.toByteArray();
                    //insert default image
                    final ParseFile file = new ParseFile("albumCover.jpg", bitMapData);
                    file.saveInBackground(new SaveCallback() {

                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(CreateAlbum.this,
                                        "Error saving: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                albums.put("photo", file);
                                albums.saveInBackground();
                                photos.put("image", file);
                                photos.put("approved", true);
                                photos.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(CreateAlbum.this, "Album Saved Successfully!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(getBaseContext(), ViewAlbums.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
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
                    final ParseFile file = new ParseFile("albumCover.jpg", bitMapData);
                    file.saveInBackground(new SaveCallback() {

                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(CreateAlbum.this,
                                        "Error saving: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                albums.put("photo", file);
                                albums.saveInBackground();
                                photos.put("image", file);
                                photos.put("approved", true);
                                photos.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(CreateAlbum.this, "Album Saved Successfully!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(getBaseContext(), ViewAlbums.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
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
            customImage = BitmapFactory.decodeFile(imagePath, options);

            // Do something with the bitmap
            ivAlbum.setImageBitmap(customImage);
            imageChanged = true;

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

}

package com.example.rohan.photolib;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BasicViewPhotos extends AppCompatActivity {

    ListView listView;
    String albumTitle, albumID, username = "user";
    Button addPhoto;
    ArrayList<Photo> photos = new ArrayList<>();
    public static PhotosAdapter adapter = null;
    boolean hasAccess;
    ArrayList<String> users = new ArrayList<String>();
    JSONArray usersArray;
    final private int PICTURE_REQUEST = 1;
    byte[] bitMapData;
    Bitmap customImage;
    ParseObject newImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_view_photos);

        listView = (ListView) findViewById(R.id.listViewPhotosListing);

        if (getIntent().getExtras() != null){
            albumTitle = getIntent().getExtras().getString("ALBUM");
            albumID = getIntent().getExtras().getString("ID");
        }

        addPhoto = (Button) findViewById(R.id.addPhotosButton);
        addPhoto.setEnabled(false);

        ParseQuery<ParseObject> photosQuery = ParseQuery.getQuery("Photos");
        photosQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photoList, ParseException e) {
                if (e == null) {
                    //Log.d("demo", "Retrieved " + photoList.size() + " albums");
                    for (ParseObject p : photoList) {
                        if(p.getBoolean("approved")){
                            Photo photo = new Photo();
                            photo.setAlbum(p.getString("albumTitle"));
                            if (Objects.equals(photo.getAlbum(), albumTitle)) {
                                photo.setImageUrl(p.getParseFile("image").getUrl());
                                photo.setId(p.getObjectId());
                                photos.add(photo);
                            }
                        }
                    }

                    //Call listView adapter
                    adapter = new PhotosAdapter(BasicViewPhotos.this, R.layout.photos_list_layout, photos, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String temp2Id;
                            temp2Id = photos.get(position).getId();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Photos");
                            query.getInBackground(temp2Id, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    String tempUrl;
                                    tempUrl = object.getParseFile("image").getUrl();
                                    Intent i = new Intent(getBaseContext(), SingleImageView.class);
                                    i.putExtra("URL", tempUrl);
                                    startActivity(i);
                                }
                            });
                        }
                    });
                }
            }
        });

        ParseQuery<ParseObject> albumQuery = ParseQuery.getQuery("Albums");
        albumQuery.getInBackground(albumID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                usersArray = object.getJSONArray("allowedUsers");
                if (usersArray != null) {
                    int len = usersArray.length();
                    for (int i = 0; i < len; i++) {
                        try {
                            users.add(usersArray.get(i).toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                //sets true if allowed user
                if (users.contains(ParseUser.getCurrentUser().getUsername())) {
                    hasAccess = true;
                }
                if (hasAccess) {
                    addPhoto.setEnabled(true);
                }
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumPickerIntent = new Intent(Intent.ACTION_PICK);
                albumPickerIntent.setType("image/*");
                startActivityForResult(albumPickerIntent, PICTURE_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basic_view_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            newImage = new ParseObject("Photos");
            //upload new image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            customImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitMapData = stream.toByteArray();
            //insert default image
            final ParseFile file = new ParseFile("newImage.jpg", bitMapData);
            file.saveInBackground(new SaveCallback() {

                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(BasicViewPhotos.this,
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        newImage.put("image", file);
                        newImage.put("albumTitle", albumTitle);
                        newImage.put("approved", false);
                        newImage.put("addedBy", ParseUser.getCurrentUser().getUsername());
                        newImage.saveInBackground();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(BasicViewPhotos.this, "Photo awaiting approval from album owner", Toast.LENGTH_LONG).show();
                    }
                }
            });


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }
}

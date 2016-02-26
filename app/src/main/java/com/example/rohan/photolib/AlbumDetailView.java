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
import android.widget.Switch;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
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

public class AlbumDetailView extends AppCompatActivity {

    ParseObject photo = new ParseObject("Photos");
    ParseObject newImage;
    final private int PICTURE_REQUEST = 1;
    public static PhotosAdapter adapter = null;
    public static ProfileAdapter profAdapter = null;
    Button addUser, addPhoto, delete, saveAlbum, approvePhotos;
    String username = "user", albumTitle, albumID;
    Switch privacySetting;
    ListView listView;
    Boolean isPublic, hasAccess = false;
    byte[] bitMapData;
    Bitmap customImage;
    ArrayList<Photo> photos = new ArrayList<>();
    ArrayList<String> users = new ArrayList<String>();
    ArrayList<Album> albums = new ArrayList<>();
    JSONArray usersArray;
    ArrayList<Profile> profiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail_view);

        if (getIntent().getExtras() != null){
            albumTitle = getIntent().getExtras().getString("ALBUM");
            albumID = getIntent().getExtras().getString("ID");
        }

        addUser = (Button) findViewById(R.id.buttonAddUser);
        addPhoto = (Button) findViewById(R.id.buttonAddPhoto);
        delete = (Button) findViewById(R.id.buttonDeleteAlbum);
        saveAlbum = (Button) findViewById(R.id.albumSaveButton);
        approvePhotos = (Button) findViewById(R.id.buttonPending);
        privacySetting = (Switch) findViewById(R.id.switchAlbumPrivacy);

        listView = (ListView) findViewById(R.id.listViewPhotosListing);

        ParseQuery<ParseObject> albumQuery = ParseQuery.getQuery("Albums");
        albumQuery.getInBackground(albumID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                isPublic = object.getBoolean("public");
                if (isPublic) {
                    privacySetting.setChecked(true);
                } else {
                    privacySetting.setChecked(false);
                }
            }
        });

        updateList();

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SelectAddedUsers.class);
                i.putExtra("ALBUM", albumID);
                startActivity(i);
                finish();
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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<ParseObject> photosQuery = ParseQuery.getQuery("Photos");
                photosQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> photoList, ParseException e) {
                        if (e == null) {
                            for (ParseObject p : photoList) {
                                Photo photo = new Photo();
                                photo.setAlbum(p.getString("albumTitle"));
                                if (Objects.equals(photo.getAlbum(), albumTitle)) {
                                    p.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            ParseQuery<ParseObject> albumQuery = ParseQuery.getQuery("Albums");
                                            albumQuery.getInBackground(albumID, new GetCallback<ParseObject>() {
                                                @Override
                                                public void done(ParseObject object, ParseException e) {
                                                    object.deleteInBackground(new DeleteCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            Intent i = new Intent(getBaseContext(), ViewAlbums.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });

        saveAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> newAlbumQuery = ParseQuery.getQuery("Albums");
                newAlbumQuery.getInBackground(albumID, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (privacySetting.isChecked()) {
                            object.put("public", true);
                        } else {
                            object.put("public", false);
                        }
                        object.saveInBackground();
                    }
                });
            }
        });

        approvePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ApprovePhotos.class);
                i.putExtra("ALBUM", albumTitle);
                startActivity(i);
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_detail_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_photos:
                updateList();
                return true;
            case R.id.action_users:
                usersList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
                        Toast.makeText(AlbumDetailView.this,
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        newImage.put("image", file);
                        newImage.put("albumTitle", albumTitle);
                        newImage.put("approved", true);
                        newImage.put("addedBy", ParseUser.getCurrentUser().getUsername());
                        newImage.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                updateList();
                                Toast.makeText(AlbumDetailView.this, "Photo Saved Successfully!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    void updateList(){
        if(adapter != null){
            adapter.clear();
        }
        if(profAdapter != null){
            profAdapter.clear();
        }
        ParseQuery<ParseObject> photosQuery = ParseQuery.getQuery("Photos");
        photosQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photoList, ParseException e) {
                if (e == null) {
                    //Log.d("demo", "Retrieved " + photoList.size() + " albums");
                    for (ParseObject p : photoList) {
                        Photo photo = new Photo();
                        photo.setAlbum(p.getString("albumTitle"));
                        if (Objects.equals(photo.getAlbum(), albumTitle) && p.getBoolean("approved")) {
                            photo.setImageUrl(p.getParseFile("image").getUrl());
                            photo.setId(p.getObjectId());
                            photos.add(photo);
                        }
                    }
                    //Call listView adapter
                    adapter = new PhotosAdapter(AlbumDetailView.this, R.layout.photos_list_layout, photos, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            String tempId;
                            tempId = photos.get(position).getId();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Photos");
                            query.getInBackground(tempId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    object.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            updateList();
                                        }
                                    });
                                }
                            });
                            return false;
                        }
                    });

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
    }

    void usersList(){
        if(adapter != null){
            adapter.clear();
        }
        if(profAdapter != null){
            profAdapter.clear();
        }
        ParseQuery<ParseObject> albumQuery = ParseQuery.getQuery("Albums");
        albumQuery.getInBackground(albumID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    users.clear();
                    //get list of allowed users
                    usersArray = object.getJSONArray("allowedUsers");
                    Log.d("userList", usersArray.toString());
                    if (usersArray != null) {
                        int len = usersArray.length();
                        for (int i = 0; i < len; i++) {
                            try {
                                users.add(usersArray.get(i).toString());
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        Log.d("moreUsers", users.toString());
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> profileList, ParseException e) {
                            if (e == null) {
                                Log.d("demo", "Retrieved " + profileList.size() + " profiles");
                                for (ParseObject p : profileList) {
                                    Profile profile = new Profile();
                                    try {
                                        if (users.contains(p.getParseUser("createdBy").fetchIfNeeded().getUsername())) {
                                            profile.setName(p.getString("Name"));
                                            profile.setGender(p.getString("Gender"));
                                            profile.setId(p.getObjectId());
                                            profile.setImageUrl(p.getParseFile("Image").getUrl());

                                            //get ParseUser who owns profile
                                            ParseUser pu = p.getParseUser("createdBy");
                                            try {
                                                pu.fetchIfNeeded().getUsername();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                            profile.setUserName(pu.getUsername());
                                            profiles.add(profile);
                                        }
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                                }

                                //Call listView adapter
                                profAdapter = new ProfileAdapter(AlbumDetailView.this, R.layout.list_layout, profiles, username);
                                listView.setAdapter(profAdapter);

                                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
                                        query.getInBackground(albumID, new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                Log.d("objectCreator", object.get("creatorName").toString());
                                                Log.d("objectTitle", object.get("title").toString());
                                                ArrayList<String> tempUsers = new ArrayList<String>();
                                                Log.d("test1", tempUsers.toString());
                                                tempUsers.add(profiles.get(position).getUserName());
                                                Log.d("test2", tempUsers.toString());
                                                object.removeAll("allowedUsers", tempUsers);
                                                object.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        usersList();
                                                    }
                                                });
                                            }
                                        });
                                        return false;
                                    }
                                });

                            } else {
                                Log.d("demo", "Error: " + e.getMessage());
                            }
                        }
                    });

                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(getBaseContext(), ViewAlbums.class);
        startActivity(i);
        finish();
    }
}
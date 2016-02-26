package com.example.rohan.photolib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApprovePhotos extends AppCompatActivity {

    String albumTitle, username = "user";
    ArrayList<Photo> photos = new ArrayList<>();
    public static PhotosAdapter adapter = null;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_photos);

        if (getIntent().getExtras() != null){
            albumTitle = getIntent().getExtras().getString("ALBUM");
        }

        listView = (ListView) findViewById(R.id.listViewApproveListing);

        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_approve_photos, menu);
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

    public void updateList(){
        if(adapter != null){
            adapter.clear();
        }
        ParseQuery<ParseObject> photosQuery = ParseQuery.getQuery("Photos");
        photosQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photoList, ParseException e) {
                if (e == null) {
                    for (ParseObject p : photoList) {
                        if(!p.getBoolean("approved")){
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
                    adapter = new PhotosAdapter(ApprovePhotos.this, R.layout.photos_list_layout, photos, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            String tempId;
                            tempId = photos.get(position).getId();
                            ParseQuery<ParseObject> deleteQuery = ParseQuery.getQuery("Photos");
                            deleteQuery.getInBackground(tempId, new GetCallback<ParseObject>() {
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
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            String temp2Id;
                            temp2Id = photos.get(position).getId();
                            Log.d("tempID", temp2Id);
                            ParseQuery<ParseObject> approveQuery = ParseQuery.getQuery("Photos");
                            approveQuery.getInBackground(temp2Id, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    Log.d("object", object.toString());
                                    object.put("approved", true);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            updateList();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}

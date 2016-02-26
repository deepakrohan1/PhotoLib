package com.example.rohan.photolib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListing extends AppCompatActivity {

    ParseObject profile;
    ArrayList<Profile> profiles = new ArrayList<>();
    public static ProfileAdapter adapter = null;
    String username = "user";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listing);

        listView = (ListView) findViewById(R.id.listViewUserListing);

        //receive list
        if (getIntent().getExtras() != null){
            profiles = (ArrayList<Profile>) getIntent().getExtras().getSerializable("PROFILES");
        }

        //Get listing of all user profiles
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> profileList, ParseException e) {
                if (e == null) {
                    Log.d("demo", "Retrieved " + profileList.size() + " profiles");
                    for (ParseObject p : profileList) {
                        Log.d("demo", p.getString("Name"));
                        Profile profile = new Profile();
                        if (p.getBoolean("Public") == true) {
                            profile.setName(p.getString("Name"));
                            profile.setGender(p.getString("Gender"));
                            profile.setId(p.getObjectId());
                            profile.setImageUrl(p.getParseFile("Image").getUrl());
                            profile.setIsPublic(p.getBoolean("Public"));

                            //get ParseUser who owns profile
                            ParseUser pu = p.getParseUser("createdBy");
                            try {
                                pu.fetchIfNeeded().getUsername();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                profile.setUserName(pu.fetchIfNeeded().getUsername());
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            profiles.add(profile);
                        }
                    }

                    //Call listView adapter
                    adapter = new ProfileAdapter(UserListing.this, R.layout.list_layout, profiles, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(getBaseContext(), ProfileDetail.class);
                            i.putExtra("PROFILE", profiles.get(position));
                            i.putExtra("PROFILES", profiles);
                            startActivity(i);
                        }
                    });
                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });



    }

    public void refreshList(){
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}

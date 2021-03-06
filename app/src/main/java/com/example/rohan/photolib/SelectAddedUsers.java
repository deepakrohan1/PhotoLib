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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class SelectAddedUsers extends AppCompatActivity {

    ParseObject profile;
    ArrayList<Profile> profiles = new ArrayList<>();
    public static ProfileAdapter adapter = null;
    String username = "user", albumID;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listing);

        listView = (ListView) findViewById(R.id.listViewUserListing);

        if (getIntent().getExtras() != null){
            albumID = getIntent().getExtras().getString("ALBUM");
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
                        if (p.getBoolean("Public")) {
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
                            try {
                                profile.setUserName(pu.fetchIfNeeded().getUsername());
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            profiles.add(profile);
                        }
                    }

                    //Call listView adapter
                    adapter = new ProfileAdapter(SelectAddedUsers.this, R.layout.list_layout, profiles, username);
                    listView.setAdapter(adapter);

                    //Set listener to select a new user
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            ParseQuery<ParseObject> albumQuery = ParseQuery.getQuery("Albums");
                            albumQuery.getInBackground(albumID, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if(object.containsKey(profiles.get(position).getUserName())){
                                        Toast.makeText(SelectAddedUsers.this, "User already has access to this album", Toast.LENGTH_LONG).show();
                                    } else {
                                        object.addUnique("allowedUsers", profiles.get(position).getUserName());
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                /*ParseUser.logOut();
                Intent j = new Intent(ToDoList.this, ToDoMain.class);
                finish();
                startActivity(j);*/
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

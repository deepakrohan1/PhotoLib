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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ViewAlbums extends AppCompatActivity {

    ParseObject album = new ParseObject("Albums");
    ArrayList<Album> albums = new ArrayList<>();
    ArrayList<String> users = new ArrayList<String>();
    public static AlbumsAdapter adapter = null;
    String username = "user";
    ListView listView;
    JSONArray usersArray;
    boolean hasAccess = false, ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_albums);

        listView = (ListView) findViewById(R.id.listViewAlbumListing);

        username = ParseUser.getCurrentUser().getUsername();
        Log.d("test1", username);

        //Get listing of all user albums
        allAlbums();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_albums, menu);
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
            case R.id.action_all:
                adapter.clear();
                hasAccess = false;
                allAlbums();
                return true;
            case R.id.action_mine:
                adapter.clear();
                hasAccess = false;
                myAlbums();
                return true;
            case R.id.action_shared:
                adapter.clear();
                hasAccess = false;
                sharedAlbums();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void allAlbums(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> albumList, ParseException e) {
                if (e == null) {
                    for (ParseObject p : albumList) {
                        hasAccess = false;
                        users.clear();
                        //get list of allowed users
                        usersArray = p.getJSONArray("allowedUsers");
                        Log.d("demo", usersArray.toString());
                        if(usersArray != null){
                            int len = usersArray.length();
                            for(int i = 0; i < len; i++){
                                try {
                                    users.add(usersArray.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        Log.d("demo", users.toString());
                        //sets true if allowed user
                        if(users.contains(username)){
                            Log.d("demo", "matched user");
                            hasAccess = true;
                        }
                        //Log.d("demo", p.getString("Name"));
                        Album album = new Album();
                        if (p.getBoolean("public") || hasAccess) {
                            album.setTitle(p.getString("title"));
                            album.setPrivacy(p.getBoolean("public"));
                            album.setAuthor(p.getObjectId());
                            album.setImageUrl(p.getParseFile("photo").getUrl());
                            album.setId(p.getObjectId());

                            //get ParseUser who owns album
                            ParseUser pu = p.getParseUser("creatorID");
                            try {
                                pu.fetchIfNeeded().getUsername();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            album.setAuthor(pu.getUsername());
                            albums.add(album);
                        }
                    }

                    //Call listView adapter
                    adapter = new AlbumsAdapter(ViewAlbums.this, R.layout.album_list_layout, albums, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(albums.get(position).getAuthor()== ParseUser.getCurrentUser().getUsername()){
                                Intent i = new Intent(getBaseContext(), AlbumDetailView.class);
                                i.putExtra("ALBUM", albums.get(position).getTitle());
                                i.putExtra("ID", albums.get(position).getId());
                                startActivity(i);
                            }else{
                                Intent i = new Intent(getBaseContext(), BasicViewPhotos.class);
                                i.putExtra("ALBUM", albums.get(position).getTitle());
                                i.putExtra("ID", albums.get(position).getId());
                                startActivity(i);
                            }

                        }
                    });

                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void myAlbums(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> albumList, ParseException e) {
                if (e == null) {
                    for (ParseObject p : albumList) {
                        hasAccess = false;
                        //sets true if user is the owner
                        if(p.getString("creatorName").equals(ParseUser.getCurrentUser().getUsername())){
                            hasAccess = true;
                        }
                        Album album = new Album();
                        if (hasAccess) {
                            album.setTitle(p.getString("title"));
                            album.setPrivacy(p.getBoolean("public"));
                            album.setAuthor(p.getObjectId());
                            album.setImageUrl(p.getParseFile("photo").getUrl());
                            album.setId(p.getObjectId());

                            //get ParseUser who owns album
                            ParseUser pu = p.getParseUser("creatorID");
                            try {
                                pu.fetchIfNeeded().getUsername();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            album.setAuthor(pu.getUsername());
                            albums.add(album);
                        }
                    }

                    //Call listView adapter
                    adapter = new AlbumsAdapter(ViewAlbums.this, R.layout.album_list_layout, albums, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(albums.get(position).getAuthor()== ParseUser.getCurrentUser().getUsername()){
                                Intent i = new Intent(getBaseContext(), AlbumDetailView.class);
                                i.putExtra("ALBUM", albums.get(position).getTitle());
                                i.putExtra("ID", albums.get(position).getId());
                                startActivity(i);
                            }else{
                                Intent i = new Intent(getBaseContext(), BasicViewPhotos.class);
                                i.putExtra("ALBUM", albums.get(position).getTitle());
                                i.putExtra("ID", albums.get(position).getId());
                                startActivity(i);
                            }

                        }
                    });

                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void sharedAlbums(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> albumList, ParseException e) {
                if (e == null) {
                    for (ParseObject p : albumList) {
                        users.clear();
                        hasAccess = false;
                        //get list of allowed users
                        usersArray = p.getJSONArray("allowedUsers");
                        if(usersArray != null){
                            int len = usersArray.length();
                            for(int i = 0; i < len; i++){
                                try {
                                    users.add(usersArray.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        //sets true if allowed user
                        if(users.contains(username)){
                            hasAccess = true;
                        }
                        //Log.d("demo", p.getString("Name"));
                        Album album = new Album();
                        if (hasAccess) {
                            album.setTitle(p.getString("title"));
                            album.setPrivacy(p.getBoolean("public"));
                            album.setAuthor(p.getObjectId());
                            album.setImageUrl(p.getParseFile("photo").getUrl());
                            album.setId(p.getObjectId());

                            //get ParseUser who owns album
                            ParseUser pu = p.getParseUser("creatorID");
                            try {
                                pu.fetchIfNeeded().getUsername();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            album.setAuthor(pu.getUsername());
                            albums.add(album);
                        }
                    }

                    //Call listView adapter
                    adapter = new AlbumsAdapter(ViewAlbums.this, R.layout.album_list_layout, albums, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(albums.get(position).getAuthor()== ParseUser.getCurrentUser().getUsername()){
                                Intent i = new Intent(getBaseContext(), AlbumDetailView.class);
                                i.putExtra("ALBUM", albums.get(position).getTitle());
                                i.putExtra("ID", albums.get(position).getId());
                                startActivity(i);
                            }else{
                                Intent i = new Intent(getBaseContext(), BasicViewPhotos.class);
                                i.putExtra("ALBUM", albums.get(position).getTitle());
                                i.putExtra("ID", albums.get(position).getId());
                                startActivity(i);
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
        Intent i = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(i);
    }
}

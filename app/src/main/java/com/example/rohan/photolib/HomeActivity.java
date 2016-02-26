package com.example.rohan.photolib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {
    TextView textView;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
            userName = ParseUser.getCurrentUser().getUsername();

//        TODO: Design a Profile Page

        textView = (TextView)findViewById(R.id.userName);
        textView.setText("Welcome to BookFace " + userName);

        Button btnMessages = (Button) findViewById(R.id.buttonMessages);
        btnMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(HomeActivity.this, MessageInbox.class);
                startActivity(j);
            }
        });

        Button btnEditProfile = (Button) findViewById(R.id.buttonEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(HomeActivity.this, ProfileEdit.class);
                startActivity(j);
            }
        });

        Button btnEditAlbums = (Button) findViewById(R.id.buttonEditAlbum);
        btnEditAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(HomeActivity.this, CreateAlbum.class);
                startActivity(j);
            }
        });

        Button btnBrowseProfiles = (Button) findViewById(R.id.buttonProfiles);
        btnBrowseProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(HomeActivity.this, UserListing.class);
                startActivity(j);
            }
        });

        Button btnBrowseAlbums = (Button) findViewById(R.id.buttonAlbums);
        btnBrowseAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(HomeActivity.this, ViewAlbums.class);
                startActivity(j);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        ParseUser.logOut();
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}

package com.example.rohan.photolib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileDetail extends AppCompatActivity {

    Profile profile;
    ArrayList<Profile> profiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //implementation !! Check how current user is being shown for visibility of edit button !!
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.d("demo", "Current user is: " + currentUser);

        //receive list
        if (getIntent().getExtras() != null){
            profile = (Profile) getIntent().getExtras().getSerializable("PROFILE");
            profiles = (ArrayList<Profile>) getIntent().getExtras().getSerializable("PROFILES");
        }

        TextView tvName = (TextView) findViewById(R.id.textViewProfileName);
        tvName.setText(profile.getName());

        TextView tvGender = (TextView) findViewById(R.id.textViewProfileGender);
        tvGender.setText(profile.getGender());

        ImageView iv = (ImageView) findViewById(R.id.imageViewProfilePic);
        Picasso.with(ProfileDetail.this).load(profile.getImageUrl()).into(iv);

        Button butEdit = (Button) findViewById(R.id.buttonEdit);
        if (currentUser.getUsername().equals(profile.getUserName())){
            butEdit.setEnabled(true);
            butEdit.setVisibility(View.INVISIBLE);

        } else {
            butEdit.setText("Send Message");
            butEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getBaseContext(), ComposeMessage.class);
                    i.putExtra("PROFILE", profile);
                    i.putExtra("PROFILES", profiles);
                    i.putExtra("TOUSER", profile.getUserName());        //!! Needs to be changed to userId in implementation !!
                    startActivity(i);
                }
            });
        }

    }
}

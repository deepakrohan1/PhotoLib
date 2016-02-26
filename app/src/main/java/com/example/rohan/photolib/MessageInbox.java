package com.example.rohan.photolib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageInbox extends AppCompatActivity {

    ParseObject message;
    ArrayList<Message> messages = new ArrayList<>();
    ArrayList<Profile> profiles = new ArrayList<>();
    public static MessageAdapter adapter = null;
    ListView listView;
    String username = "user";
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_inbox);

        listView = (ListView) findViewById(R.id.listViewMessages);

        //Get current user
        currentUser = ParseUser.getCurrentUser();

        getList();
    }

    public void getList(){
        listView.setAdapter(null);
        messages.clear();
        profiles.clear();

        //Get listing of all user profiles for privacy
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Profile");
        query2.findInBackground(new FindCallback<ParseObject>() {
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
                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });


        //Get listing of all user's messages
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messageList, ParseException e) {
                if (e == null) {
                    Log.d("demo", "Retrieved " + messageList.size() + " messages");
                    for (ParseObject p : messageList) {
                        Log.d("demo", p.getString("To"));

                        //add message only if addressed to logged in user
                        if (p.getString("To").equals(currentUser.getUsername())) {
                            Message message = new Message();
                            message.setFrom(p.getString("From"));
                            message.setTo(p.getString("To"));
                            message.setId(p.getObjectId());
                            message.setBody(p.getString("Body"));
                            message.setSubject(p.getString("Subject"));

                            Date date = p.getCreatedAt();
                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            message.setTime(df.format(date));

                            message.setRead(p.getBoolean("Read"));

                            if (p.getParseFile("Image") != null) {
                                message.setImage(p.getParseFile("Image").getUrl());
                            }
                            Log.d("demo", message.toString());
                            messages.add(message);
                        }
                    }

                    //Call listView adapter
                    adapter = new MessageAdapter(MessageInbox.this, R.layout.message_list_layout, messages, username);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(getBaseContext(), MessageDetail.class);
                            i.putExtra("MESSAGE", messages.get(position));
                            i.putExtra("PROFILES", profiles);
                            startActivityForResult(i, 1);
                        }
                    });
                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("demo", "Just deleted a message");
                    Toast.makeText(MessageInbox.this, "Message has been deleted", Toast.LENGTH_SHORT).show();
                    getList();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    getList();
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_compose:
                Intent j = new Intent(MessageInbox.this, ComposeMessage.class);
                j.putExtra("PROFILES", profiles);
                j.putExtra("TOUSER", "");        //blank to show not opening with user selected
                startActivity(j);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

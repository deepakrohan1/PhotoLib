package com.example.rohan.photolib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageDetail extends AppCompatActivity {

    Message message;
    ArrayList<Profile> profiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        //receive list
        if (getIntent().getExtras() != null){
            profiles = (ArrayList<Profile>) getIntent().getExtras().getSerializable("PROFILES");
            message = (Message) getIntent().getExtras().getSerializable("MESSAGE");
        }

        //set Message to read
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.getInBackground(message.getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put("Read", true);
                    object.saveInBackground();
                } else {
                    // something went wrong

                }
            }
        });

        TextView tvFrom = (TextView) findViewById(R.id.textViewFromDetail);
        tvFrom.setText("FROM: " + message.getFrom());

        TextView tvSubject = (TextView) findViewById(R.id.textViewSubjectDetail);
        tvSubject.setText("SUBJECT: " + message.getSubject());

        //Time Section
        TextView tvTime = (TextView) findViewById(R.id.textViewTimeDetail);
        tvTime.setText("TIME: " + (CharSequence) message.getTime());

        TextView tvBody = (TextView) findViewById(R.id.textViewBody);
        tvBody.setText(message.getBody());

        if (message.getImage() != null) {
            ImageView iv = (ImageView) findViewById(R.id.imageViewMessage);
            Picasso.with(MessageDetail.this).load(message.getImage()).into(iv);
        }

        Button butReply = (Button) findViewById(R.id.buttonReply);
        butReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(MessageDetail.this, ComposeMessage.class);
                j.putExtra("PROFILES", profiles);
                j.putExtra("TOUSER", message.getFrom());
                startActivity(j);
            }
        });

        Button butDelete = (Button) findViewById(R.id.buttonDelete);
        butDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
                query.getInBackground(message.getId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            object.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Intent i = new Intent();
                                    setResult(Activity.RESULT_OK, i);
                                    finish();
                                }
                            });


                        } else {
                            // something went wrong

                        }
                    }
                });
            }
        });

    }

    //Refresh when back button is pressed to fix isRead()
    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}

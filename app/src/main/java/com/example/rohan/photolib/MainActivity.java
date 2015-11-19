package com.example.rohan.photolib;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, SignupFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Get the Login Fragment
         */
        getFragmentManager().beginTransaction().add(R.id.container, new LoginFragment(), "login").commit();


        /**
         * Twitter Login
         */

//        ParseTwitterUtils.logIn(this, new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException err) {
//                if (user == null) {
//                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
//                } else if (user.isNew()) {
//                    Log.d("MyApp", "User signed up and logged in through Twitter!");
//                } else {
//                    Log.d("MyApp", "User logged in through Twitter!");
//                }
//            }
//        });


        /**
         * Push for a Channel of Giants
         */

//        ParsePush push = new ParsePush();
//        push.setChannel("Giants");
//        push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
//        push.sendInBackground();
//        Log.d("s", "push triggered");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * From Login
     */
    @Override
    public void onFragmentInteraction() {

    }

    @Override
    public void callSignUp() {
            getFragmentManager().beginTransaction().addToBackStack(null)
                    .add(R.id.container, new SignupFragment(), "signup").commit();
    }
    /**
     *
     */
}

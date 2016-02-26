package com.example.rohan.photolib;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
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
         * Push for a Channel of Giants
         */

//        ParsePush push = new ParsePush();
//        push.setChannel("Giants");
//        push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
//        push.sendInBackground();
//        Log.d("s", "push triggered");
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
                    .replace(R.id.container, new SignupFragment(), "signup").commit();
    }
    /**
     * From Signup Fragment
     */

    @Override
    public void onCancelButton() {
        getFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment(),"login").commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("loginFBOnAct","Code reaches here");
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
//        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag("login");
//        fragment.onActivityResult(requestCode,resultCode,data);
    }
}

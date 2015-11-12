package com.example.rohan.photolib;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by rohan on 11/11/15.
 */
public class ParseInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * ParseInt Permissions
         */

        Parse.initialize(getApplicationContext(), "Bgyv9Ai6dejljuTV1pRvT9AsvRR8HO89ZrfNSJN9", "W7QHGsjlDBywthI4h1c2x99F3Aixxx274qHLWG0R");
        ParseInstallation.getCurrentInstallation().saveInBackground();




    }
}

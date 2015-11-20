package com.example.rohan.photolib;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
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
        ParseTwitterUtils.initialize("bzmEZ1F8m2W67Q3EJ0ql9Pjsy", "QaornL5u67hKFXUVcTadtRHpOdJZf08emJwZjrRMfHllWO6tzp");
//        ParseTwitterUtils.getTwitter().getScreenName();


        ParseInstallation.getCurrentInstallation().saveInBackground();




    }
}

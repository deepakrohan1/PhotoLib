package com.example.rohan.photolib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;
import com.parse.SaveCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "baerveVgCAjx7hfyCuUEFephpLPfgrK7cXsOQvXo", "nvAbMXY2sQfaJhJ51raDrCs1FmtqJR15KnIHSQ8o");
         ParseInstallation.getCurrentInstallation().saveInBackground();
        /**
         * Twitter
         */
        ParseTwitterUtils.initialize("bzmEZ1F8m2W67Q3EJ0ql9Pjsy", "QaornL5u67hKFXUVcTadtRHpOdJZf08emJwZjrRMfHllWO6tzp");
        /**
         * FB
         */
        ParseFacebookUtils.initialize(getApplicationContext());
        Log.d("parseinit", getApplicationContext().toString());
        printHaskKey();
    }

    public void printHaskKey() {
    //TODO: Change the Package names when you update
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.rohan.photolib",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
}

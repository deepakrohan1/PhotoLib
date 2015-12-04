package com.example.rohan.photolib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    EditText editTextUsername, editTextPassword;
    Button buttonLogin, buttonSignup;
    ImageView imageViewFacebook, imageViewTwitter;
    String username = "", password = "";
    List<String> permissions;
    String email, name;
    ParseUser parseUser;

    //    private Pattern pattern; //Get Email Working
//    public Matcher matcher;
    User user = new User();


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        intializeUI();
//        pattern = Pattern.compile(SignupFragment.EMAIL_PATTERN);

        ParseUser.logOut(); //TODo remvove this at the end
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                Log.d("login", username + "--Pass--" + password);

                if (isValidCreds(username, password)) ;
                {
                    //TODO: Authenticate users
                }
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callSignUp();
            }
        });

        /**
         * Twitter Login
         */

        imageViewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("loginTw", "Uh oh. The user cancelled the Twitter login.");
                        } else if (user.isNew()) {
                            Log.d("loginTw", "User signed up and logged in through Twitter!");
//                    Log.d("loginTw", ParseTwitterUtils.getTwitter().getScreenName().toString());

                        } else {
                            Log.d("loginTw", "User logged in through Twitter!");
                            Log.d("loginTw", ParseTwitterUtils.getTwitter().getScreenName().toString());

                        }
                    }
                });
            }
        });

        /**
         * FB Auth
         */

        imageViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("loginFB", "Inside Button Click");
//                matcher = pattern.matcher(email);

                ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(),
                        permissions = Arrays.asList("public_profile", "email"),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                Log.d("loginFB", "Testing FB login");
                                if (err != null) {
                                    Log.d("loginFB", "This is an error: " + err.toString());
                                }
                                if (user == null) {
                                    Log.d("loginFB", "Uh oh. The user cancelled the Facebook login.");
                                } else if (user.isNew()) {
                                    Log.d("loginFB", "User signed up and logged in through Facebook!" + ParseUser.getCurrentUser());
                                    fbLoadUserDetails();
                                } else {
                                    Log.d("loginFB", "User logged in through Facebook!" + ParseUser.getCurrentUser());
                                    Intent i = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(i);
//                                    saveUserDetails();
                                }

                            }
                        });

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();

        public void callSignUp();
    }

    /**
     * Intializations
     */
    public void intializeUI() {
        buttonLogin = (Button) getView().findViewById(R.id.buttonLogin);
        buttonSignup = (Button) getView().findViewById(R.id.buttonSignup);

        editTextUsername = (EditText) getView().findViewById(R.id.editTextName);
        editTextPassword = (EditText) getView().findViewById(R.id.editTexPassword);

        imageViewFacebook = (ImageView) getView().findViewById(R.id.imageViewFacebook);
        imageViewTwitter = (ImageView) getView().findViewById(R.id.imageViewTwitter);
    }

    public boolean isValidCreds(String username, String password) {

        if (username.trim().equals("") || password.trim().equals("")) {
            Toast.makeText(getActivity(), "Enter the User Credentials", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username.contains(" ")) {
            Toast.makeText(getActivity(), "Invalid Username", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }

    /**
     * TODO Getting FB Online :)
     */
    private void fbLoadUserDetails() {
        Log.d("loginfbsave", "inside user det");
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Log.d("loginfbsave", "Accessing from id: " + profile.getFirstName());
        } else {
            return;
        }
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Log.d("loginfbsave", "saving to parse with details");

                        try {
                            String[] test = {};
                            name = graphResponse.getJSONObject().getString("name");
                            user.setName(name);
                            email = graphResponse.getJSONObject().getString("email");
                            user.setEmail(email);
                            Log.d("loginfbsave", "user--" + user.toString() + "--email--" + email.toString());
                            /**
                             * Saving to Parse
                             */
                            Log.d("loginFBload", "updating parse site with details");
                            parseUser = ParseUser.getCurrentUser();
                            parseUser.put("name", name);
                            parseUser.setUsername(email.split("@")[0]); //Takes the deepakrohan from deepakrohan@facebook.com
                            parseUser.setEmail(email);

                            parseUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.d("loginFBErr", "Storing Parse error: " + e.toString());
                                    } else {
                                        Toast.makeText(getActivity(), "name:" + name + " email: " + email, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            Log.d("loginfbsaveErr", e.toString());
                        }
                    }
                }).executeAsync();
    }
}

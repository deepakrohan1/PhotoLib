package com.example.rohan.photolib;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SignupFragment extends Fragment {

    Button buttonSignup, buttonCancel;
    EditText editTextName, editTextUsername, editTextPassword, editTextCPassword, editTextEmail;
    String name = "", username = "", password = "", cPassword = "", email = "";
    private Pattern pattern; //Get Email Working
    public Matcher matcher;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private OnFragmentInteractionListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

//    // TODO: Rename method, update argument and hook method into UI event
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeUI();
        pattern = Pattern.compile(EMAIL_PATTERN);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                cPassword = editTextCPassword.getText().toString();
                email = editTextEmail.getText().toString();

                Log.d("signup", "name--" + name + "--username--" + username + "--password--" + password + "--cPass--" + cPassword +
                        "--emaill--" + email);
                if (isValid(name, email, username, password, cPassword)) {

                    //TODO Implementation of Signup
                    final ParseQuery<ParseUser> user = ParseUser.getQuery();
                    user.whereEqualTo("username", username);
                    user.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                if (objects.size() > 0) {
                                    Toast.makeText(getActivity(), "The username has been taken " + username
                                            + " ,choose another", Toast.LENGTH_SHORT).show();
                                } else {
                                    ParseUser createUser = new ParseUser();
                                    createUser.setUsername(username);
                                    createUser.setPassword(password);
                                    createUser.setEmail(email);
                                    createUser.put("name", name);
                                    createUser.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity(), "New user has been created", Toast.LENGTH_SHORT).show();
                                                mListener.onCancelButton();
                                            } else {
                                                Log.d("signupErr", "Err Signnup Act: " + e.toString());
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });


                } else {
                    Log.d("signupErr", "Check for the Error");
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelButton();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();

        public void onCancelButton();
    }

    private void initializeUI() {
        buttonSignup = (Button) getView().findViewById(R.id.buttonSignup);
        buttonCancel = (Button) getView().findViewById(R.id.buttonCancel);
        editTextName = (EditText) getView().findViewById(R.id.editTextName);
        editTextUsername = (EditText) getView().findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) getView().findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) getView().findViewById(R.id.editTextPassword);
        editTextCPassword = (EditText) getView().findViewById(R.id.editTextCPassword);
    }

    public boolean isValid(String name, String email, String username, String password, String cPassword) {
        matcher = pattern.matcher(email);

        if (name.trim().equals("") || username.trim().equals("") || email.trim().equals("") ||
                password.trim().equals("") || cPassword.trim().equals("")) {
            Toast.makeText(getActivity(), "Don't leave the fields Empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username.contains(" ")) {
            Toast.makeText(getActivity(), "No space in username field", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.trim().equals(cPassword.trim())) {
            Toast.makeText(getActivity(), "The passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!matcher.matches()) {
            Toast.makeText(getActivity(), "Enter a valid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

}

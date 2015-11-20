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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SignupFragment extends Fragment {

    Button buttonSignup, buttonCancel;
    EditText editTextName, editTextUsername, editTextPassword, editTextCPassword;
    String name = "", username = "", password = "", cPassword="";

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

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                cPassword = editTextCPassword.getText().toString();

                Log.d("signup", "name--" + name + "--username--" + username + "--password--" + password + "--cPass--" + cPassword);
                if (isValid(name, username, password, cPassword)) {

                    //TODO check for user existance on the server
                    if (checkExistance(username)) {
                        Toast.makeText(getActivity(),"The username" +username+ " is already taken up.", Toast.LENGTH_SHORT).show();

                    } else {
//                        TODO implement Signup
                    }

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
     * <p>
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
        editTextPassword = (EditText) getView().findViewById(R.id.editTextPassword);
        editTextCPassword = (EditText) getView().findViewById(R.id.editTextCPassword);
    }

    public boolean isValid(String name, String username, String password, String cPassword){

        if(name.trim().equals("") || username.trim().equals("") || password.trim().equals("") || cPassword.trim().equals("")){
            Toast.makeText(getActivity(),"Don't leave the fields Empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.trim().equals(cPassword.trim())){
            Toast.makeText(getActivity(),"The passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    public boolean checkExistance(String username){
        ParseQuery<ParseUser> users = ParseUser.getQuery();
        users.whereEqualTo("username", username);
        final boolean[] foundUser = {false};
        users.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    Log.d("signupGetObj","Found :"+objects.size());
                    if(objects.size() > 0){
                         foundUser[0] = true;
                    }else{
                        foundUser[0] = false;
                    }
                }else{
                    Log.d("signupGetErr", "Parse error: "+e.toString());
                }
            }

        });
        Log.d("signupUser","User Found "+foundUser[0]);

        return foundUser[0];
    }

}

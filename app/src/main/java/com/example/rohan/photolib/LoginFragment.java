package com.example.rohan.photolib;

import android.app.Activity;
import android.net.Uri;
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
    String username = "", password="";
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
    public void intializeUI(){
        buttonLogin = (Button) getView().findViewById(R.id.buttonLogin);
        buttonSignup = (Button) getView().findViewById(R.id.buttonSignup);

        editTextUsername = (EditText)getView().findViewById(R.id.editTextUsername);
        editTextPassword = (EditText)getView().findViewById(R.id.editTexPassword);

        imageViewFacebook = (ImageView)getView().findViewById(R.id.imageViewFacebook);
        imageViewTwitter = (ImageView)getView().findViewById(R.id.imageViewTwitter);
    }

    public boolean isValidCreds(String username, String password){

        if(username.trim().equals("") || password.trim().equals("")){
            Toast.makeText(getActivity(),"Enter the User Credentials",Toast.LENGTH_SHORT).show();
            return false;
//            TODO: ADD else if to check for email
        }else{
            return true;
        }
    }
}

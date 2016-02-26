//Midterm Makeup
//Justin Campbell
package com.example.rohan.photolib;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProfileAdapter extends ArrayAdapter<Profile> {
    Context mContext;
    int mResource;
    List<Profile> mObjects;
    String thisUser;
    ArrayList<Profile> profiles = new ArrayList<>();
    Profile p;
    ParseFile image;
    View mConvertView;


    public ProfileAdapter(Context context, int resource, List<Profile> objects, String userName) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mObjects = objects;
        this.thisUser = userName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mConvertView = convertView;
        Log.d("parseDemo", "USername : " + thisUser);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (mConvertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mConvertView = inflater.inflate(mResource, parent, false);
        }
        p = mObjects.get(position);

        TextView textViewUserName = (TextView) mConvertView.findViewById(R.id.textViewUserName);
        textViewUserName.setText(p.getName());

        ImageView iv = (ImageView) mConvertView.findViewById(R.id.imageViewPreview);
        Picasso.with(mContext).load(p.getImageUrl()).resize(100,100).into(iv);

    return mConvertView;

    }
}

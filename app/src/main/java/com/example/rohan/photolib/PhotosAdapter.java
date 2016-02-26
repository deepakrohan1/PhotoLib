package com.example.rohan.photolib;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 12/11/2015.
 */
public class PhotosAdapter extends ArrayAdapter<Photo>{
    Context mContext;
    int mResource;
    List<Photo> mObjects;
    String thisUser;
    ArrayList<Photo> photos = new ArrayList<>();
    //Album a;
    Photo p;
    ParseFile image;
    View mConvertView;
    boolean isFull = false;


    public PhotosAdapter(Context context, int resource, List<Photo> objects, String userName) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mObjects = objects;
        this.thisUser = userName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
        mConvertView = convertView;
        Log.d("parseDemo", "USername : " + thisUser);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (mConvertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mConvertView = inflater.inflate(mResource, parent, false);
        }
        p = mObjects.get(position);

        final ImageView iv = (ImageView) mConvertView.findViewById(R.id.imageViewPreview);
        Picasso.with(mContext).load(p.getImageUrl()).resize(300,300).into(iv);

        return mConvertView;
    }
}

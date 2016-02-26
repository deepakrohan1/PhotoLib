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

/**
 * Created by Erik on 12/10/2015.
 */
public class AlbumsAdapter extends ArrayAdapter<Album>{
    Context mContext;
    int mResource;
    List<Album> mObjects;
    String thisUser;
    ArrayList<Album> albums = new ArrayList<>();
    Album a;
    ParseFile image;
    View mConvertView;


    public AlbumsAdapter(Context context, int resource, List<Album> objects, String userName) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mObjects = objects;
        this.thisUser = userName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
        mConvertView = convertView;
        Log.d("parseDemo", "USername : " + thisUser);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (mConvertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mConvertView = inflater.inflate(mResource, parent, false);
        }
        a = mObjects.get(position);

        TextView albumTitle = (TextView) mConvertView.findViewById(R.id.albumTitle);
        albumTitle.setText(a.getTitle());

        TextView albumAuthor = (TextView) mConvertView.findViewById(R.id.albumAuthor);
        albumAuthor.setText(a.getAuthor());

        ImageView iv = (ImageView) mConvertView.findViewById(R.id.albumPreview);
        Picasso.with(mContext).load(a.getImageUrl()).resize(100,100).into(iv);

        return mConvertView;

    }
}

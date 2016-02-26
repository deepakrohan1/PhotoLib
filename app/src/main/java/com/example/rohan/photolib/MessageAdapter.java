//Midterm Makeup
//Justin Campbell
package com.example.rohan.photolib;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseFile;

import java.util.List;


public class MessageAdapter extends ArrayAdapter<Message> {
    Context mContext;
    int mResource;
    List<Message> mObjects;
    String thisUser;
    Message p;
    ParseFile image;
    View mConvertView;


    public MessageAdapter(Context context, int resource, List<Message> objects, String userName) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mObjects = objects;
        this.thisUser = userName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mConvertView = convertView;

        if (mConvertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mConvertView = inflater.inflate(mResource, parent, false);
        }
        p = mObjects.get(position);

        TextView tvFrom = (TextView) mConvertView.findViewById(R.id.textViewFrom);
        tvFrom.setText("FROM: " + p.getFrom());

        TextView tvTime = (TextView) mConvertView.findViewById(R.id.textViewTime);
        tvTime.setText(p.getTime());

        TextView tvSubject = (TextView) mConvertView.findViewById(R.id.textViewSubject);
        tvSubject.setText("SUBJECT: " + p.getSubject());


        //Color background according to isRead status
        if (p.isRead()){
            mConvertView.setBackgroundColor(Color.GRAY);
        } else {
            mConvertView.setBackgroundColor(Color.WHITE);
        }

    return mConvertView;

    }
}

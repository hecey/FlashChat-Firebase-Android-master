package com.example.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;

/**
 * Created by katrina on 10/17/2017.
 */

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapshotArrayList;

    private ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            mSnapshotArrayList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity mActivity, DatabaseReference mDatabaseReference, String mDisplayName) {
        this.mActivity = mActivity;
        this.mDatabaseReference = mDatabaseReference;
        this.mDisplayName = mDisplayName;
        this.mSnapshotArrayList = new ArrayList<>();

        //Attacht listener in adapter to Database Reference
        mDatabaseReference.addChildEventListener(mChildEventListener);


    }

    public class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mSnapshotArrayList.size();
    }

    @Override
    public InstantMessage getItem(int position) {
        //Make sure adapter provide the correct Message to Listview - DataSnapshot Firebase
        DataSnapshot mDataSnapshot = mSnapshotArrayList.get(position);
        return mDataSnapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            // Create View from Layout XML File
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate means in Android parse the XML
            view = inflater.inflate(R.layout.chat_msg_row, viewGroup, false);
            // Use class for individual chat message row
            //link  fields in ViewHolder to fields in Chat's message row
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) view.findViewById(R.id.author);
            holder.body = (TextView) view.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            //Storing our ViewGHolder for a short period of time for reuse it later
            view.setTag(holder);
        }

        // Make sure we are showing the correct Author and message in the ROW if VIEW is recycled
        final InstantMessage message= getItem(position);
        final ViewHolder holder = (ViewHolder) view.getTag();

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String messge = message.getMessage();
        holder.body.setText(messge);

        return view;
    }

    public void cleanUp(){
        mDatabaseReference.removeEventListener(mChildEventListener);
    }
}

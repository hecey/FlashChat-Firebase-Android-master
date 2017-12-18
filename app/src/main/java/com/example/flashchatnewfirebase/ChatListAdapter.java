package com.example.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.InsetDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<InstantMessage> mSnapshotArrayList;

    private ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            InstantMessage holder = dataSnapshot.getValue(InstantMessage.class);
            holder.setKey(dataSnapshot.getKey());
            mSnapshotArrayList.add(holder);

           notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            InstantMessage newIM = dataSnapshot.getValue(InstantMessage.class);
            for(InstantMessage im : mSnapshotArrayList){
                if(key.equals(im.getKey())){
                    im.setValues(im);
                    notifyDataSetChanged();
                    break;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for(InstantMessage im : mSnapshotArrayList){
                    if(key.equals(im.getKey())){
                        mSnapshotArrayList.remove(im);
                        notifyDataSetChanged();
                        break;
                    }
                }
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
        this.mDatabaseReference = mDatabaseReference.child("messages");
        this.mDisplayName = mDisplayName;

        //Attacht listener in adapter to Database Reference
        this.mDatabaseReference.addChildEventListener(mChildEventListener);

        this.mSnapshotArrayList = new ArrayList<>();



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
        InstantMessage im = mSnapshotArrayList.get(position);
        return im;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

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

        //If author is equals to the mDisplayName
        boolean isMe = message.getAuthor().equals(mDisplayName);
       setChatRowAppearance(isMe, holder);

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String msg = message.getMessage();
        String key = message.getKey();
        holder.body.setText(key);
        holder.body.setText(msg);

        holder.authorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(mActivity, holder.authorName.getText(), Toast.LENGTH_SHORT).show();
                deleteItem(getItem(position));

            }
        });

        return view;
    }

    private void  setChatRowAppearance(boolean isItMe, ViewHolder holder){
        if(isItMe){
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }
        else{
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

    }

    public void deleteItem (InstantMessage im) {
        //mSnapshotArrayList.remove(position);


        mDatabaseReference.child(im.getKey()).removeValue();
        Toast.makeText(mActivity, im.getKey(), Toast.LENGTH_SHORT).show();

        // remove(int) does not exist for arrays, you would have to write that method yourself or use a List instead of an array
        //notifyDataSetChanged();
    }

    public void updateItem (InstantMessage im, String newAuthor, String newMessage) {
        //mSnapshotArrayList.remove(position);
        im.setAuthor(newAuthor);
        im.setMessage(newMessage);

        mDatabaseReference.child(im.getKey()).setValue(im);
        //Toast.makeText(mActivity, im.getKey(), Toast.LENGTH_SHORT).show();

        // remove(int) does not exist for arrays, you would have to write that method yourself or use a List instead of an array
        //notifyDataSetChanged();
    }

    public void cleanUp(){
        mDatabaseReference.removeEventListener(mChildEventListener);
    }
}

package com.example.flashchatnewfirebase;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashchatnewfirebase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private static ChatListAdapter mChatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // TODO: Set up the display name and get the Firebase reference
        setupDisplayName();
        // Reference Database Object
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);


        // TODO: Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendMessage();
                return true;
            }
        });

        // TODO: Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }


    // TODO: Retrieve the display name from the Shared Preferences
    private void setupDisplayName() {
        SharedPreferences preferences = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
        mDisplayName = preferences.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        if (mDisplayName == null) mDisplayName = "Anonymous";

    }

    private void sendMessage() {
        Log.d("Flashchat", "I sent something");
        // TODO: Grab the text the user typed in and push the message to Firebase
        String input = mInputText.getText().toString();
        if (!input.equals("")) {
            InstantMessage instantMessage = new InstantMessage(input, mDisplayName);
            mDatabaseReference.child("messages").push().setValue(instantMessage);
            mInputText.setText("");
        }

    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.


    @Override
    protected void onStart() {
        super.onStart();
        mChatListAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);
        mChatListView.setAdapter(mChatListAdapter);
        registerForContextMenu(mChatListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                //Toast.makeText(this, mChatListAdapter.getItem(info.position).getKey(), Toast.LENGTH_SHORT).show();
                mChatListAdapter.deleteItem(mChatListAdapter.getItem(info.position));
                return true;
            case R.id.update:
                //Toast.makeText(this, mChatListAdapter.getItem(info.position).getKey(), Toast.LENGTH_SHORT).show();
                showEditDialog(mChatListAdapter.getItem(info.position));
                // mChatListAdapter.deleteItem(mChatListAdapter.getItem(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);

        }
        //return super.onContextItemSelected(item);
    }

    private void showEditDialog(final InstantMessage item) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainChatActivity.this);
                builder.setTitle(getString(R.string.dialog_edit_title));
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.edit_item, null, false);
                builder.setView(view);


                final EditText authorEditText = (EditText) view.findViewById(R.id.author_text);
                final EditText messageEditText = (EditText) view.findViewById(R.id.message_text);
                if (item != null) {
                    // pre-populate
                    authorEditText.setText(item.getAuthor());
                    messageEditText.setText(item.getMessage());

                    TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // empty
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // empty
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String author = authorEditText.getText().toString();
                            String message = messageEditText.getText().toString();
                            mChatListAdapter.updateItem(item, author, message);
                        }
                    };

                    authorEditText.addTextChangedListener(textWatcher);
                    messageEditText.addTextChangedListener(textWatcher);
                }

//                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
                builder.setNegativeButton(R.string.close, null);

        builder.show();

    }

    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.
        mChatListAdapter.cleanUp();
    }


}

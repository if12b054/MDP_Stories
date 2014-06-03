package com.example.storytellers;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class RoomListActivity extends ListActivity {

	private String username;
	private EditText roomname;
	private ArrayList<String> rooms;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomlist);

		Intent intent = getIntent();
		this.username = intent.getStringExtra("username");
		this.roomname = (EditText) findViewById(R.id.editRoomname);
		this.roomname.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, 
		    		KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		            Button createBtn = (Button) findViewById(R.id.createBtn);
		            createBtn.performClick();
		            handled = true;
		        }
		        return handled;
		    }
		});
		createListView();
	}

	private void createListView() {
		this.rooms = new ArrayList<String>();

		for (int i = 0; i < 10; i++) {
			this.rooms.add("Room" + i);
		}
	    this.adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1,
	            rooms);
	        setListAdapter(adapter);
	}

	public final void createRoom(final View view) {
		int length = this.roomname.getText().length();
		if ( length <= 0 && length >= 25) {
			this.roomname.setError("Please enter a roomname! "
					+ "(max. 25 characters)");
		} else {
			Intent intent = new Intent(RoomListActivity.this,
					RoomActivity.class);
			intent.putExtra("username", this.username);
			intent.putExtra("roomname", this.roomname.getText().toString());
			RoomListActivity.this.startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
	    Intent intent = new Intent(RoomListActivity.this,
				MainActivity.class);
		RoomListActivity.this.startActivity(intent);
	    finish();
	}
}

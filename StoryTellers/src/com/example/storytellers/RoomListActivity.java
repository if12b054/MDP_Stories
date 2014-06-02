package com.example.storytellers;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;


public class RoomListActivity extends ListActivity {

	private String username;
	private ArrayList<String> rooms;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomlist);

		Intent intent = getIntent();
		this.username = intent.getStringExtra("username");
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
		EditText roomname = (EditText) findViewById(R.id.editRoomname);
		if (roomname.getText().length() <= 0) {
			roomname.setError("Please enter a roomname!");
		} else {
			Intent intent = new Intent(RoomListActivity.this,
					RoomActivity.class);
			intent.putExtra("username", this.username);
			intent.putExtra("roomname", roomname.getText().toString());
			RoomListActivity.this.startActivity(intent);
		}
	}
}

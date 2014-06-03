package com.example.storytellers;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class RoomDetailsActivity extends ListActivity {

	private String username;
	private String roomname;
	private TextView room;
	private ArrayList<String> users;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomdetails);

		Intent intent = getIntent();
		this.username = intent.getStringExtra("username");
		this.roomname = intent.getStringExtra("roomname");
		this.room = (TextView) findViewById(R.id.textRoomnameDetails);
		this.room.setText(this.roomname);
		createListView();
	}

	private void createListView() {
		this.users = new ArrayList<String>();

		for (int i = 0; i < 5; i++) {
			this.users.add("User" + i);
		}
	    this.adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1,
	            users);
	        setListAdapter(adapter);
	}

	public final void leaveRoom(final View view) {
		Intent intent = new Intent(RoomDetailsActivity.this,
				RoomListActivity.class);
		intent.putExtra("username", this.username);
		RoomDetailsActivity.this.startActivity(intent);
	}

	public final void leaveDetails(final View view) {
		finish();
	}
}

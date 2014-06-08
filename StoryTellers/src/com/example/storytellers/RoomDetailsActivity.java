package com.example.storytellers;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class RoomDetailsActivity extends ListActivity implements RoomRequestListener{

	private TextView room;
	private String[] users;
	private ArrayAdapter<String> adapter;
	private WarpClient theClient;

	@Override
	protected void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try {
			theClient = WarpClient.getInstance();
			theClient.addRoomRequestListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setContentView(R.layout.activity_roomdetails);
		this.room = (TextView) findViewById(R.id.textRoomnameDetails);
		this.room.setText(Utils.ACTUAL_ROOM_NAME);
		createListView();
	}

	private void createListView() {
		theClient.getLiveRoomInfo(Utils.ACTUAL_ROOM_ID);
	}

	public final void leaveRoomDetails(final View view) {
		Intent intent = new Intent(RoomDetailsActivity.this,
				RoomActivity.class);
		RoomDetailsActivity.this.startActivity(intent);
		this.finish();
	}

	public final void leaveDetails(final View view) {
		finish();
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		users = event.getJoinedUsers();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(users != null){
					adapter = new ArrayAdapter<String>(RoomDetailsActivity.this,
				            android.R.layout.simple_list_item_1,
				            users);
				        setListAdapter(adapter);
				}
			}
		});	
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

package com.example.storytellers;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class RoomDetailsActivity extends ListActivity implements RoomRequestListener{

	private TextView room;
	private String[] users;
	private ArrayAdapter<String> adapter;
	private WarpClient theClient;

	@Override
	protected void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_roomdetails);
		try {
			theClient = WarpClient.getInstance();
			theClient.addRoomRequestListener(this);
		} catch (Exception e) {
			Log.d("WarpClient", "Something's wrong in onCreate() "
					+ "of RoomDetailsActivity");
			e.printStackTrace();
			Intent intent = new Intent(RoomDetailsActivity.this,
					MainActivity.class);
			RoomDetailsActivity.this.startActivity(intent);
			finish();
		}
		this.room = (TextView) findViewById(R.id.textRoomnameDetails);
		this.room.setText(Utils.ACTUAL_ROOM_NAME);
		createListView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		theClient.addRoomRequestListener(this);		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		theClient.removeRoomRequestListener(this);
	}

	private void createListView() {
		theClient.getLiveRoomInfo(Utils.ACTUAL_ROOM_ID);
	}

	public final void leaveRoomDetails(final View view) {
		theClient.unsubscribeRoom(Utils.ACTUAL_ROOM_ID);
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
					adapter = null;
					adapter = new ArrayAdapter<String>(RoomDetailsActivity.this,
				            R.layout.list_item, users);
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
	public void onLeaveRoomDone(RoomEvent event) {
		if( event.getResult() == WarpResponseResultCode.SUCCESS) {
			theClient.unsubscribeRoom(event.getData().getId());
			Log.d("onLeaveRoomDone", "Room left");
			Intent intent = new Intent(RoomDetailsActivity.this,
					RoomListActivity.class);
			RoomDetailsActivity.this.startActivity(intent);
			finish();
		}else{
			showToastOnUIThread("onLeaveRoomDone with ErrorCode: "
					+ event.getResult());
		}
	}
	
	private void showToastOnUIThread(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(RoomDetailsActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
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
	public void onUnSubscribeRoomDone(RoomEvent event) {
		if(event.getResult() == WarpResponseResultCode.SUCCESS){
			Log.d("onUnSubscribeRoom", "Unsubscribed Room \"" + event.getData().getName()
					+ "\" with id = " + event.getData().getId());
			theClient.leaveRoom(Utils.ACTUAL_ROOM_ID);
		} else {
			showToastOnUIThread("onUnSubscribeRoomDone with ErrorCode: "
					+ event.getResult());
		}
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

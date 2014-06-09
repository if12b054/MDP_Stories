package com.example.storytellers;

import java.util.ArrayList;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class RoomListActivity extends ListActivity implements ZoneRequestListener,
RoomRequestListener {

	private EditText roomname;
	private ArrayList<String> rooms;
	private ArrayAdapter<String> adapter;
	private int maxUsers = 2;
	private int turnTime = 10;
	private WarpClient theClient;
	private String[] roomIds;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_roomlist);
		try {
			theClient = WarpClient.getInstance();
		} catch (Exception e) {
			Log.d("WarpClient", "Something's wrong in onCreate() "
					+ "of RoomListActivity");
			e.printStackTrace();
			Intent intent = new Intent(RoomListActivity.this,
					MainActivity.class);
			RoomListActivity.this.startActivity(intent);
			finish();
		}
		// zuerst checken ob man noch eingeloggt ist
		// wenn nicht zurück zum einloggen!
		this.adapter = null;
		theClient.addZoneRequestListener(this);
		theClient.addRoomRequestListener(this);
		this.rooms = new ArrayList<String>();
		this.roomname = (EditText) findViewById(R.id.editRoomname);
		this.roomname.setOnEditorActionListener(
				new OnEditorActionListener() {
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		theClient.addRoomRequestListener(this);
		createListView();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		theClient.removeRoomRequestListener(this);
	}

	private void createListView() {
		theClient.getAllRooms();
		System.out.println("create list called");
	}

	public final void createRoom(final View view) {
		int length = this.roomname.getText().length();
		if (length <= 0 || length >= 25){
			this.roomname.setError("Please enter a roomname! "
					+ "max. 25 characters)");
		} else {
			Utils.ACTUAL_ROOM_NAME = this.roomname.getText().toString();
			theClient.createTurnRoom(Utils.ACTUAL_ROOM_NAME,
					Utils.USER_NAME, maxUsers, null, turnTime);
		}
	}

	@Override
	public void onBackPressed() {
	    theClient.disconnect();
		Intent intent = new Intent(RoomListActivity.this,
				MainActivity.class);
		RoomListActivity.this.startActivity(intent);
	    finish();
	}

	private void showToastOnUIThread(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(RoomListActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onCreateRoomDone(RoomEvent event) {
		if (event.getResult() == WarpResponseResultCode.SUCCESS) {
			Utils.ACTUAL_ROOM_ID = event.getData().getId();
			theClient.joinRoom(Utils.ACTUAL_ROOM_ID);
		} else {
			showToastOnUIThread("onCreateRoomDone with ErrorCode: "
					+ event.getResult());
		}
	}

	@Override
	public void onDeleteRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
	}

	// get all rooms and put it into the list
	@Override
	public void onGetAllRoomsDone(AllRoomsEvent event) {
		roomIds = event.getRoomIds();
		Log.d("Number of Rooms", Integer.toString(
				event.getRoomIds().length));
		if (this.roomIds.length != this.rooms.size()) {
			for (int i = 0; i < roomIds.length; i++) {
				theClient.getLiveRoomInfo(roomIds[i]);
			}
		}
	}
	
	@Override  
	protected void onListItemClick(ListView l, View v, int pos, long id) {  
		this.adapter.notifyDataSetChanged();
		Utils.ACTUAL_ROOM_ID = roomIds[pos];
		Utils.ACTUAL_ROOM_NAME = rooms.get(pos);
		theClient.joinRoom(Utils.ACTUAL_ROOM_ID);
	}
	
	@Override
	public void onJoinRoomDone(RoomEvent event) {
		if (event.getResult() == WarpResponseResultCode.SUCCESS) {
			Utils.ACTUAL_ROOM_ID = event.getData().getId();
			theClient.subscribeRoom(event.getData().getId());
		} else{
			showToastOnUIThread("onJoinRoomDone with ErrorCode: "
					+ event.getResult());
		}
	}
	
	@Override
	public void onSubscribeRoomDone(RoomEvent event) {
		if (event.getResult() == WarpResponseResultCode.SUCCESS) {
			Log.d("onSubscribeRoom", "joining Room "
					+ event.getData().getName());
			Intent intent = new Intent(RoomListActivity.this,
					RoomActivity.class);
			RoomListActivity.this.startActivity(intent);
			finish();
		} else {
			showToastOnUIThread("onSubscribeRoomDone Failed with ErrorCode: "
					+ event.getResult());
		}
	}

	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGetMatchedRoomsDone(MatchedRoomsEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGetOnlineUsersDone(AllUsersEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		this.rooms.add(event.getData().getName());
		Log.d("Roomname : ", event.getData().getName());
		if (this.rooms.size() == this.roomIds.length){ 
			runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	Log.d("Listadapter", "neuer wird erstellt");
	            	Log.d("rooms",Integer.toString(rooms.size()));
	            	adapter = null;
	            	adapter = new ArrayAdapter<String>(RoomListActivity.this,
				            R.layout.list_item, rooms);
					setListAdapter(adapter);
					adapter.notifyDataSetChanged();
	            }
			});
		}
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

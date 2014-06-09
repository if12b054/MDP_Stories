package com.example.storytellers;

import java.util.HashMap;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.TurnBasedRoomListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class RoomActivity extends Activity implements RoomRequestListener,
TurnBasedRoomListener, NotifyListener{
	
	private Button sendBtn;
	private TextView story;
	private TextView room;
	private EditText sentence;
	private WarpClient theClient;
	private int actualRoomUsers;
	private boolean gameStarted = false;
	private int moveCounter = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_room);
		try {
			theClient = WarpClient.getInstance();
			theClient.addRoomRequestListener(this);
			theClient.addTurnBasedRoomListener(this);
			theClient.addNotificationListener(this);
		} catch (Exception e) {
			Log.d("WarpClient", "Something's wrong in onCreate() "
					+ "of RoomActivity");
			e.printStackTrace();
			Intent intent = new Intent(RoomActivity.this,
					MainActivity.class);
			RoomActivity.this.startActivity(intent);
			finish();
		}
		
		this.sendBtn = (Button) findViewById(R.id.sendBtn);
		this.sendBtn.setEnabled(false);

		this.story = (TextView) findViewById(R.id.textStory);
		this.story.setMovementMethod(new ScrollingMovementMethod());

		this.room = (TextView) findViewById(R.id.textRoomname);
		this.room.setText(Utils.ACTUAL_ROOM_NAME);

		this.sentence = (EditText) findViewById(R.id.editStory);
		this.sentence.setOnEditorActionListener(
				new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, 
		    		KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		            Button sendBtn = (Button) findViewById(R.id.sendBtn);
		            sendBtn.performClick();
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
		theClient.addTurnBasedRoomListener(this);
		theClient.addNotificationListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		theClient.removeRoomRequestListener(this);
		theClient.removeTurnBasedRoomListener(this);
		theClient.removeNotificationListener(this);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(theClient!=null){
			theClient.removeRoomRequestListener(this);
			theClient.removeTurnBasedRoomListener(this);
			theClient.removeNotificationListener(this);
		}
	}

	public final void sendSentence(final View view) {
		int length = this.sentence.getText().length();
		if (length <= 0) {
			this.sentence.setError("Please enter a sentence!");
		} else {
			InputMethodManager imm = (InputMethodManager) getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(this.sentence.getWindowToken(), 0);
			theClient.sendMove(this.sentence.getText().toString());
			Log.d("sendSentence", "-----");
		}
	}

	public final void leaveRoom(final View view) {
		theClient.unsubscribeRoom(Utils.ACTUAL_ROOM_ID);
	}

	public final void showDetails(final View view) {
		Intent intent = new Intent(RoomActivity.this,
				RoomDetailsActivity.class);
		RoomActivity.this.startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		// empty method to disable back button
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		actualRoomUsers = event.getJoinedUsers().length;
		Utils.ACTUAL_ROOM_OWNER = event.getData().getRoomOwner();
		Log.d("onGetLiveRoomInfoDone", "-----");
		if(actualRoomUsers == event.getData().getMaxUsers() && Utils.ACTUAL_ROOM_OWNER.equals(Utils.USER_NAME)&& gameStarted == false){
			theClient.startGame();
			gameStarted = true;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					sendBtn.setEnabled(true);
				}
			});
			Log.d("Game", "started");
		}		
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLeaveRoomDone(RoomEvent event) {
		if( event.getResult() == WarpResponseResultCode.SUCCESS) {
			Log.d("onLeaveRoomDone", "Room left");
			Intent intent = new Intent(RoomActivity.this,
					RoomListActivity.class);
			RoomActivity.this.startActivity(intent);
			finish();
		}else{
			showToastOnUIThread("onLeaveRoomDone with ErrorCode: "
					+ event.getResult());
		}
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
	
	private void showToastOnUIThread(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(RoomActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onGetMoveHistoryDone(byte arg0, MoveEvent[] arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSendMoveDone(byte arg0) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sentence.setText("");
				sendBtn.setEnabled(false);
				Log.d("onSendMoveDone", "sendButton disabled");
			}
		});
	}

	@Override
	public void onStartGameDone(byte arg0) {
		gameStarted = true;
	}

	@Override
	public void onStopGameDone(byte arg0) {
		gameStarted = false;	
	}

	@Override
	public void onChatReceived(ChatEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGameStarted(String arg0, String arg1, String arg2) {
		showToastOnUIThread("Game start");
		gameStarted = true;
		showToastOnUIThread("Your turn");
	}

	@Override
	public void onGameStopped(String arg0, String arg1) {
		showToastOnUIThread("Game over");
		theClient.leaveRoom(Utils.ACTUAL_ROOM_ID);
	}

	@Override
	public void onMoveCompleted(final MoveEvent event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				story.append(event.getMoveData());
			}
		});
		Log.d("onMoveCompleted", "");
		if(event.getNextTurn().equals(Utils.USER_NAME)){
			if(Utils.ACTUAL_ROOM_OWNER.equals(Utils.USER_NAME)){
				moveCounter++;
				if(moveCounter == 5){
					theClient.stopGame();
					gameStarted = false;
					Log.d("Game", "stopped");
				}
			}
			if(gameStarted == true){
			Log.d("onMoveCompleted", "Your next turn!");
			showToastOnUIThread("Your turn");
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					sendBtn.setEnabled(true);
				}
			});
		}
	}

	@Override
	public void onPrivateChatReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomCreated(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomDestroyed(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePeersReceived(UpdateEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			HashMap<String, Object> arg2, HashMap<String, String> arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserJoinedRoom(RoomData roomData, String roomId) {
		theClient.getLiveRoomInfo(roomData.getId());
		Log.d("onUserJoinedRoom", "");
	}

	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLeftRoom(RoomData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserPaused(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserResumed(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
}

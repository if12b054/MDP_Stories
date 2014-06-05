package com.example.storytellers;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class RoomActivity extends Activity implements RoomRequestListener{
	
	private TextView story;
	private TextView room;
	private EditText sentence;
	private WarpClient theClient;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);
		try {
			theClient = WarpClient.getInstance();
			theClient.addRoomRequestListener(this);
		} catch (Exception e) {
			Log.d("WarpClient", "Something's wrong in onCreate() of RoomActivity");
			e.printStackTrace();
		}

		this.story = (TextView) findViewById(R.id.textStory);
		this.story.setMovementMethod(new ScrollingMovementMethod());

		this.room = (TextView) findViewById(R.id.textRoomname);
		this.room.setText(Utils.ACTUAL_ROOM_NAME);
		
		this.sentence = (EditText) findViewById(R.id.editStory);
		this.sentence.setOnEditorActionListener(new OnEditorActionListener() {
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
	protected void onPause() {
		super.onPause();
		theClient.removeRoomRequestListener(this);
	}

	public final void sendSentence(final View view) {
		int length = this.sentence.getText().length();
		if ( length <= 0 ) {
			this.sentence.setError("Please enter a sentence!");
		} else {
			this.story.append(this.sentence.getText().toString());
			this.sentence.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(this.sentence.getWindowToken(), 0);
		}
	}

	public final void leaveRoom(final View view) {
		theClient.leaveRoom(Utils.ACTUAL_ROOM_ID);
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
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent event) {
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			theClient.unsubscribeRoom(event.getData().getId());
		}else{
			showToastOnUIThread("onLeaveRoomDone with ErrorCode: "+event.getResult());
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
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			Log.d("onUnSubscribeRoom", "Room \""+event.getData().getName()+"\" with id = "+event.getData().getId()+" is left!");
			this.finish();
		}else{
			showToastOnUIThread("onUnSubscribeRoomDone with ErrorCode: "+event.getResult());
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
	private void showToastOnUIThread(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(RoomActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}

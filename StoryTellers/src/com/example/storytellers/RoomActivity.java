package com.example.storytellers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RoomActivity extends Activity {

	private String username;
	private String roomname;
	private TextView story;
	private TextView room;
	private EditText sentence;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);

		this.story = (TextView) findViewById(R.id.textStory);
		this.story.setMovementMethod(new ScrollingMovementMethod());

		Intent intent = getIntent();
		this.username = intent.getStringExtra("username");
		this.roomname = intent.getStringExtra("roomname");
		this.room = (TextView) findViewById(R.id.textRoomname);
		this.room.setText(this.roomname);
		
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
		Intent intent = new Intent(RoomActivity.this,
				RoomListActivity.class);
		intent.putExtra("username", this.username);
		RoomActivity.this.startActivity(intent);
	}

	public final void showDetails(final View view) {
		Intent intent = new Intent(RoomActivity.this,
				RoomDetailsActivity.class);
		intent.putExtra("username", this.username);
		intent.putExtra("roomname", this.roomname);
		RoomActivity.this.startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		// empty method to disable back button
	}
}

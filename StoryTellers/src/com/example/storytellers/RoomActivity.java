package com.example.storytellers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class RoomActivity extends Activity {

	private String username;
	private String roomname;
	private TextView story;
	private TextView room;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);

		this.story = (TextView) findViewById(R.id.textStory);
		this.story.setMovementMethod(new ScrollingMovementMethod());

		Intent intent = getIntent();
		this.username = intent.getStringExtra("username");
		this.roomname = intent.getStringExtra("roomname");
		this.room = (TextView) findViewById(R.id.textRoomname);
		this.room.setText(this.roomname);
	}

	public final void sendSentence(final View view) {
		EditText sentence = (EditText) findViewById(R.id.editStory);
		if (sentence.getText().length() <= 0) {
			sentence.setError("Please enter a sentence!");
		} else {
			story.append(sentence.getText().toString());
			sentence.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(sentence.getWindowToken(), 0);
		}
	}

	public final void leaveRoom(final View view) {
		Intent intent = new Intent(RoomActivity.this,
				RoomListActivity.class);
		intent.putExtra("username", this.username);
		RoomActivity.this.startActivity(intent);
	}
}

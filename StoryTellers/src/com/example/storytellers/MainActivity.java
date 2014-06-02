package com.example.storytellers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public final void login(final View view) {
		EditText username = (EditText) findViewById(R.id.editUsername);
		if (username.getText().length() <= 0) {
			username.setError("Please enter a username!");
		} else {
			Intent intent = new Intent(MainActivity.this, RoomActivity.class);
			intent.putExtra("username", username.getText().toString());
			MainActivity.this.startActivity(intent);
		}
	}
}

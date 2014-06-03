package com.example.storytellers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {

	private EditText username;
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.username = (EditText) findViewById(R.id.editUsername);
		this.username.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, 
		    		KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		            Button loginBtn = (Button) findViewById(R.id.loginBtn);
		            loginBtn.performClick();
		            handled = true;
		        }
		        return handled;
		    }
		});
	}

	public final void login(final View view) {
		int length = this.username.getText().length();
		if ( length <= 0 && length >= 25) {
			this.username.setError("Please enter a username! "
					+ "(max. 25 characters)");
		} else {
			Intent intent = new Intent(MainActivity.this,
					RoomListActivity.class);
			intent.putExtra("username",
					this.username.getText().toString());
			MainActivity.this.startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		// empty method to disable back button
	}
}

package com.example.storytellers;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity implements ConnectionRequestListener {

	private EditText username;
	private WarpClient theClient;
	private ProgressDialog progressDialog;
	private Handler handler = new Handler();
	
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
		init();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		theClient.addConnectionRequestListener(this); 
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		theClient.removeConnectionRequestListener(this);  
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		if(theClient!=null){
			theClient.disconnect();
		}
	}

	public final void login(final View view) {
		int length = this.username.getText().length();
		Log.d("login", "textLength = "+length);
		
		if (length <= 0 || length >= 25){
			this.username.setError("Please enter a username!");
					if(length >= 25){
					this.username.setError( this.username.getError() + "( max. 25 characters)");
					}
		} else {
			Utils.USER_NAME  = this.username.getText().toString();
			progressDialog = ProgressDialog.show(this, "", "Please wait...");
			progressDialog.setCancelable(true);
			theClient.connectWithUserName(Utils.USER_NAME);
		}
	}
	
	private void init(){
		WarpClient.initialize(Constants.apiKey, Constants.secretKey);
		WarpClient.setRecoveryAllowance(120);
        try {
            theClient = WarpClient.getInstance();
        } catch (Exception ex) {
            Toast.makeText(this, "Exception in Initilization", Toast.LENGTH_LONG).show();
        }
        
	}

	@Override
	public void onConnectDone(ConnectEvent event) {
		if(progressDialog!=null){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.dismiss();
					
				}
			});
		}
		Log.d("serverConnection", "eventResult: " + event.getResult());
		if(event.getResult() == WarpResponseResultCode.SUCCESS){
        	showToastOnUIThread("Connection success");
        	Intent intent = new Intent(MainActivity.this,
					RoomListActivity.class);
			MainActivity.this.startActivity(intent);
        }
        else if(event.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
        	showToastOnUIThread("Connection recovered");
        }
        else if(event.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE){
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog = ProgressDialog.show(MainActivity.this, "", "Recoverable connection error. Recovering session after 5 seconds");
				}
			});
        	handler.postDelayed(new Runnable() {
                @Override
                public void run() {                                          
                    progressDialog.setMessage("Recovering...");
                    theClient.RecoverConnection();
                }
            }, 5000);
        }
        else{
        	showToastOnUIThread("Non-recoverable connection error.");
        }
	}

	@Override
	public void onDisconnectDone(ConnectEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitUDPDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	private void showToastOnUIThread(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}

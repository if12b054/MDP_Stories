package com.example.storytellers;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class Utils {

	public static String USER_NAME = "";
	public static String ACTUAL_ROOM_NAME = "";
	public static String ACTUAL_ROOM_ID = "";
	public static String ACTUAL_ROOM_OWNER = "";

	public static String[] removeLocalUserNameFromArray(
			final String[] user) {
		ArrayList<String> userList = new ArrayList<String>();
		for (int i = 0; i < user.length; i++) {
			Log.d("i=" + i, user[i]);
			if (!USER_NAME.equals(user[i])) {
				userList.add(user[i]);
			}
		}
		String returnArray[] = new String[userList.size()];
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = userList.get(i).toString();
		}
		return returnArray;
	}

	public static void showToast(final Context ctx, final String message) {
		Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
	}

}

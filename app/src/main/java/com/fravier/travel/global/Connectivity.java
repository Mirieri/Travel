package com.fravier.travel.global;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Connectivity {
	public static Context ctx;
	static ConnectivityManager connectivityManager;
	private static Boolean gps_flag = false;

	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}

	public Connectivity(Context c) {
		ctx = c;
	}

	public static boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();

		if (ni == null) {
			// begin doing checks to diagnose why theres no data

			return false;

		} else
			return true;
	}

	public static void debugConnectivity() {

		if (!isNetworkConnected()) {// diagnose if it is a data network issue

			// scenario A. If Mobile Data has not been switched on
			if (getMobileDataEnabled() == false) {
				Toast.makeText(ctx, "Reason: Mobile Data is Disabled",
                        Toast.LENGTH_LONG).show();
				setMobileDataEnabled(true);
				if (isNetworkConnected()) {
					Toast.makeText(ctx, "Mobile Data Active", Toast.LENGTH_LONG)
							.show();
				}

			} else if (isNetworkConnected() == false) {
				if (!getMobileDataEnabled()) {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.setClassName("com.android.phone",
							"com.android.phone.Settings");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctx.startActivity(intent);
				}
				if (isAirplaneMode()) {
					Toast.makeText(ctx, "Reason: Airplane Mode is ON",
                            Toast.LENGTH_LONG).show();
					disableAirplaneMode();
				}

			}
			if (getMobileDataEnabled() && !isAirplaneMode()
					&& getConnectivityStatus(ctx) == TYPE_NOT_CONNECTED) {
				Toast.makeText(ctx, "no bundle", Toast.LENGTH_LONG).show();
			}

		}
	}

	public static boolean getMobileDataEnabled() {// troubleshooting to check
													// if data is enabled
		connectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class cmClass = Class.forName(connectivityManager.getClass()
                    .getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true); // Make the method callable
			// get the setting for "mobile data"
			return (Boolean) method.invoke(connectivityManager);
		} catch (Exception e) {
			// Some problem accessible private API
			// TODO do whatever error handling you want here
		}
		return false;
	}

	private static void setMobileDataEnabled(boolean on) {// toggling on the
															// mobile data
		Toast.makeText(ctx, "Switching on Mobile Data...", Toast.LENGTH_LONG)
				.show();
		connectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Method dataMtd;
		try {
			dataMtd = ConnectivityManager.class.getDeclaredMethod(
					"setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(connectivityManager, true);

			if (isNetworkConnected()) {
				Toast.makeText(
                        ctx,
                        "The network is working fine. You may try login again.",
                        Toast.LENGTH_LONG).show();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean isAirplaneMode() {
		int settingValue;
		try {
			settingValue = Settings.System.getInt(ctx.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON);
			if (settingValue == 1) {
				System.out.println("true");
				return true;
			} else if (settingValue != 1) {
				System.out.println("false");
				return false;
			}
			System.out.println("nullest");
			return false;
		} catch (SettingNotFoundException e) {
			return false;
		}
	}

	public static void disableAirplaneMode() {
		Toast.makeText(ctx, "Switching Off Airplane Mode...", Toast.LENGTH_LONG)
				.show();
		Settings.System.putInt(ctx.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0);

		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", false);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.sendBroadcast(intent);
		if (isNetworkConnected()) {
			Toast.makeText(ctx,
                    "The network is working fine. You may try again.",
                    Toast.LENGTH_LONG).show();
		}

	}

}

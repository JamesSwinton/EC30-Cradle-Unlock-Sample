package com.zebra.silva.ec30cradletest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {
  int color = 16;//1-Green,16-Red,17-Blue
  boolean isSolid = false;
  int REQUEST_CODE = 2;

  private static final String TAG = "UnlockActivity";
  private static final String UNLOCK_ACTION = "com.zebra.UNLOCK";

  public static final String COMMAND = "COMMAND";
  public static final String BLINK = "UNLOCK_LED_BLINK";
  public static final String SOLID = "UNLOCK_LED_SOLID";

  private IntentFilter mUnlockIntentFilter;
  private UnlockIntentReceiver mUnlockIntentReceiver;

  public Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Register Intents Receiver
    registerReceiver();

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //CradleLEDBlink();
        CradleUnlockWithLED();
        //CradleUnlockWithLEDBlink();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Init Context
    context = this;

    // Check for StartActivity Intents
    getStartActivityIntents();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver();
  }

  private void getStartActivityIntents() {
    Intent startActivityIntent = getIntent();
    if (startActivityIntent != null) {
      if (startActivityIntent.getAction() == null) {
        CradleUnlockWithLED(true);
      } else {
        Log.i(TAG, "Started Via UI");
      }
//      if (startActivityIntent.getExtras() != null) {
//        Bundle startActivityExtras = startActivityIntent.getExtras();
//        String command = startActivityExtras.getString(COMMAND);
//        if (command != null) {
//          if (command.equals(BLINK)) {
//            // CradleUnlockWithLEDBlink();
//          } else if (command.equals(SOLID)) {
//            CradleUnlockWithLED(true);
//          } else {
//            Log.e(TAG, "Invalid Command");
//          }
//        } else {
//          Log.e(TAG, "No Command Intent");
//        }
//      }
    } else {
      Log.e(TAG, "No Start Activity Intent Supplied");
    }
  }

  private void registerReceiver() {
    if (mUnlockIntentReceiver == null) {
      mUnlockIntentReceiver = new UnlockIntentReceiver();
    }
    if (mUnlockIntentFilter == null) {
      mUnlockIntentFilter = new IntentFilter();
    }
    mUnlockIntentFilter.addAction(UNLOCK_ACTION);
    registerReceiver(mUnlockIntentReceiver, mUnlockIntentFilter);
  }

  private void unregisterReceiver() {
    unregisterReceiver(mUnlockIntentReceiver);
  }


  private void CradleUnlock() {
    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle unlockBundle = new Bundle();
    unlockBundle.putInt("TIMEOUT", 5);
    unlockBundle.putBoolean("LED", false);
    intent.putExtra("UNLOCK", unlockBundle);

    Intent responseIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_UNLOCK");

    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);
    sendBroadcast(intent);
  }

  private void CradleUnlockWithLED() {
    Log.i(TAG, "Start Unlock with LED");

    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle unlockBundle = new Bundle();
    unlockBundle.putInt("TIMEOUT", 10);
    unlockBundle.putBoolean("LED", true);
    intent.putExtra("UNLOCK", unlockBundle);

    Intent responseIntent = new Intent(getApplicationContext(),  MyBroadcastReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_UNLOCK_WITH_LED");

    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);
    sendBroadcast(intent);
  }

  private void CradleUnlockWithLED(boolean exitOnSend) {
    Log.i(TAG, "Start Unlock with LED & Exit");

    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle unlockBundle = new Bundle();
    unlockBundle.putInt("TIMEOUT", 10);
    unlockBundle.putBoolean("LED", true);
    intent.putExtra("UNLOCK", unlockBundle);

    Intent responseIntent = new Intent(getApplicationContext(),  MyBroadcastReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_UNLOCK_WITH_LED");

    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);
    sendBroadcast(intent);

    if (exitOnSend) {
      ((MainActivity) context).finish();
    }
  }

  private void CradleLEDBlink() {
    Log.i(TAG, "Start Unlock with Blink");

    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle blinkBundle = new Bundle();
    blinkBundle.putInt("TIMEOUT", 10);
    blinkBundle.putInt("COLOR", color); //1-Green,16-Red,17-Blue
    blinkBundle.putBoolean("SOLID", isSolid); // true OR false
    intent.putExtra("BLINK", blinkBundle);

    Intent responseIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_BLINK_LED");

    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);
    sendBroadcast(intent);
  }

  public class UnlockIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "Unlock Intent Received");

      // Validate Packages Sent
      if (intent.getExtras() != null) {
        // Only one Package sent -> Start Update
        if (intent.getExtras() != null) {
          String command = intent.getStringExtra(COMMAND);
          if (command != null) {
            if (command.equals(BLINK)) {
              // CradleUnlockWithLEDBlink();
            } else if (command.equals(SOLID)) {
              CradleUnlockWithLED(true);
            } else {
              Log.e(TAG, "Invalid Command");
            }
          } else {
            Log.e(TAG, "No Command Intent");
          }
        } else {
          Log.e(TAG, "No Extra Value");
        }
      }
    }
  }

  /*The broadcast receiver below will receive the responseIntent defined in the above functions once any of the above API calls have been processed
  register this broadcast receiver by putting the below in the <application> section of the manifest.
          <receiver
          android:name=".MainActivity$MyBroadcastReceiver">
      </receiver>

  */
  public static class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

      String status = "";
      String command = intent.getStringExtra("COMMAND");
      String resultCode = intent.getStringExtra("RESULT_CODE");
      String resultMessage = intent.getStringExtra("RESULT_MESSAGE");

      if (command != null) {
        status += "\n* " + command;
      }
      if (resultCode != null) {
        status += "\n* ResultCode= " + resultCode;
      }
      if (resultMessage != null) {
        status += "\n* Message= " + resultMessage + " \n";
      }
      Log.d(MyBroadcastReceiver.class.getSimpleName(), status);
    }

  }
}










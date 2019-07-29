package com.zebra.silva.ec30cradletest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

  // Debugging
  private static final String TAG = "UnlockActivity";

  // Constants
  private static final int CRADLE_RESULTS = 100;
  private static final String COMMAND = "COMMAND";
  private static final String BLINK = "UNLOCK_LED_BLINK";
  private static final String SOLID = "UNLOCK_LED_SOLID";
  private static final String UNLOCK_ACTION = "com.zebra.UNLOCK";

  // Unlock Variables
  private static final int color = 16; //1 = Green, 16 = Red, 17 = Blue
  private static final boolean isSolid = false;

  // Broadcast Variables
  private IntentFilter mUnlockIntentFilter;
  private UnlockIntentReceiver mUnlockIntentReceiver;
  private CradleCommunicationResponseReceiver mCradleCommunicationResponseReceiver;

  public Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Register Intents Receiver
    registerReceiver();

    // Init Unlock Button
    findViewById(R.id.button).setOnClickListener(view -> cradleUnlockWithLED());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver();
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Init Context
    context = this;

    // Check for StartActivity Intents
    getStartActivityIntents();
  }

  private void getStartActivityIntents() {
    // Get Intents from Activity Start
    Intent mainActivityIntent = getIntent();

    // Validate Intent
    if (mainActivityIntent == null) {
      Log.i(TAG, "Main Activity does not contain Intent");
      return;
    }

    // Validate Action
    if (mainActivityIntent.getAction() == null) {
      Log.i(TAG, "Main Activity Intent does not have an Action");
      return;
    }

    // Start Unlock with Exit
    if (mainActivityIntent.getAction().equals(UNLOCK_ACTION)) {
      cradleUnlockWithLEDAndExit();
    }
  }

  /**
   * Receiver Register / Unregister Methods
   */

  private void registerReceiver() {
    // Build Unlock Receiver
    if (mUnlockIntentReceiver == null) {
      mUnlockIntentReceiver = new UnlockIntentReceiver();
    }

    // Build Unlock Filter
    if (mUnlockIntentFilter == null) {
      mUnlockIntentFilter = new IntentFilter();
    } mUnlockIntentFilter.addAction(UNLOCK_ACTION);

    // Build Cradle Response Receiver
    if (mCradleCommunicationResponseReceiver == null) {
      mCradleCommunicationResponseReceiver = new CradleCommunicationResponseReceiver();
    }

    // Register Receivers
    registerReceiver(mUnlockIntentReceiver, mUnlockIntentFilter);
    registerReceiver(mCradleCommunicationResponseReceiver, new IntentFilter());
  }

  private void unregisterReceiver() {
    // Unregister Receivers
    unregisterReceiver(mUnlockIntentReceiver);
    unregisterReceiver(mCradleCommunicationResponseReceiver);
  }

  /**
   * Unlocking Methods
   */

  private void cradleUnlockWithoutLED() {
    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle unlockBundle = new Bundle();
    unlockBundle.putInt("TIMEOUT", 5);
    unlockBundle.putBoolean("LED", false);
    intent.putExtra("UNLOCK", unlockBundle);

    Intent responseIntent = new Intent(getApplicationContext(), CradleCommunicationResponseReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_UNLOCK");

    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), CRADLE_RESULTS, responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);
    sendBroadcast(intent);
  }

  private void cradleUnlockWithLED() {
    // Log Unlock Attempt
    Log.i(TAG, "Start Unlock with LED");
    Toast.makeText(context, "Starting Unlock with LED", Toast.LENGTH_SHORT).show();

    // Create Unlock Intent
    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle unlockBundle = new Bundle();
    unlockBundle.putInt("TIMEOUT", 10);
    unlockBundle.putBoolean("LED", true);
    intent.putExtra("UNLOCK", unlockBundle);

    // Create Callback Intent
    Intent responseIntent = new Intent(getApplicationContext(),  CradleCommunicationResponseReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_UNLOCK_WITH_LED");

    // Bundle Intents
    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), CRADLE_RESULTS,
        responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);

    // Send Broadcast
    sendBroadcast(intent);
  }

  private void cradleUnlockWithLEDAndExit() {
    // Log Unlock Attempt
    Log.i(TAG, "Start Unlock with LED & Exit");
    Toast.makeText(context, "Starting Unlock with LED & Exit", Toast.LENGTH_SHORT).show();

    // Create Unlock Intent
    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle unlockBundle = new Bundle();
    unlockBundle.putInt("TIMEOUT", 10);
    unlockBundle.putBoolean("LED", true);
    intent.putExtra("UNLOCK", unlockBundle);

    // Create Callback Intent
    Intent responseIntent = new Intent(getApplicationContext(),  CradleCommunicationResponseReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_UNLOCK_WITH_LED");

    // Bundle Intents
    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), CRADLE_RESULTS,
        responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);

    // Send Broadcast
    sendBroadcast(intent);

    // Exit Application
    ((MainActivity) context).finish();
  }

  /**
   * Misc. Cradle Methods
   */

  private void cradleLEDBlink() {
    // Log Blink
    Log.i(TAG, "Start Unlock with Blink");

    Intent intent = new Intent();
    intent.setAction("com.symbol.cradle.api.ACTION_DO");
    Bundle blinkBundle = new Bundle();
    blinkBundle.putInt("TIMEOUT", 10);
    blinkBundle.putInt("COLOR", color); //1-Green,16-Red,17-Blue
    blinkBundle.putBoolean("SOLID", isSolid); // true OR false
    intent.putExtra("BLINK", blinkBundle);

    Intent responseIntent = new Intent(getApplicationContext(), CradleCommunicationResponseReceiver.class);
    responseIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
    responseIntent.putExtra("COMMAND", "CRADLE_BLINK_LED");

    PendingIntent piResponse = PendingIntent.getBroadcast(getApplicationContext(), CRADLE_RESULTS, responseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    intent.putExtra("CALLBACK_RESPONSE", piResponse);
    sendBroadcast(intent);
  }

  /**
   * Broadcast Receiver to Listen for Unlock Command
   */

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
              cradleLEDBlink();
            } else if (command.equals(SOLID)) {
              cradleUnlockWithLEDAndExit();
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

  /**
   * Broadcast Receiver to Listen for Cradle Response
   */

  public static class CradleCommunicationResponseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

      // Get Response Details
      String status = "";
      String command = intent.getStringExtra("COMMAND");
      String resultCode = intent.getStringExtra("RESULT_CODE");
      String resultMessage = intent.getStringExtra("RESULT_MESSAGE");

      // Build Response String
      if (command != null) {
        status += "\n* " + command;
      }
      if (resultCode != null) {
        status += "\n* ResultCode= " + resultCode;
      }
      if (resultMessage != null) {
        status += "\n* Message= " + resultMessage + " \n";
      }

      // Log Response
      Log.d(TAG, status);
    }
  }
}










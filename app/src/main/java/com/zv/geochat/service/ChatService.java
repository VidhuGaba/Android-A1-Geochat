package com.zv.geochat.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;

import com.zv.geochat.notification.NotificationDecorator;

public class ChatService extends Service {
    private static final String TAG = "ChatService";

    public static final String MSG_CMD = "msg_cmd";
    public static final int CMD_JOIN_CHAT = 10;
    public static final int CMD_LEAVE_CHAT = 20;
    public static final int CMD_SEND_MESSAGE = 30;
    public static final int CMD_RECEIVE_MESSAGE = 40;
    public static final String KEY_MESSAGE_TEXT = "message_text";
    public static final String KEY_USER_NAME = "user_name";

    public static final int CMD_CONNECT_ERROR_25 = 25;

    public static final int CMD_SEND_RANDOM_ID = 94;

    private NotificationManager notificationMgr;
    private PowerManager.WakeLock wakeLock;
    private NotificationDecorator notificationDecorator;

    public ChatService() {
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
        super.onCreate();
        notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationDecorator = new NotificationDecorator(this, notificationMgr);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            Bundle data = intent.getExtras();
            handleData(data);
            if (!wakeLock.isHeld()) {
                Log.v(TAG, "acquiring wake lock");
                wakeLock.acquire();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy()");
        notificationMgr.cancelAll();
        Log.v(TAG, "releasing wake lock");
        wakeLock.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int getResponseCode() {
        return 0;
    }

    public class ChatServiceBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }


    private void handleData(Bundle data) {
        int command = data.getInt(MSG_CMD);
        Log.d(TAG, "-(<- received command data to service: command=" + command);
        if (command == CMD_JOIN_CHAT) {
            String userName = (String) data.get(KEY_USER_NAME);
            Log.i(TAG, "Joining Chat... Connecting as User: " + userName);
            notificationDecorator.displaySimpleNotification("Joining Chat...", "Connecting as User: " + userName);
        } else if (command == CMD_LEAVE_CHAT) {
            notificationDecorator.displaySimpleNotification("Leaving Chat...", "Disconnecting");
            stopSelf();
            Log.i(TAG, "Stopping Service...");
        } else if (command == CMD_SEND_MESSAGE) {
            String messageText = (String) data.get(KEY_MESSAGE_TEXT);
            notificationDecorator.displaySimpleNotification("Sending message...", messageText);
            Log.i(TAG, "Sending message..." + messageText);
        } else if (command == CMD_RECEIVE_MESSAGE) {
            String testUser = "User2";
            String testMessage = "Simulated Message";
            notificationDecorator.displaySimpleNotification("New message...: "+ testUser, testMessage);
            Log.i(TAG, "New message...: "+ testUser + "Test message : " + testMessage);
        } else if (command == CMD_CONNECT_ERROR_25) {
            //String testUser = "User2";
            //String testMessage = "Simulated Message";
            notificationDecorator.displaySimpleNotification("Connect Error",  "Connect Error : 25");
            Log.i(TAG, "Connect Error : 25");
        } else if (command == CMD_SEND_RANDOM_ID) {
            //String testUser = "User2";
            Random rand = new Random();
            int maxNumber = 10;

            int randomNumber = rand.nextInt(maxNumber) + 1;
            notificationDecorator.displaySimpleNotification("Random ID ", "Random ID : " + randomNumber);
            Log.i(TAG, "Connect Error : 25");
        } else {
            Log.w(TAG, "Ignoring Unknown Command! id=" + command);
        }
    }
}

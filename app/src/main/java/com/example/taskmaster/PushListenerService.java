package com.example.taskmaster;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient;
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationDetails;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class PushListenerService extends FirebaseMessagingService {
    public static final String TAG = PushListenerService.class.getSimpleName();

    // Intent action used in local broadcast
    public static final String ACTION_PUSH_NOTIFICATION = "push-notification";
    // Intent keys
    public static final String INTENT_SNS_NOTIFICATION_FROM = "from";
    public static final String INTENT_SNS_NOTIFICATION_DATA = "data";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "Registering push notifications token: " + token);
        MainActivity.getPinpointManager(getApplicationContext()).getNotificationClient().registerDeviceToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message: " + remoteMessage.getData());


        final NotificationClient notificationClient = MainActivity.getPinpointManager(getApplicationContext()).getNotificationClient();

        final NotificationDetails notificationDetails = NotificationDetails.builder()
                .from(remoteMessage.getFrom())
                .mapData(remoteMessage.getData())
                .intentAction(NotificationClient.FCM_INTENT_ACTION)
                .build();

        NotificationClient.CampaignPushResult pushResult = notificationClient.handleCampaignPush(notificationDetails);

        if (!NotificationClient.CampaignPushResult.NOT_HANDLED.equals(pushResult)) {

            if (NotificationClient.CampaignPushResult.APP_IN_FOREGROUND.equals(pushResult)) {
                /* Create a message that will display the raw data of the campaign push in a dialog. */
                final HashMap<String, String> dataMap = new HashMap<>(remoteMessage.getData());
                broadcast(remoteMessage.getFrom(), dataMap);
            }
        }
    }

    private void broadcast(final String from, final HashMap<String, String> dataMap) {
        Intent intent = new Intent(ACTION_PUSH_NOTIFICATION);
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from);
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, dataMap);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}


package uk.ac.aston.jonesja1.ersclient.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import uk.ac.aston.jonesja1.ersclient.MainActivity;
import uk.ac.aston.jonesja1.ersclient.R;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        String status = remoteMessage.getData().get("STATUS");
        String site = remoteMessage.getData().get("SITE");
        Log.i(TAG, "Message: " + status);

        createNotification(createMessage(status, site));
        manageLocationService(status);
    }

    private String createMessage(String status, String site) {
        StringBuilder builder = new StringBuilder();
        builder.append(site);
        if (isEmergencyState(status)) {
            builder.append(" is now in an EMERGENCY status.");
            builder.append(" Please avoid travelling to the area.");
        } else {
            builder.append(" has returned to normal conditions.");
            builder.append(" Please continue business as usual.");
        }
        return builder.toString();
    }

    private boolean isEmergencyState(String status) {
        return !"CALM".equalsIgnoreCase(status);
    }

    private void createNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Capgemini Incident Update")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotificationBuilder.build());
    }

    private void manageLocationService(String status) {
        boolean isEmergency = isEmergencyState(status);
        if (isEmergency) {
            startService(new Intent(this, UserLocationService.class));
        } else {
            stopService(new Intent(this, UserLocationService.class));
        }
    }
}

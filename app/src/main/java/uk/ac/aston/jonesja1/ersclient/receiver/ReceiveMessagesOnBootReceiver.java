package uk.ac.aston.jonesja1.ersclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import uk.ac.aston.jonesja1.ersclient.service.MessagingService;

/**
 * Receives the BOOT_COMPLETED intent when phone boots and starts the MessagingService
 * to receive updates from the ERS Server.
 */
public class ReceiveMessagesOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, MessagingService.class);
            context.startService(serviceIntent);
        }
    }

}

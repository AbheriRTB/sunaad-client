package com.abheri.sunaad.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.abheri.sunaad.R;

/**
 * Created by prasanna.ramaswamy on 20/08/16.
 */


public class AlarmBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();

        String messageText = intent.getStringExtra("MessageText");

        Intent notificationIntent = new Intent(context, ProgramFragment.class);
        PendingIntent pIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(), notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle("Demo App Notification")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageText))
                .setContentText(messageText)
                .setTicker("New Message Alert!")
                .setSmallIcon(R.drawable.sunaad_notification)
                .setContentIntent(pIntent).build();

        NotificationManager notificationManager = (NotificationManager)
                                        context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        Uri ringNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(context, ringNotification);
        mp.start();

    }
}
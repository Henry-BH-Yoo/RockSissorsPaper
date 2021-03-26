package ca.on.conec.rockscissorspaper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Notification Service
 */
public class NotificationService extends Service {
    private Timer timer = null;
    private NotificationManager notificationManager;
    private Notification notification;
    private Intent intent;
    private PendingIntent pendingIntent;

    private final static int NOTIFICATION_ID = new Random().nextInt();
    private final static int NOTIFICATION_TIMER_DURATION = 10000;
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        timer = new Timer(true);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        intent = new Intent(getApplicationContext() , MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ;
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT);

        // When the test over API 26, "channel is null" error occurs , so assign the channel id in Notification.Builder
        // Refer to https://developer.android.com/training/notify-user/channels?hl=en

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String id = "RockScissorsPaper";
            String description = "NotificationService of RockScissorsPaper";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(id, description, importance);
            notificationManager.createNotificationChannel(channel);
            notification = new Notification.Builder(getApplicationContext() , id)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentTitle((getString(R.string.encourage_title)))
                    .setContentText(getString(R.string.encourage_message))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentTitle((getString(R.string.encourage_title)))
                    .setContentText(getString(R.string.encourage_message))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notificationManager.notify(NOTIFICATION_ID , notification);
                timer.cancel();
                stopSelf();
            }
        } , NOTIFICATION_TIMER_DURATION);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
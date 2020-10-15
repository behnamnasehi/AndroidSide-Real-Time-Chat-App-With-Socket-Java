package android.behnamnasehi.chatApp.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.activity.ChatBoxActivity;
import android.behnamnasehi.chatApp.application.Functions;
import android.behnamnasehi.chatApp.dataholder.SessionManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.Contract;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    public static final String TYPE_NOTIFICATION_ORDER = "App\\Order";
    public static final String TYPE_NOTIFICATION_ARTICLE = "App\\Article";
    public static final String TYPE_NOTIFICATION_RATE = "RATE";
    private String APPLICATION_CHANNEL_ID = "BanaanApplicationNotifications";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        new SessionManager(this).setFcmToken(token);
        Functions.refreshUserFCMToken(new SessionManager(this).getPublicKey(), token);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        String model = data.get("model");
        String type = data.get("type");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createApplicationChannel();

        showNotify(title, body, Integer.parseInt(data.get("model_id")), getNotificationIntent(model, type, data));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createApplicationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(APPLICATION_CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(APPLICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Notifications");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Contract(pure = true)
    private PendingIntent getNotificationIntent(@NonNull String model, String type, Map<String, String> data) {
        Intent intent;
        intent = new Intent(this, ChatBoxActivity.class);
        intent.putExtra("room", data.get("room"));
        return PendingIntent.getActivity(this, 4273, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showNotify(String title, String body, int modelID, PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, APPLICATION_CHANNEL_ID);
        mBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle(mBuilder).bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setVibrate(new long[]{300, 300, 300, 300, 300})
                .setLights(Color.GREEN, 1000, 2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(modelID, mBuilder.build());
        }
    }
}

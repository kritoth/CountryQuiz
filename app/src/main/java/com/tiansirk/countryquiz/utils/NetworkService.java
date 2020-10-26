package com.tiansirk.countryquiz.utils;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.tiansirk.countryquiz.R;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.tiansirk.countryquiz.App.CHANNEL_ID;
import static com.tiansirk.countryquiz.MainActivity.EXTRA_KEY_URL;

public class NetworkService extends IntentService {

    public static final String TAG = NetworkService.class.getSimpleName();
    public static final String CONTENT_TITLE = "Quiz Service";
    public static final String CONTENT_TEXT = "Downloading country data for the quiz.";
    public static final int ID = 1;
    private PowerManager.WakeLock wakeLock; //To ensures finishing the services even if the device goes to sleep

    public NetworkService() {
        super(TAG);
    }

    /**
     * Used to (1) display a persistent notification and promote this service as a foregroundService on
     * devices with Android O (API 26) or higher. (2) Keep the CPU running even after the screen is turned off,
     * with a partial wake Lock using the {@link PowerManager}’s newWakeLock method.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: started");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG+":wakeLock");
        wakeLock.acquire(120000);
        Log.d(TAG, "onCreate: wakeLock acquired");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(CONTENT_TITLE)
                    .setContentText(CONTENT_TEXT)
                    .setSmallIcon(R.drawable.ic_outline_cloud_download_24)
                    .build();
            startForeground(ID, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: started");
        String url = intent.getStringExtra(EXTRA_KEY_URL);
        NetworkUtils.downloadAllCountries(url);
    }

    /**
     * Important to release the wakeLock when the service ends!
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        Log.d(TAG, "onDestroy: wakeLock released");
    }
}

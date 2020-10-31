package com.tiansirk.countryquiz;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.ResultReceiver;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_RUNNING;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;
import static com.tiansirk.countryquiz.App.CHANNEL_ID;
import static com.tiansirk.countryquiz.MainActivity.EXTRA_KEY_RECEIVER;
import static com.tiansirk.countryquiz.MainActivity.EXTRA_KEY_URL;

public class NetworkService extends IntentService {

    public static final String TAG = NetworkService.class.getSimpleName();
    public static final String URL_ALL_COUNTRY = "https://restcountries.eu/rest/v2/all";
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
        Timber.d("started");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG+":wakeLock");
        wakeLock.acquire(120000);
        Timber.d("wakeLock acquired in onCreate");

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
        Timber.d("Starting service for OkHttp");
        final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_KEY_RECEIVER);
        String url = intent.getStringExtra(EXTRA_KEY_URL);
        final Bundle b = new Bundle();
        if (url.equals(URL_ALL_COUNTRY)) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Timber.e(e.toString());
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Timber.d("Response failed within okHttp");
                        b.putString("failed", response.toString());
                        receiver.send(STATUS_FAILED, b);
                        throw new IOException("Unexpected code " + response);
                    } else {
                        Timber.d("Response succeeded within okHttp");
                        // parse the result then send it back
                        b.putString("results", response.body().string());
                        receiver.send(STATUS_SUCCESSFUL, b);
                    }
                }
            });

        }
    }

    /**
     * Important to release the wakeLock when the service ends!
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        Timber.d("wakeLock released in onDestroy");
    }
}

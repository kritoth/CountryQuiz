package com.tiansirk.countryquiz.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import timber.log.Timber;

public class MyResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public MyResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            Timber.d("Result received");
            mReceiver.onReceiveResult(resultCode, resultData);
        } else {
            Timber.d("Result received BUT it's null");
        }
    }
}

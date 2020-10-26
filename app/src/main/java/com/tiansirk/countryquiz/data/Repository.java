package com.tiansirk.countryquiz.data;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();
    //FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

    private Context activityContext;
    private Country[] mCountries;

    public Repository(Context activityContext) {
        this.activityContext = activityContext;
    }

    /** CREATE */


    /** READ */


    /** UPDATE */


    /** DELETE */

}

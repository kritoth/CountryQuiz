package com.tiansirk.countryquiz.utils;

import android.util.Log;

import com.tiansirk.countryquiz.data.Country;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    public static final String TAG = NetworkUtils.class.getSimpleName();
    public static final String URL_ALL_COUNTRY = "https://restcountries.eu/rest/v2/all";

    /**
     * Reads from the http URL and returns its response's String representation to be used by GSON for parsing.
     * Uses OKHttp library: https://github.com/square/okhttp
     */
    public static void downloadAllCountries(String url){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // parse the result then store into Firestore
                    storeJsonToFirestore(getCountriesFromJson(response.body().string()));
                }
            }
        });
    }

    /**
     * Parses the json into list of {@link com.tiansirk.countryquiz.data.Country} objects
     * @param json a JSON String to parse
     * @return List of Country objects parsed
     */
    public static List<Country> getCountriesFromJson(String json) {

    }

    /**
     * Stores the list of {@link com.tiansirk.countryquiz.data.Country} objects into Firestore as a subcollection of
     * User document.
     * @param countries to store
     */
    private static void storeJsonToFirestore(List<Country> countries){

    }
}

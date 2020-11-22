package com.tiansirk.countryquiz.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tiansirk.countryquiz.model.Country;
import com.tiansirk.countryquiz.model.CountryJson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for using Gson, see: https://github.com/google/gson
 */
public class JsonUtils {

    /**
     * Serialize (ie. converts) the {@link CountryJson} object to JSON formatted String.
     * Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeCountryToJson(CountryJson country){
        Gson gson = new Gson();
        return gson.toJson(country);
    }

    /**
     * Deserialize (ie. reads from) the JSON and creates a {@link CountryJson} object accordingly.
     * Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     * @return the {@link CountryJson}
     */
    public static CountryJson getCountryFromJson(String json){
        Gson gson = new Gson();
        CountryJson country = gson.fromJson(json, CountryJson.class);
        return country;
    }

    /**
     * Serialize (ie. converts) the List of {@link CountryJson} objects to JSON formatted String.
     * Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeCountriesToJson(List<CountryJson> countries){
        Gson gson = new Gson();
        return gson.toJson(countries);
    }

    /**
     * Deserialize (ie. reads from) the JSON and creates Link of {@link CountryJson} objects accordingly.
     * Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java objects.
     * @return the List of {@link CountryJson}s
     */
    public static List<CountryJson> getCountryJsonsFromJson(String json){
        //Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Gson gson = new Gson();
        Type countryListType = new TypeToken<ArrayList<CountryJson>>(){}.getType();
        List<CountryJson> countries = gson.fromJson(json, countryListType);
        return countries;
    }

}

package com.tiansirk.countryquiz.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tiansirk.countryquiz.data.Country;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    /**
     * Serialize (ie. converts) the {@link Country} object to JSON formatted String.
     * Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeCountryToJson(Country country){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(country);
    }

    /**
     * Deserialize (ie. reads from) the JSON and creates a {@link Country} object accordingly.
     * Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     * @return the {@link Country}
     */
    public static Country getCountryFromJson(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Country country = gson.fromJson(json, Country.class);
        return country;
    }

    /**
     * Serialize (ie. converts) the List of {@link Country} objects to JSON formatted String.
     * Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeCountriesToJson(List<Country> countries){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(countries);
    }

    /**
     * Deserialize (ie. reads from) the JSON and creates Link of {@link Country} objects accordingly.
     * Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java objects.
     * @return the List of {@link Country}s
     */
    public static List<Country> getCountriesFromJson(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type listType = new TypeToken<ArrayList<Country>>(){}.getType();
        List<Country> countries = gson.fromJson(json, listType);
        return countries;
    }

}

package com.tiansirk.countryquiz.utils;

import com.tiansirk.countryquiz.model.Country;
import com.tiansirk.countryquiz.model.CountryJson;
import com.tiansirk.countryquiz.model.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryUtils {

    public static List<Country> getCountriesFromJson(String... json) {
        List<CountryJson> countryJsons = JsonUtils.getCountriesFromJson(json[0]);
        List<Country> countries = new ArrayList<>();
        for(CountryJson cj : countryJsons){
            countries.add(new Country(cj.getName(),
                    new ArrayList<String>(Arrays.asList(cj.getTopLevelDomain())),
                    cj.getAlpha3Code(),
                    cj.getCapital(),
                    cj.getRegion(),
                    cj.getPopulation(),
                    cj.getArea(),
                    new ArrayList<String>(Arrays.asList(cj.getTimezones())),
                    new ArrayList<String>(Arrays.asList(cj.getBorders())),
                    new ArrayList<Currency>(Arrays.asList(cj.getCurrencies())),
                    cj.getFlag()
            ));
        }
        return countries;
    }
}

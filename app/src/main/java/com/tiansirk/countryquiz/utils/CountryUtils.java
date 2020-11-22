package com.tiansirk.countryquiz.utils;

import com.tiansirk.countryquiz.model.Country;
import com.tiansirk.countryquiz.model.CountryJson;
import com.tiansirk.countryquiz.model.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryUtils {

    /** Parses the {@param json} and creates {@link Country} objects from it. */
    public static List<Country> getCountriesFromJson(String... json) {
        List<CountryJson> countryJsons = JsonUtils.getCountryJsonsFromJson(json[0]);
        return convertCountryJsonsToCountries(countryJsons);
    }

    /** Converts  list of {@link CountryJson} objects to list of {@link Country} objects*/
    private static List<Country> convertCountryJsonsToCountries (List<CountryJson> countryJsons){
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
                    new ArrayList<String>(convertCodeToName(cj.getBorders(), countryJsons)),
                    new ArrayList<Currency>(Arrays.asList(cj.getCurrencies())),
                    cj.getFlag()
            ));
        }
        return countries;
    }

    /** Converts the {@param alpha3Code} to the name of the respective {@link Country}*/
    private static List<String> convertCodeToName(String[] alpha3codes, List<CountryJson> countryJsons) {
        List<String> names = new ArrayList<>();
        for(String code: alpha3codes){
            for (int i=0;i<countryJsons.size();i++) {
                String string = countryJsons.get(i).getAlpha3Code();
                if(string.matches(code)){
                    names.add(countryJsons.get(i).getName());
                }
            }
        }
        return names;
    }
}

package com.tiansirk.countryquiz.model;

/**
 * Model class to create by GSON from the JSON response from API. The array of Currency objects
 * are to transform to 2 distinct String fields. One for a substring of the value of "name" and
 * One for the value of "symbol"
 */
public class CountryJson {

    String name;            //"Afghanistan"
    String[] topLevelDomain;  //[".af"]
    String capital;         //"Kabul"
    String region;          //"Asia"
    int population;         //27657145
    double area;            //652230.0
    String[] timezones;     //["UTC+04:30"]
    String[] borders;       //["IRN","PAK","TKM","UZB","TJK","CHN"]
    Currency[] currencies;  //[{"code": "COP", "name": "Colombian peso", "symbol": "$"}] -> String currencyName, String currencySymbol
    String flag;            //"https://restcountries.eu/data/afg.svg"


}

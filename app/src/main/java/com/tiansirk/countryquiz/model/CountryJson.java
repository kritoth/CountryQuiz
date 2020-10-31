package com.tiansirk.countryquiz.model;

import java.util.Arrays;

/**
 * Model class to create by GSON from the JSON response from API. The array of {@link Currency} objects
 * are to transform to 2 distinct String fields. One for a substring of the value of "name" and
 * One for the value of "symbol"
 */
public class CountryJson {

    String name;            //"Afghanistan"
    String[] topLevelDomain;  //[".af"]
    String alpha3Code;      //"AFG"
    String capital;         //"Kabul"
    String region;          //"Asia"
    int population;         //27657145
    double area;            //652230.0
    String[] timezones;     //["UTC+04:30"]
    String[] borders;       //["IRN","PAK","TKM","UZB","TJK","CHN"]
    Currency[] currencies;  //[{"code": "COP", "name": "Colombian peso", "symbol": "$"}] -> String currencyName, String currencySymbol
    String flag;            //"https://restcountries.eu/data/afg.svg"

    public CountryJson(String name, String[] topLevelDomain, String alpha3Code, String capital, String region, int population, double area, String[] timezones, String[] borders, Currency[] currencies, String flag) {
        this.name = name;
        this.topLevelDomain = topLevelDomain;
        this.alpha3Code = alpha3Code;
        this.capital = capital;
        this.region = region;
        this.population = population;
        this.area = area;
        this.timezones = timezones;
        this.borders = borders;
        this.currencies = currencies;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
    }

    public String[] getTopLevelDomain() {
        return topLevelDomain;
    }

    public void setTopLevelDomain(String[] topLevelDomain) {
        this.topLevelDomain = topLevelDomain;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String[] getTimezones() {
        return timezones;
    }

    public void setTimezones(String[] timezones) {
        this.timezones = timezones;
    }

    public String[] getBorders() {
        return borders;
    }

    public void setBorders(String[] borders) {
        this.borders = borders;
    }

    public Currency[] getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Currency[] currencies) {
        this.currencies = currencies;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "CountryJson{" +
                "name='" + name + '\'' +
                ", topLevelDomain=" + Arrays.toString(topLevelDomain) +
                ", alpha3Code='" + alpha3Code + '\'' +
                ", capital='" + capital + '\'' +
                ", region='" + region + '\'' +
                ", population=" + population +
                ", area=" + area +
                ", timezones=" + Arrays.toString(timezones) +
                ", borders=" + Arrays.toString(borders) +
                ", currencies=" + Arrays.toString(currencies) +
                ", flag='" + flag + '\'' +
                '}';
    }
}

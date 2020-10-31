package com.tiansirk.countryquiz.model;

import java.util.Arrays;

/**
 * Model class for Firestore document: Country
 */
public class Country {

    String name;            //"Afghanistan"
    String[] topLevelDomain;  //[".af"]
    String alpha3Code;      //"AFG"
    String capital;         //"Kabul"
    String region;          //"Asia"
    int population;         //27657145
    double area;            //652230.0
    String[] timezones;     //["UTC+04:30"]
    String[] borders;       //["IRN","PAK","TKM","UZB","TJK","CHN"]
    String currencyName;    //"̶C̶o̶l̶o̶̶m̶b̶i̶a̶n̶ peso"
    String currencySymbol;  //"$"
    String flag;            //"https://restcountries.eu/data/afg.svg"

    public Country(String name, String[] topLevelDomain, String alpha3Code, String capital, String region, int population, double area, String[] timezones, String[] borders, String currencyName, String currencySymbol, String flag) {
        this.name = name;
        this.topLevelDomain = topLevelDomain;
        this.alpha3Code = alpha3Code;
        this.capital = capital;
        this.region = region;
        this.population = population;
        this.area = area;
        this.timezones = timezones;
        this.borders = borders;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getTopLevelDomain() {
        return topLevelDomain;
    }

    public void setTopLevelDomain(String[] topLevelDomain) {
        this.topLevelDomain = topLevelDomain;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
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

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", topLevelDomain=" + Arrays.toString(topLevelDomain) +
                ", alpha3Code='" + alpha3Code + '\'' +
                ", capital='" + capital + '\'' +
                ", region='" + region + '\'' +
                ", population=" + population +
                ", area=" + area +
                ", timezones=" + Arrays.toString(timezones) +
                ", borders=" + Arrays.toString(borders) +
                ", currencyName='" + currencyName + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }
}

package com.tiansirk.countryquiz.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Model class for Country to be used in game. This must be made from raw JSON parsed model classes
 */
public class Country {

    //The auto ID given by Firestore. It is needed to have during queries for identification, sorting, etc purposes
    private String documentId;
    private String name;                  //"Afghanistan"
    private List<String> topLevelDomain;  //".af" - transformed from array
    private String alpha3Code;            //"AFG"
    private String capital;               //"Kabul"
    private String region;                //"Asia"
    private int population;               //27657145
    private double area;                  //652230.0
    private List<String> timezones;       //["UTC+04:30"]
    private List<String> borders;         //["IRN","PAK","TKM","UZB","TJK","CHN"]
    private List<Currency> currencies;    //[{"code": "COP", "name": "Colombian peso", "symbol": "$"}, ...]
    private String flag;                  //"https://restcountries.eu/data/afg.svg"

    /** Empty constructor is needed for Firestore to be able to recreate the Object from its Document */
    public Country() {
    }

    public Country(String name, List<String> topLevelDomain, String alpha3Code, String capital, String region, int population, double area,
                   List<String> timezones, List<String> borders, List<Currency> currencies, String flag) {
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

    //Need to be excluded from Firestore's autogenereting the Object
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTopLevelDomain() {
        return topLevelDomain;
    }

    public void setTopLevelDomain(List<String> topLevelDomain) {
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

    public List<String> getTimezones() {
        return timezones;
    }

    public void setTimezones(List<String> timezones) {
        this.timezones = timezones;
    }

    public List<String> getBorders() {
        return borders;
    }

    public void setBorders(List<String> borders) {
        this.borders = borders;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
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
        return "Country{" +
                "documentId='" + documentId + '\'' +
                ", name='" + name + '\'' +
                ", topLevelDomain=" + topLevelDomain +
                ", alpha3Code='" + alpha3Code + '\'' +
                ", capital='" + capital + '\'' +
                ", region='" + region + '\'' +
                ", population=" + population +
                ", area=" + area +
                ", timezones=" + timezones +
                ", borders=" + borders +
                ", currencies=" + currencies +
                ", flag='" + flag + '\'' +
                '}';
    }
}

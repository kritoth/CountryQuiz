package com.tiansirk.countryquiz.model;

/**
 * Model class to create by GSON from the JSON response from API. This Currency object
 * is to be transformed to 2 distinct String fields when the recreated {@link Country} object is to be stored into Firestore.
 * One for a substring of the value of "name" and one for the value of "symbol".
 */
public class Currency {

    String code;    //"AFN"
    String name;    //"Afghan afghani"
    String symbol;  //"؋"

    public Currency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}

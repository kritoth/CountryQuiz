package com.tiansirk.countryquiz.utils;

import android.os.Build;

import com.tiansirk.countryquiz.model.Country;
import com.tiansirk.countryquiz.model.Question;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class GenerateQuestionUtils {
    public static final int NUM_OF_WRONG_ANSWERS = 3;

    public static List<Question> generateQuestions(List<Country> countries) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            String name = country.getName();
            Field[] declFields = country.getClass().getDeclaredFields();
            List<Field> declaredFields = new ArrayList<>(Arrays.asList(declFields));

            for(Field declaredField : declaredFields) {
                String question = buildQuestion(name, declaredField);
                String  rightAnswer = buildRightAnswer(declaredField, country);
                List<String>  wrongAnswers = buildWrongAnswers(declaredField, countries, i);
                //if(!question.isEmpty) questions.add(new Question(i, question, rightAnswer, wrongAnswers));
            }
        }
        return questions;
    }

    private static String buildQuestion(String countryName, Field countryField) {
        String subject = countryField.getName();
        Timber.d("field name: %s", subject);
        switch(subject) {
            case ("documentId"):
            case ("alpha3Code"):
            case ("name"):
                break;
            case ("topLevelDomain"):
                return String.format("What is the domain of %s used on the internet?", countryName);
            case ("capital"):
                return String.format("What is the %s of %s?", subject, countryName);
            case ("region"):
                return String.format("In which %s can %s be found?", subject, countryName);
            case ("population"):
            case ("area"):
                return String.format("What is the approximate %s of %s?", subject, countryName);
            case ("timezones"):
                return String.format("In which time zone can %s be found?", countryName);
            case ("borders"):
                return String.format("Which country is a neighbor to %s?", countryName);
            case ("currencies"):
                return String.format("Which is the currency of %s?", countryName);
            case ("flag"):
                return String.format("Which is the %s of %s?", subject, countryName);
        }
        return "";
    }


    private static String buildRightAnswer(Field countryField, Country country){
        //String fieldType = countryField.toGenericString();
        String subject = countryField.getName();
        switch(subject) {
            case ("documentId"):
            case ("alpha3Code"):
            case ("name"):
                break;
            case ("topLevelDomain"):
                if (country.getTopLevelDomain().isEmpty()
                        || country.getTopLevelDomain().get(0).isEmpty()) return "Has no internet domain";
                else return country.getTopLevelDomain().get(0);
            case ("capital"):
                return country.getCapital();
            case ("region"):
                return country.getRegion();
            case ("population"):
                return roundPopulation(country.getPopulation());
            case ("area"):
                return roundArea(country.getArea());
            case ("timezones"):
                if (country.getTimezones().isEmpty()
                        || country.getTimezones().get(0).isEmpty()) return "There is no time zone applied";
                else return country.getTimezones().get(0);
            case ("borders"):
                if (country.getBorders().isEmpty()
                        || country.getBorders().get(0).isEmpty()) return "Has no neighbor";
                else return country.getBorders().get(0);
            case ("currencies"):
                if (country.getCurrencies().isEmpty()
                        || country.getCurrencies().get(0).getName().isEmpty()) return "Has no currency applied";
                else return country.getCurrencies().get(0).getName();
            case ("flag"):
                return country.getFlag();
        }
        return "";
    }


    private static String roundPopulation(int population){

    }


    private static String roundArea(double area){

    }


    private static List<String> buildWrongAnswers(Field countryField, List<Country> countries, int rightAnswerIndex) {
        List<String> wrongAnswers = new ArrayList<>();

        int[] nums = getUniqueRandomNumbers(NUM_OF_WRONG_ANSWERS, rightAnswerIndex, countries.size());

        for (int i = 0; i < nums.length; i++) {
            while (true) {
                String cap = countries.get(nums[i]).getCapital();


            }
        }

        wrongAnswers.add(countries.get(nums[0]).getCapital());
        wrongAnswers.add(countries.get(nums[1]).getCapital());
        wrongAnswers.add(countries.get(nums[2]).getCapital());

        return wrongAnswers;
    }


    private static int[] getUniqueRandomNumbers(int numOfNums, int excludeNum, int endNum) {
        int[] uniqueRandomNums = new int[numOfNums];
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < endNum; i++) {
            list.add(i);
        }
        list.remove(excludeNum);
        Collections.shuffle(list);
        for (int i = 0; i < numOfNums; i++) {
            uniqueRandomNums[i] = list.get(i);
        }
        return uniqueRandomNums;
    }
}

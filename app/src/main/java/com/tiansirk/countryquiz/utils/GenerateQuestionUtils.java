package com.tiansirk.countryquiz.utils;

import com.tiansirk.countryquiz.model.Country;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class GenerateQuestionUtils {
    public static final int NUM_OF_WRONG_ANSWERS = 3;
    public static final int NUMBER_OF_QUESTIONS_IN_A_LEVEL = 10;

    /** Generates {@link Level}s for the countries received. The levels returned as list.*/
    public static List<Level> generateLevels(List<Country> countries) {
        List<Question> questions = generateQuestions(countries);
        Collections.shuffle(questions); // Ensures unique ordering

        List<Level> levels = new ArrayList<>();
        int numOfLevels = questions.size()/NUMBER_OF_QUESTIONS_IN_A_LEVEL;//ha nem osztható akkor +1
            for(int i=0; i<numOfLevels; i++){
                levels.add(new Level(i+1, questions
                        .subList(i* NUMBER_OF_QUESTIONS_IN_A_LEVEL,i*NUMBER_OF_QUESTIONS_IN_A_LEVEL+NUMBER_OF_QUESTIONS_IN_A_LEVEL),
                        0, false));
            }

        Timber.d("Num of levels: " +levels.size()
                + "\nFirst level num of questions: " +levels.get(0).getQuestions().size()
                + "\nLast level num of questions: " +levels.get(levels.size()-1).getQuestions().size()
                + "\nFirst question: " +levels.get(0).getQuestions().get(0)
                + "\nAlmost last question: " +levels.get(levels.size()-1).getQuestions().get(0));
        return levels;
    }

    /** Generates {@link Question}s for the countries received. The questions returned as list.*/
    public static List<Question> generateQuestions(List<Country> countries) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            String name = country.getName();
            Field[] declFields = country.getClass().getDeclaredFields();
            List<Field> declaredFields = new ArrayList<>(Arrays.asList(declFields));
            Collections.shuffle(declaredFields); // Ensures unique ordering
            for (Field declaredField : declaredFields) {
                String question = buildQuestion(name, declaredField);
                if (question.isEmpty()) continue;// if question is empty the field is not relevant!
                String rightAnswer = buildRightAnswer(declaredField, country);
                List<String> wrongAnswers = buildWrongAnswers(declaredField, countries, i);
                questions.add(new Question(i, question, rightAnswer, wrongAnswers));
            }
        }
        Timber.d("No. of generated questions: #%s", questions.size());
        return questions;
    }

        /** Building the question for the country received with using the field received.
         * If the field is not relevant, an empty string is returned.*/
    private static String buildQuestion(String countryName, Field countryField) {
        String subject = countryField.getName();
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
                return String.format("What is the approximate %s in square km of %s?", subject, countryName);
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

    /** Building up the right answer for each field of the country, used in the game.
     * For unused fields returns empty string.
     * For empty fields returns an answer that the country doesn't have that feature. */
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

    /** Building up answers for each field ensuring neither one is related to the country in question,
     * by using the index of it. The number of answers is defined as constant.
     * For not relevant fields returns empty string.
     * For empty fields returns an answer that the country doesn't have that feature. */
    private static List<String> buildWrongAnswers(Field countryField, List<Country> countries, int rightAnswerIndex) {
        List<String> wrongAnswers = new ArrayList<>();
        int[] nums = getUniqueRandomNumbers(NUM_OF_WRONG_ANSWERS, rightAnswerIndex, countries.size()); //the rightAnswerIndex must not be among these
        for (int i = 0; i < nums.length; i++) {
            String answ= buildRightAnswer(countryField, countries.get(nums[i]));
            wrongAnswers.add(answ); //get a random country, using an index from the unique random numbers array
        }
        //to make sure no duplicated answers present check with a Set and do some recursion if needed
        Set <String> set = new HashSet<>(wrongAnswers);
        if(set.size()<wrongAnswers.size()){
            buildWrongAnswers(countryField, countries, rightAnswerIndex);
        }
        return wrongAnswers;
    }

    /** Returns a formatted String version of {@param population}, rounded according to its value */
    private static String roundPopulation(int population){
        if(population<1001) return String.format("|%,d|", Math.round(population/10.0)*10);
        else if(population<50001) return String.format("|%,d|", Math.round(population/10.0)*100);
        else if(population<1000001) return String.format("|%,d|", Math.round(population/10.0)*1000);
        else return String.format("|%,d|", Math.round(population/10.0)*100000);
    }

    /** Returns a formatted String version of {@param area}, rounded according to its value */
    private static String roundArea(double area){
        int a = (int) area;
        if(area<101) return String.format("|%,d|", a);
        else if(area<1001) return String.format("|%,d|", Math.round(a/10.0)*10);
        else if(area<50001) return String.format("|%,d|", Math.round(a/10.0)*100);
        else if(area<1000001) return String.format("|%,d|", Math.round(a/10.0)*1000);
        else return String.format("|%,d|", Math.round(a/10.0)*100000);
    }

    /** Generates unique random numbers between 0 inclusive and {@param endNum} exclusive,
     * {@param numOfNums} times, excluding {@param excludeNum}. */
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

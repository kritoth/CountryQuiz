package com.tiansirk.countryquiz.utils;

import android.content.Context;

import com.tiansirk.countryquiz.R;
import com.tiansirk.countryquiz.model.Country;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class GenerateQuestionUtils {
    public static final int NUM_OF_WRONG_ANSWERS = 3;
    public static final int NUMBER_OF_QUESTIONS_IN_A_LEVEL = 10;

    /** Generates {@link Level}s for the countries received. The levels returned as list.*/
    public static List<Level> generateLevels(List<Country> countries, Context context) {
        List<Question> questions = generateQuestions(countries, context);
        List<Level> levels = new ArrayList<>();
        int numOfLevels = questions.size()/NUMBER_OF_QUESTIONS_IN_A_LEVEL;//ha nem osztható akkor +1
            for(int i=0; i<numOfLevels; i++){
                levels.add(new Level(i+1, questions
                        .subList(i* NUMBER_OF_QUESTIONS_IN_A_LEVEL,i*NUMBER_OF_QUESTIONS_IN_A_LEVEL+NUMBER_OF_QUESTIONS_IN_A_LEVEL),
                        0, false));
            }
//            for(Level level : levels){
//                Timber.d("Order in levels when is about to be sent: %s", level.getLevel());
//            }
        return levels;
    }

    /** Generates {@link Question}s for the countries received. The questions returned as list.*/
    public static List<Question> generateQuestions(List<Country> countries, Context context) {
        List<Question> questions = new ArrayList<>();
        Collections.shuffle(countries); // Ensures unique ordering
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            String name = country.getName();
            Field[] declFields = country.getClass().getDeclaredFields();
            List<Field> declaredFields = new ArrayList<>(Arrays.asList(declFields));
            Collections.shuffle(declaredFields); // Ensures unique ordering
            for (Field declaredField : declaredFields) {
                String question = buildQuestion(name, declaredField, context);
                if (question.isEmpty()) continue;// if question is empty the field is not relevant!
                String rightAnswer = buildRightAnswer(declaredField, country, context);
                List<String> wrongAnswers = buildWrongAnswers(rightAnswer, declaredField, countries, i, context);
                questions.add(new Question(i+1, question, rightAnswer, wrongAnswers));
            }
        }
        Timber.d("No. of generated questions: #%s", questions.size());
        Collections.shuffle(questions); // Ensures unique and random ordering
        return questions;
    }

    /** Building the question for the country received with using the field received.
     * If the field is not relevant, an empty string is returned.*/
    private static String buildQuestion(String countryName, Field countryField, Context context) {
        String subject = countryField.getName();
        switch(subject) {
            case ("documentId"):
            case ("alpha3Code"):
            case ("name"):
                break;
            case ("topLevelDomain"):
                return String.format(context.getString(R.string.question_topLevelDomain), countryName);
            case ("capital"):
                return String.format(context.getString(R.string.question_capital), subject, countryName);
            case ("region"):
                return String.format(context.getString(R.string.question_region), subject, countryName);
            case ("population"):
                return String.format(context.getString(R.string.question_population), subject, countryName);
            case ("area"):
                return String.format(context.getString(R.string.question_area), subject, countryName);
            case ("timezones"):
                return String.format(context.getString(R.string.question_timezones), countryName);
            case ("borders"):
                return String.format(context.getString(R.string.question_borders), countryName);
            case ("currencies"):
                return String.format(context.getString(R.string.question_currencies), countryName);
            case ("flag"):
                return String.format(context.getString(R.string.question_flag), subject, countryName);
        }
        return "";
    }

    /** Building up the right answer for each field of the country, used in the game.
     * For unused fields returns empty string.
     * For empty fields returns an answer that the country doesn't have that feature. */
    private static String buildRightAnswer(Field countryField, Country country, Context context){
        //String fieldType = countryField.toGenericString();
        String subject = countryField.getName();
        switch(subject) {
            case ("documentId"):
            case ("alpha3Code"):
            case ("name"):
                break;
            case ("topLevelDomain"):
                if (country.getTopLevelDomain().isEmpty()
                        || country.getTopLevelDomain().get(0).isEmpty()) return context.getString(R.string.answer_no_domain);
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
                if (country.getTimezones().isEmpty() || country.getTimezones().get(0).isEmpty())
                    return context.getString(R.string.answer_no_timezones);
                else return country.getTimezones().get(0);
            case ("borders"):
                if (country.getBorders().isEmpty() || country.getBorders().get(0).isEmpty())
                    return context.getString(R.string.answer_no_borders);
                else return country.getBorders().get(0);
            case ("currencies"):
                if (country.getCurrencies().isEmpty() || country.getCurrencies().get(0).getName().isEmpty())
                    return context.getString(R.string.answer_no_currency);
                else{
                    String currency = country.getCurrencies().get(0).getName();
                    if(currency.contains(" ")) {
                        return currency.substring(currency.indexOf(" ") + 1);
                    }else {
                        return currency;
                    }
                }
            case ("flag"):
                return country.getFlag();
        }
        return "";
    }

    /** Building up answers for each field ensuring neither one is related to the country in question,
     * by using the index of it. The number of answers is defined as constant.
     * For not relevant fields returns empty string.
     * For empty fields returns an answer that the country doesn't have that feature. */
    private static List<String> buildWrongAnswers(String rightAnswer, Field countryField,
                                                  List<Country> countries, int rightAnswerIndex, Context context) {
        List<String> wrongAnswers = new ArrayList<>();
        int[] nums = getUniqueRandomNumbers(NUM_OF_WRONG_ANSWERS, rightAnswerIndex, countries.size()); //the rightAnswerIndex must not be among these
        for (int i = 0; i < nums.length; i++) {
            String answ = buildRightAnswer(countryField, countries.get(nums[i]), context);
            //to make sure no duplicated answers present compare and do some correcting
            while(rightAnswer.equals(answ)){
                Timber.d("Regenerate equal answer!");
                answ = buildRightAnswer(countryField,
                        countries.get(getUniqueRandomNumbers(1, rightAnswerIndex, countries.size())[0]),
                        context);
            }
            wrongAnswers.add(answ);
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

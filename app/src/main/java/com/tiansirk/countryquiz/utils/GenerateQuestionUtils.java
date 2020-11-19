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
                //String  rightAnswer = buildRightAnswer(declaredField);
                //List<String>  wrongAnswers = buildWrongAnswers(declaredField, countryJsons, i);
                //questions.add(new Question(i, question, rightAnswer, wrongAnswers));
            }
        }
        return questions;
    }

}

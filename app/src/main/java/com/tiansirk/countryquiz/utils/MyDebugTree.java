package com.tiansirk.countryquiz.utils;

import timber.log.Timber;

/**
 * Own customized Timber for logging during debug build variant
 */
public class MyDebugTree extends Timber.DebugTree{

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("Class:%s: Line: %s, Method: %s",
                super.createStackElementTag(element),
                element.getLineNumber(),
                element.getMethodName());
    }
}

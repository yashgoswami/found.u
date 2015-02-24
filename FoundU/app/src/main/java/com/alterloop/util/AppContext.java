package com.alterloop.util;

/**
 * Created by Ankit on 07-12-2014.
 */
import android.content.Context;

public class AppContext {

    private static Context sAppContext;

    public static void init(Context context) {
        sAppContext = context.getApplicationContext();
    }

    public static Context get() {
        if (sAppContext == null) throw new RuntimeException("AppContext was not initialized with init(context) method");
        return sAppContext;
    }

}

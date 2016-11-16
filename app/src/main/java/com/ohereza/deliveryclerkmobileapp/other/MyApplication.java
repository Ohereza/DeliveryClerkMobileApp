package com.ohereza.deliveryclerkmobileapp.other;

import android.app.Application;

/**
 * Created by rkabagamba on 11/14/2016.
 */

public class MyApplication extends Application {

    private static boolean mapActivityVisible;

    public static boolean isMapActivityVisible() {
        return mapActivityVisible;
    }

    public static void mapActivityResumed() {
        mapActivityVisible = true;
    }


    public static void mapActivityPaused() {
        mapActivityVisible = false;
    }

}

package com.example.foosball;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    private static final String preferencePlayerName = "playerName";

    public static void setPlayerName(Context context, String playerName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preferencePlayerName, playerName);
        editor.commit();
    }

    public static String getPlayerName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(preferencePlayerName, "");
    }
}

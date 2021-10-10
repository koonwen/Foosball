package com.example.foosball;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    private static final String preferencePlayerName = "playerName";
    private static final String preferenceGameCode = "gameCode";
    public static final int NUM_CHARS_GAME_CODE = 6;

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

    public static void setGameCode(Context context, String gameCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preferenceGameCode, gameCode);
        editor.commit();
    }

    public static String getGameCode(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(preferenceGameCode, "");
    }

    public static String generateGameCode() {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int numCharacters = 6;
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numCharacters; i++) {
            final int index = (int) (characters.length() * Math.random());
            final char character = characters.charAt(index);
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }
}

package com.example.foosball;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    private static final String PREFERENCE_PLAYER_NAME = "playerName";
    private static final String PREFERENCE_PLAYER_ID = "playerId";
    private static final String PREFERENCE_GAME_CODE = "gameCode";
    public static final int NUM_CHARS_GAME_CODE = 6;

    public static void setPlayerName(Context context, String playerName) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_PLAYER_NAME, playerName);
        editor.commit();
    }

    public static String getPlayerName(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREFERENCE_PLAYER_NAME, "");
    }

    public static void setPlayerId(Context context, int playerId) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCE_PLAYER_ID, playerId);
        editor.commit();
    }

    public static int getPlayerId(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PREFERENCE_PLAYER_ID, 0);
    }

    public static void setGameCode(Context context, String gameCode) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_GAME_CODE, gameCode);
        editor.commit();
    }

    public static String getGameCode(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREFERENCE_GAME_CODE, "");
    }

    public static String generateGameCode() {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < NUM_CHARS_GAME_CODE; i++) {
            final int index = (int) (characters.length() * Math.random());
            final char character = characters.charAt(index);
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }
}

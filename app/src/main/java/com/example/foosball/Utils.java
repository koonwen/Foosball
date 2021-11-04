package com.example.foosball;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    private static final String PREFERENCE_PLAYER_NAME = "playerName";
    private static final String PREFERENCE_PLAYER_ID = "playerId";
    private static final String PREFERENCE_GAME_CODE = "gameCode";
    public static final int NUM_CHARS_GAME_CODE = 6;
    public static final int HOST_PLAYER_ID = 1;

    /**
     * Saves user entered player name into the Editor, so that it can be accessed by other activities
     * @param context
     * @param playerName User entered player name
     */
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

    /**
     * Saves current player ID into the Editor, so that it can be accessed by other activities
     * @param context
     * @param playerId Current player ID
     */

    public static void setPlayerId(Context context, int playerId) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCE_PLAYER_ID, playerId);
        editor.commit();
    }

    /**
     * Returns the previously saved player ID
     * @param context
     * @return Integer player ID
     */

    public static int getPlayerId(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PREFERENCE_PLAYER_ID, 1);
    }

    /**
     * Boolean for whether the current player is the game host based on player ID
     * @param context
     * @return Boolean
     */

    public static boolean isGameHost(Context context) {
        return getPlayerId(context) == HOST_PLAYER_ID;
    }

    /**
     * Saves the current game code into the Editor so that it can be accessed by other activities
     * @param context
     * @param gameCode String current game code
     */

    public static void setGameCode(Context context, String gameCode) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_GAME_CODE, gameCode);
        editor.commit();
    }

    /**
     * Returns the previously saved game code
     * @param context
     * @return String game code
     */

    public static String getGameCode(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREFERENCE_GAME_CODE, "");
    }

    /**
     * Random generator for a 6 character game code
     * @return String gamecode
     */

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

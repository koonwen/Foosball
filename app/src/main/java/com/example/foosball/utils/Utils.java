package com.example.foosball.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Provides utility methods.
 */
public class Utils {
    private static final String PREFERENCE_PLAYER_ID = "playerId";
    private static final String PREFERENCE_GAME_CODE = "gameCode";
    public static final int NUM_CHARS_GAME_CODE = 6;
    public static final int HOST_PLAYER_ID = 1;

    /**
     * Saves current player ID so that it can be accessed by other activities.
     *
     * @param context Application context.
     * @param playerId Current player ID.
     */
    public static void setPlayerId(Context context, int playerId) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCE_PLAYER_ID, playerId);
        editor.apply();
    }

    /**
     * Returns the previously saved player ID.
     *
     * @param context Application context.
     * @return Integer player ID.
     */
    public static int getPlayerId(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PREFERENCE_PLAYER_ID, 1);
    }

    /**
     * Checks whether the current player is the game host based on player ID.
     *
     * @param context Application context.
     * @return True if the current player is the game host; false otherwise.
     */
    public static boolean isGameHost(Context context) {
        return getPlayerId(context) == HOST_PLAYER_ID;
    }

    /**
     * Saves the current game code so that it can be accessed by other activities
     *
     * @param context Application context.
     * @param gameCode Game code.
     */
    public static void setGameCode(Context context, String gameCode) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_GAME_CODE, gameCode);
        editor.apply();
    }

    /**
     * Returns the previously saved game code.
     *
     * @param context Application context.
     * @return Game code.
     */
    public static String getGameCode(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREFERENCE_GAME_CODE, "");
    }

    /**
     * Returns a random game code according to the specified requirements.
     *
     * @return Generated game code.
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

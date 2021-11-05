package com.example.foosball;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public final class MainActivityTest {

    public static final String STRING_TO_BE_TYPED = "Player A";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void intentsInit() {
        // initialize Espresso Intents capturing
        Intents.init();
    }

    @After
    public void intentsTeardown() {
        // release Espresso Intents capturing
        Intents.release();
    }

    @Test
    public void insertPlayerName() {
        // Type text and then press the button.
        onView(withId(R.id.playerName))
                .perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.playerName)).check(matches(withText(STRING_TO_BE_TYPED)));
    }

    /**
     * Note: If experiencing issues, make sure Developer Options is enabled on
     * your android device, and to turn off
     * Window animation scale
     * Transition animation scale
     * Animator duration scale
     * See: https://developer.android.com/training/testing/espresso/setup
     */
    @Test
    public void toLobby() {
        // Type text and then press the button.
        onView(withId(R.id.playerName)).perform(typeText(STRING_TO_BE_TYPED),
                closeSoftKeyboard());
        onView(withId(R.id.createGame)).perform(click());

        // Verify that intent was sent to launch LobbyActivity
        intended(hasComponent(LobbyActivity.class.getName()));
    }
}

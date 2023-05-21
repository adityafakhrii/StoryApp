package com.adityafakhri.storyapp.ui.login

import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.ui.auth.AuthActivity
import com.adityafakhri.storyapp.ui.login.utils.EspressoIdlingResource
import com.adityafakhri.storyapp.ui.story.list.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginFragmentTest {

    @get:Rule
    val activity = ActivityScenarioRule(AuthActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_logout() {
        Intents.init()
        onView(withId(R.id.emailEditText)).perform(
            typeText("adityafakhri03@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordEditText)).perform(typeText("Bazit2207"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.btn_login)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))

        //logout
        onView(withId(R.id.menu_logout)).perform(click())
    }

    @Test
    fun login_failed() {
        onView(withId(R.id.emailEditText)).perform(
            typeText("adityafakhri03@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordEditText)).perform(
            typeText("testresulterror"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_login)).perform(click())
    }
}
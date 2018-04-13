package com.alimojarrad.fair

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.alimojarrad.fair.Modules.Selection.FindCarActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith




/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UITest {
    @Rule @JvmField
    var findCarActivityTestRule = ActivityTestRule(FindCarActivity::class.java, true, true)
    @Test
    fun FindCarActivityUI() {
        onView(withId(R.id.findcaractivity_viewoptions)).perform(click())
    }
}

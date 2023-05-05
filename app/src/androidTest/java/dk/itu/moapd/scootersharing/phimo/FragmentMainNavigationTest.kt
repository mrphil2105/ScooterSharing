package dk.itu.moapd.scootersharing.phimo

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dk.itu.moapd.scootersharing.phimo.fragments.MainFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class FragmentMainNavigationTest {
    @get:Rule
    val fineLocationRule: GrantPermissionRule = GrantPermissionRule.grant(ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationRule: GrantPermissionRule = GrantPermissionRule.grant(ACCESS_COARSE_LOCATION)

    @Test
    fun testNavigationToFragmentAdd() {
        val navController = mock(NavController::class.java)
        val fragmentScenario = launchFragmentInContainer<MainFragment>()

        fragmentScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.add_ride)).perform(click())

        verify(navController).navigate(R.id.action_mainFragment_to_addRideFragment)
    }
}

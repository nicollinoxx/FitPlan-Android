package dev.hotwire.turbo.fitplanandroid.main

import android.os.Bundle
import android.webkit.CookieManager
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.hotwire.strada.KotlinXJsonConverter
import dev.hotwire.strada.Strada
import dev.hotwire.turbo.activities.TurboActivity
import dev.hotwire.turbo.delegates.TurboActivityDelegate
import dev.hotwire.turbo.fitplanandroid.R
import dev.hotwire.turbo.fitplanandroid.util.BASE_URL
import dev.hotwire.turbo.fitplanandroid.util.SESSION_COOKIE
import dev.hotwire.turbo.nav.TurboNavDestination

class MainActivity : AppCompatActivity(), TurboActivity {
    override lateinit var delegate: TurboActivityDelegate

    private val viewFlipper: ViewFlipper
        get() = findViewById(R.id.view_flipper)

    private val bottomNavigationView: BottomNavigationView
        get() = findViewById(R.id.bottom_navigation_view)

    // Menu item to nav host fragment of the tab it opens. The order matches the
    // ViewFlipper children, so a tab's position doubles as its displayedChild.
    private val tabs = listOf(
        R.id.tab_sheets to R.id.sheets_nav_host,
        R.id.tab_shares to R.id.shares_nav_host,
        R.id.tab_dashboard to R.id.dashboard_nav_host,
        R.id.tab_social to R.id.social_nav_host,
        R.id.tab_profile to R.id.profile_nav_host
    )

    // Rails session each tab last rendered under, keyed by tab position.
    private val tabSessions = mutableMapOf<Int, String?>()

    // Tabs currently sitting on a screen that asked for the bottom navigation to
    // be hidden, so switching back to one restores the right chrome.
    private val tabsHidingNavigation = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // The constructor registers the first nav host fragment; the remaining
        // tabs are registered so the delegate can switch between them later.
        delegate = TurboActivityDelegate(this, tabs.first().second)
        tabs.drop(1).forEach { (_, navHostId) -> delegate.registerNavHostFragment(navHostId) }

        Strada.config.jsonConverter = KotlinXJsonConverter()

        // Every tab starts out rendering the session the app launched with.
        tabs.indices.forEach { tabSessions[it] = sessionToken() }

        val selectedTab = savedInstanceState?.getInt(SELECTED_TAB_KEY) ?: 0

        // Check the restored item before listening, so restoring state does not
        // bounce back through the listener.
        bottomNavigationView.selectedItemId = tabs[selectedTab].first
        setupBottomNavigationView()
        selectTab(selectedTab)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_TAB_KEY, viewFlipper.displayedChild)
    }

    private fun setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val position = tabs.indexOfFirst { it.first == item.itemId }

            when (position) {
                -1 -> false
                else -> { selectTab(position); true }
            }
        }

        // Tapping the tab you are already on sends it back to its start page.
        // That is what a bottom navigation is expected to do; without it the tap
        // does nothing once the user has navigated deeper inside the tab.
        bottomNavigationView.setOnItemReselectedListener {
            returnTabToStart(viewFlipper.displayedChild)
        }
    }

    /**
     * Reselecting a tab puts it back on its start page.
     *
     * This resets the tab's session instead of popping its back stack. Popping
     * is not dependable here: Turbo registers every web page under the same
     * destination id, so popping by id matches the page already on screen, and
     * popping entry by entry walks the fragment manager through several
     * transactions in a row -- including across a modal boundary, and far
     * enough to empty the tab entirely. Resetting is a single operation that
     * always lands on the start location.
     */
    private fun returnTabToStart(position: Int) {
        delegate.navHostFragment(tabs[position].second).reset()
    }

    /**
     * Turbo keeps one session per tab. Pointing the delegate at the tab's nav
     * host fragment is what makes navigation, the back stack and the Strada
     * bridge act on the tab the user is currently looking at.
     */
    private fun selectTab(position: Int) {
        delegate.currentNavHostFragmentId = tabs[position].second
        viewFlipper.displayedChild = position
        resetTabIfSessionChanged(position)
        applyBottomNavigationVisibility(position)
    }

    /**
     * Called by every destination as it becomes visible. Screens that sign the
     * user in -- welcome, sign in, sign up, password reset -- declare
     * "bottom_navigation": "hidden" in the path configuration, because there is
     * nothing worth switching to until there is a session. Keeping the decision
     * in the path configuration means new screens opt in without touching this
     * class.
     */
    fun onDestinationStarted(destination: TurboNavDestination) {
        val position = tabs.indexOfFirst { it.second == destination.fragment.parentFragment?.id }
        if (position == -1) return

        when (destination.pathProperties[BOTTOM_NAVIGATION] == HIDDEN) {
            true -> tabsHidingNavigation.add(position)
            else -> tabsHidingNavigation.remove(position)
        }

        if (position == viewFlipper.displayedChild) {
            applyBottomNavigationVisibility(position)
        }
    }

    private fun applyBottomNavigationVisibility(position: Int) {
        bottomNavigationView.isVisible = position !in tabsHidingNavigation
    }

    /**
     * Each tab renders in its own WebView and then keeps whatever page it landed
     * on, so signing in or out leaves the other tabs showing the previous
     * session. Reloading them is not enough: a tab redirected to /welcome while
     * signed out stays on /welcome, which renders the same page either way.
     *
     * Comparing the Rails session cookie a tab rendered under against the
     * current one detects exactly that, in both directions, and only then is the
     * tab sent back to its start location -- so tabs otherwise keep their
     * history and the user returns to where they left off.
     */
    private fun resetTabIfSessionChanged(position: Int) {
        val session = sessionToken()
        if (tabSessions[position] == session) return

        tabSessions[position] = session
        delegate.navHostFragment(tabs[position].second).reset()
    }

    private fun sessionToken(): String? {
        return CookieManager.getInstance().getCookie(BASE_URL)
            ?.split(";")
            ?.map { it.trim() }
            ?.firstOrNull { it.startsWith("$SESSION_COOKIE=") }
    }

    companion object {
        private const val SELECTED_TAB_KEY = "selected_tab"
        private const val BOTTOM_NAVIGATION = "bottom_navigation"
        private const val HIDDEN = "hidden"
    }
}

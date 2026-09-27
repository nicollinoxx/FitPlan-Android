package dev.hotwire.turbo.fitplanandroid.main

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dev.hotwire.navigation.activities.HotwireActivity
import dev.hotwire.navigation.destinations.HotwireDestination
import dev.hotwire.navigation.navigator.NavigatorConfiguration
import dev.hotwire.navigation.navigator.NavigatorHost
import dev.hotwire.navigation.tabs.HotwireBottomNavigationController
import dev.hotwire.navigation.tabs.HotwireBottomNavigationController.Visibility
import dev.hotwire.navigation.tabs.HotwireBottomTab
import dev.hotwire.navigation.tabs.navigatorConfigurations
import dev.hotwire.navigation.util.applyDefaultImeWindowInsets
import dev.hotwire.turbo.fitplanandroid.R
import dev.hotwire.turbo.fitplanandroid.util.BASE_URL
import dev.hotwire.turbo.fitplanandroid.util.DASHBOARD_URL
import dev.hotwire.turbo.fitplanandroid.util.PROFILE_URL
import dev.hotwire.turbo.fitplanandroid.util.SESSION_COOKIE
import dev.hotwire.turbo.fitplanandroid.util.SHARES_URL
import dev.hotwire.turbo.fitplanandroid.util.SHEETS_URL
import dev.hotwire.turbo.fitplanandroid.util.SOCIAL_URL

class MainActivity : HotwireActivity() {
    private lateinit var bottomNavigation: HotwireBottomNavigationController

    private val bottomNavigationView: BottomNavigationView
        get() = findViewById(R.id.bottom_navigation_view)

    private val tabs by lazy {
        listOf(
            tab(R.string.tab_sheets, R.drawable.ic_tab_sheets, R.id.sheets_navigator_host, "sheets", SHEETS_URL),
            tab(R.string.tab_shares, R.drawable.ic_tab_shares, R.id.shares_navigator_host, "shares", SHARES_URL),
            tab(R.string.tab_dashboard, R.drawable.ic_tab_dashboard, R.id.dashboard_navigator_host, "dashboard", DASHBOARD_URL),
            tab(R.string.tab_social, R.drawable.ic_tab_social, R.id.social_navigator_host, "social", SOCIAL_URL),
            tab(R.string.tab_profile, R.drawable.ic_tab_profile, R.id.profile_navigator_host, "profile", PROFILE_URL)
        )
    }

    // What each tab last rendered, keyed by tab position.
    private val renderedPages = mutableMapOf<Int, RenderedPage>()

    private data class RenderedPage(val sessionToken: String?, val isAuthScreen: Boolean)

    // Tabs sitting on a screen that hides the bottom navigation.
    private val tabsHidingNavigation = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.root).applyDefaultImeWindowInsets()

        bottomNavigation = HotwireBottomNavigationController(this, bottomNavigationView, lazyLoadTabs = true)
        bottomNavigation.load(tabs, savedInstanceState?.getInt(SELECTED_TAB_KEY) ?: 0)
        bottomNavigation.setOnTabSelectedListener { position, _ -> onTabSelected(position) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_TAB_KEY, currentTab)
    }

    override fun navigatorConfigurations() = tabs.navigatorConfigurations

    /**
     * Screens that sign the user in declare "bottom_navigation": "hidden" in the
     * path configuration, since there is nothing to switch to until there is a
     * session.
     */
    fun onDestinationStarted(destination: HotwireDestination) {
        val position = positionOf(destination)
        if (position == -1) return

        when (destination.isAuthScreen) {
            true -> tabsHidingNavigation.add(position)
            else -> tabsHidingNavigation.remove(position)
        }

        if (position == currentTab) applyBottomNavigationVisibility(position)
    }

    /**
     * Records what a tab rendered, so signing in or out resets the other tabs the
     * next time they are selected.
     */
    fun onPageRendered(destination: HotwireDestination) {
        val position = positionOf(destination)
        if (position == -1 || destination.isModal) return

        renderedPages[position] = RenderedPage(sessionToken(), destination.isAuthScreen)
    }

    /**
     * A link to another tab's start page selects that tab instead of opening the
     * page inside the one on screen, so the bar never highlights the wrong tab.
     */
    fun selectTabFor(destination: HotwireDestination, location: String): Boolean {
        val from = positionOf(destination)
        val target = tabs.indexOfFirst { isStartPage(location, it) }
        if (target == -1 || target == from || from != currentTab) return false

        if (destination.isModal) destination.navigator.pop()
        bottomNavigation.selectTab(target)
        return true
    }

    fun showNotice(location: String) {
        Uri.parse(location).getQueryParameter("notice")?.let(::showMessage)
    }

    fun showMessage(message: String) {
        Snackbar.make(findViewById(R.id.root), message, Snackbar.LENGTH_SHORT).apply {
            if (bottomNavigationView.isVisible) anchorView = bottomNavigationView
        }.show()
    }

    private val currentTab: Int
        get() = bottomNavigationView.selectedItemId

    private fun onTabSelected(position: Int) {
        resetIfSessionChanged(position)
        applyBottomNavigationVisibility(position)
    }

    private fun applyBottomNavigationVisibility(position: Int) {
        bottomNavigation.visibility = if (position in tabsHidingNavigation) Visibility.HIDDEN else Visibility.DEFAULT
    }

    /**
     * A tab is out of date when the session changed since it rendered, and also
     * when it is left on an authentication screen while a session exists.
     *
     * The second case is not redundant. Signing in from a tab that was sitting on
     * the welcome screen makes that tab reload the very same screen once the modal
     * closes, which records the new session against a signed-out page. Comparing
     * tokens alone then finds nothing wrong and the tab keeps the stale screen for
     * good.
     */
    private fun resetIfSessionChanged(position: Int) {
        val rendered = renderedPages[position] ?: return
        val token = sessionToken()
        val outOfDate = rendered.sessionToken != token || (rendered.isAuthScreen && token != null)
        if (!outOfDate) return

        navigatorHost(tabs[position]).navigator.reset()
    }

    private val HotwireDestination.isAuthScreen: Boolean
        get() = pathProperties[BOTTOM_NAVIGATION] == HIDDEN

    private fun positionOf(destination: HotwireDestination): Int {
        return tabs.indexOfFirst { it.configuration.navigatorHostId == destination.navigator.configuration.navigatorHostId }
    }

    private fun isStartPage(location: String, tab: HotwireBottomTab): Boolean {
        val uri = Uri.parse(location)
        val start = Uri.parse(tab.configuration.startLocation)
        // Rails links some pages with an explicit .html format (/sheets.html), and
        // its root renders the sheets index, so it belongs to the first tab.
        val path = uri.path.orEmpty().removeSuffix(".html")
        val startPath = if (path.isEmpty() || path == "/") Uri.parse(SHEETS_URL).path else path
        return uri.host == start.host && startPath == start.path
    }

    private fun navigatorHost(tab: HotwireBottomTab): NavigatorHost {
        return supportFragmentManager.findFragmentById(tab.configuration.navigatorHostId) as NavigatorHost
    }

    private fun sessionToken(): String? {
        return CookieManager.getInstance().getCookie(BASE_URL)
            ?.split(";")
            ?.map { it.trim() }
            ?.firstOrNull { it.startsWith("$SESSION_COOKIE=") }
    }

    private fun tab(
        @StringRes title: Int,
        @DrawableRes icon: Int,
        @IdRes navigatorHostId: Int,
        name: String,
        startLocation: String
    ) = HotwireBottomTab(
        title = getString(title),
        iconResId = icon,
        configuration = NavigatorConfiguration(name = name, navigatorHostId = navigatorHostId, startLocation = startLocation)
    )

    companion object {
        private const val SELECTED_TAB_KEY = "selected_tab"
        private const val BOTTOM_NAVIGATION = "bottom_navigation"
        private const val HIDDEN = "hidden"
    }
}

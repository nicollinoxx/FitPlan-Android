package dev.hotwire.turbo.fitplanandroid.main

import android.os.Bundle
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.hotwire.strada.KotlinXJsonConverter
import dev.hotwire.strada.Strada
import dev.hotwire.turbo.activities.TurboActivity
import dev.hotwire.turbo.delegates.TurboActivityDelegate
import dev.hotwire.turbo.fitplanandroid.R

class MainActivity : AppCompatActivity(), TurboActivity {
    override lateinit var delegate: TurboActivityDelegate

    private val viewFlipper: ViewFlipper
        get() = findViewById(R.id.view_flipper)

    private val bottomNavigationView: BottomNavigationView
        get() = findViewById(R.id.bottom_navigation_view)

    // Menu item to nav host fragment of the tab it opens. The order matches the
    // ViewFlipper children, so a tab's position doubles as its displayedChild.
    private val tabs = listOf(
        R.id.tab_workouts to R.id.workouts_nav_host,
        R.id.tab_dashboard to R.id.dashboard_nav_host,
        R.id.tab_social to R.id.social_nav_host,
        R.id.tab_profile to R.id.profile_nav_host,
        R.id.tab_settings to R.id.settings_nav_host
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // The constructor registers the first nav host fragment; the remaining
        // tabs are registered so the delegate can switch between them later.
        delegate = TurboActivityDelegate(this, tabs.first().second)
        tabs.drop(1).forEach { (_, navHostId) -> delegate.registerNavHostFragment(navHostId) }

        Strada.config.jsonConverter = KotlinXJsonConverter()

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
    }

    /**
     * Turbo keeps one session per tab. Pointing the delegate at the tab's nav
     * host fragment is what makes navigation, the back stack and the Strada
     * bridge act on the tab the user is currently looking at.
     */
    private fun selectTab(position: Int) {
        delegate.currentNavHostFragmentId = tabs[position].second
        viewFlipper.displayedChild = position
    }

    companion object {
        private const val SELECTED_TAB_KEY = "selected_tab"
    }
}

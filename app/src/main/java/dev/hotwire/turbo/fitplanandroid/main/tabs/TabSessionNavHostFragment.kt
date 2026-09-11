package dev.hotwire.turbo.fitplanandroid.main.tabs

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dev.hotwire.strada.Bridge
import dev.hotwire.turbo.config.TurboPathConfiguration
import dev.hotwire.turbo.fitplanandroid.features.native.NumbersFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebBottomSheetFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebHomeFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebModalFragment
import dev.hotwire.turbo.fitplanandroid.util.customUserAgent
import dev.hotwire.turbo.session.TurboSessionNavHostFragment
import kotlin.reflect.KClass

/**
 * Base class shared by every bottom navigation tab.
 *
 * Each tab is an independent Turbo session with its own WebView and its own
 * back stack, which is what lets the user switch tabs and come back to exactly
 * where they left off.
 *
 * Subclasses only need a unique [sessionName] and a [startLocation].
 */
abstract class TabSessionNavHostFragment : TurboSessionNavHostFragment() {
    override val registeredActivities: List<KClass<out AppCompatActivity>>
        get() = listOf()

    override val registeredFragments: List<KClass<out Fragment>>
        get() = listOf(
            WebFragment::class,
            WebHomeFragment::class,
            WebModalFragment::class,
            WebBottomSheetFragment::class,
            NumbersFragment::class
        )

    override val pathConfigurationLocation: TurboPathConfiguration.Location
        get() = TurboPathConfiguration.Location(assetFilePath = "json/configuration.json")

    override fun onSessionCreated() {
        super.onSessionCreated()
        session.webView.settings.userAgentString = session.webView.customUserAgent

        // Each tab owns a WebView, so the Strada bridge is initialized per session
        Bridge.initialize(session.webView)
    }
}

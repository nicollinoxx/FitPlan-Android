package dev.hotwire.turbo.fitplanandroid

import android.app.Application
import dev.hotwire.core.bridge.BridgeComponentFactory
import dev.hotwire.core.bridge.KotlinXJsonConverter
import dev.hotwire.core.config.Hotwire
import dev.hotwire.core.turbo.config.PathConfiguration
import dev.hotwire.navigation.config.defaultFragmentDestination
import dev.hotwire.navigation.config.registerBridgeComponents
import dev.hotwire.navigation.config.registerFragmentDestinations
import dev.hotwire.turbo.fitplanandroid.features.native.NumbersFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebFragment
import dev.hotwire.turbo.fitplanandroid.strada.FlashMessageComponent
import dev.hotwire.turbo.fitplanandroid.strada.FormComponent
import dev.hotwire.turbo.fitplanandroid.strada.MenuComponent
import dev.hotwire.turbo.fitplanandroid.strada.NavButtonComponent

class FitPlanApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Hotwire.defaultFragmentDestination = WebFragment::class
        Hotwire.registerFragmentDestinations(WebFragment::class, NumbersFragment::class)

        Hotwire.registerBridgeComponents(
            BridgeComponentFactory("form", ::FormComponent),
            BridgeComponentFactory("nav-button", ::NavButtonComponent),
            BridgeComponentFactory("flash-message", ::FlashMessageComponent),
            BridgeComponentFactory("menu", ::MenuComponent)
        )

        Hotwire.config.jsonConverter = KotlinXJsonConverter()
        Hotwire.config.webViewDebuggingEnabled = BuildConfig.DEBUG

        Hotwire.loadPathConfiguration(
            context = this,
            location = PathConfiguration.Location(assetFilePath = "json/path-configuration.json")
        )
    }
}

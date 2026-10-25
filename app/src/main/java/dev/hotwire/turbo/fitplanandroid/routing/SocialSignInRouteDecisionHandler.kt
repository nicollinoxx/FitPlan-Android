package dev.hotwire.turbo.fitplanandroid.routing

import android.net.Uri
import dev.hotwire.core.turbo.visit.VisitProposal
import dev.hotwire.navigation.activities.HotwireActivity
import dev.hotwire.navigation.navigator.NavigatorConfiguration
import dev.hotwire.navigation.routing.BrowserTabRouteDecisionHandler
import dev.hotwire.navigation.routing.Router

/**
 * Google refuses to sign anyone in from a WebView, so FitPlan's own /auth/ pages,
 * which start the Google and Facebook flows, open in a Custom Tab like another
 * site would. The Rails app hands the session back to the WebView afterwards
 * (see SessionHandoff there).
 */
class SocialSignInRouteDecisionHandler : Router.RouteDecisionHandler {
    private val browserTab = BrowserTabRouteDecisionHandler()

    override val name = "social-sign-in"

    override fun matches(location: VisitProposal, configuration: NavigatorConfiguration): Boolean {
        return Uri.parse(location.location).path?.startsWith("/auth/") == true
    }

    override fun handle(location: VisitProposal, configuration: NavigatorConfiguration, activity: HotwireActivity): Router.Decision {
        return browserTab.handle(location, configuration, activity)
    }
}

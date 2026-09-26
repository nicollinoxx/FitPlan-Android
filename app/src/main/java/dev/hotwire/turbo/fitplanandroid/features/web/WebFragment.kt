package dev.hotwire.turbo.fitplanandroid.features.web

import dev.hotwire.core.turbo.errors.HttpError
import dev.hotwire.core.turbo.errors.VisitError
import dev.hotwire.core.turbo.visit.VisitProposal
import dev.hotwire.navigation.destinations.HotwireDestinationDeepLink
import dev.hotwire.navigation.fragments.HotwireWebFragment
import dev.hotwire.navigation.routing.Router
import dev.hotwire.turbo.fitplanandroid.main.MainActivity
import dev.hotwire.turbo.fitplanandroid.util.SIGN_IN_URL

@HotwireDestinationDeepLink(uri = "hotwire://fragment/web")
class WebFragment : HotwireWebFragment() {
    private val mainActivity: MainActivity?
        get() = activity as? MainActivity

    override fun onStart() {
        super.onStart()
        mainActivity?.onDestinationStarted(this)
    }

    override fun onVisitCompleted(location: String, completedOffline: Boolean) {
        super.onVisitCompleted(location, completedOffline)
        mainActivity?.onPageRendered(this)
    }

    override fun onVisitErrorReceived(location: String, error: VisitError) {
        when (error) {
            HttpError.ClientError.Unauthorized -> navigator.route(SIGN_IN_URL)
            else -> super.onVisitErrorReceived(location, error)
        }
    }

    override fun customRouteDecision(proposal: VisitProposal): Router.Decision? {
        mainActivity?.showNotice(proposal.location)

        return when (mainActivity?.selectTabFor(this, proposal.location)) {
            true -> Router.Decision.CANCEL
            else -> null
        }
    }
}

package dev.hotwire.turbo.fitplanandroid.features.web

import android.view.LayoutInflater
import android.view.View
import dev.hotwire.core.turbo.errors.HttpError
import dev.hotwire.core.turbo.errors.VisitError
import dev.hotwire.core.turbo.errors.WebError
import dev.hotwire.core.turbo.visit.VisitProposal
import dev.hotwire.navigation.destinations.HotwireDestinationDeepLink
import dev.hotwire.navigation.fragments.HotwireWebFragment
import dev.hotwire.navigation.routing.Router
import dev.hotwire.turbo.fitplanandroid.R
import dev.hotwire.turbo.fitplanandroid.main.MainActivity
import dev.hotwire.turbo.fitplanandroid.util.SIGN_IN_URL

@HotwireDestinationDeepLink(uri = "hotwire://fragment/web")
class WebFragment : HotwireWebFragment() {
    private val mainActivity: MainActivity?
        get() = activity as? MainActivity

    private var attempts = 0

    override fun onStart() {
        super.onStart()
        mainActivity?.onDestinationStarted(this)
    }

    override fun onVisitCompleted(location: String, completedOffline: Boolean) {
        super.onVisitCompleted(location, completedOffline)
        attempts = 0
        mainActivity?.onPageRendered(this)
    }

    /**
     * The server sits far from here, so a navigation that is merely slow looks
     * the same from the web view as one that failed. Reaching for the error
     * screen on the first stumble puts a failure in front of someone whose page
     * was only late, so a request that broke in transit is tried again, behind
     * the progress view, and only a run of them is reported.
     *
     * A reply the server actually sent is not retried: it is an answer, not a
     * stumble, and asking twice would not change it.
     */
    override fun onVisitErrorReceived(location: String, error: VisitError) {
        when {
            error == HttpError.ClientError.Unauthorized -> navigator.route(SIGN_IN_URL)
            error.isWorthRetrying && attempts < MAX_ATTEMPTS -> retry()
            else -> super.onVisitErrorReceived(location, error)
        }
    }

    override fun createProgressView(location: String): View {
        return LayoutInflater.from(requireContext()).inflate(R.layout.view_progress, null)
    }

    override fun customRouteDecision(proposal: VisitProposal): Router.Decision? {
        mainActivity?.showNotice(proposal.location)

        return when (mainActivity?.selectTabFor(this, proposal.location)) {
            true -> Router.Decision.CANCEL
            else -> null
        }
    }

    private fun retry() {
        attempts++
        view?.postDelayed({ if (isAdded) refresh(true) }, RETRY_DELAY_MS)
    }

    /**
     * The request never reached a reply: it timed out, the connection or the
     * name lookup failed, or a gateway gave up waiting on the origin. All of
     * them are the trip failing rather than the application answering.
     */
    private val VisitError.isWorthRetrying: Boolean
        get() = when (this) {
            is WebError.Timeout, is WebError.Connect, is WebError.HostLookup, is WebError.IO -> true
            HttpError.ClientError.RequestTimeout -> true
            HttpError.ServerError.BadGateway -> true
            HttpError.ServerError.ServiceUnavailable -> true
            HttpError.ServerError.GatewayTimeout -> true
            else -> false
        }

    companion object {
        private const val MAX_ATTEMPTS = 2
        private const val RETRY_DELAY_MS = 600L
    }
}

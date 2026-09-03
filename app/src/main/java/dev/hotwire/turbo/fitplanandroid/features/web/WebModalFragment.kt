package dev.hotwire.turbo.fitplanandroid.features.web

import android.os.Bundle
import android.view.View
import dev.hotwire.turbo.nav.TurboNavGraphDestination
import dev.hotwire.turbo.fitplanandroid.util.displayBackButtonAsCloseIcon

@TurboNavGraphDestination(uri = "turbo://fragment/web/modal")
class WebModalFragment : WebFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState); initToolbar()
    }

    private fun initToolbar() {
        toolbarForNavigation()?.displayBackButtonAsCloseIcon()
    }
}

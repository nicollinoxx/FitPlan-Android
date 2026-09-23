package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.SHARES_URL

@Suppress("unused")
class SharesSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "shares"
    override val startLocation = SHARES_URL
}

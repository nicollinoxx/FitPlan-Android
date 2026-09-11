package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.SOCIAL_URL

@Suppress("unused")
class SocialSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "social"
    override val startLocation = SOCIAL_URL
}

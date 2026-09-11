package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.PROFILE_URL

@Suppress("unused")
class ProfileSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "profile"
    override val startLocation = PROFILE_URL
}

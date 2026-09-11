package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.SETTINGS_URL

@Suppress("unused")
class SettingsSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "settings"
    override val startLocation = SETTINGS_URL
}

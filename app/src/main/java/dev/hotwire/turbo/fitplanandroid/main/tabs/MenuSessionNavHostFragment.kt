package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.MENU_URL

@Suppress("unused")
class MenuSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "menu"
    override val startLocation = MENU_URL
}

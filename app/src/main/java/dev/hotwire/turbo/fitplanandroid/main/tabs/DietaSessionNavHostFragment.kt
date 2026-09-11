package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.DIETA_URL

@Suppress("unused")
class DietaSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "dieta"
    override val startLocation = DIETA_URL
}

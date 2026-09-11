package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.TREINO_URL

@Suppress("unused")
class TreinoSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "treino"
    override val startLocation = TREINO_URL
}

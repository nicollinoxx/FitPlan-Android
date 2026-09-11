package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.PERFIL_URL

@Suppress("unused")
class PerfilSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "perfil"
    override val startLocation = PERFIL_URL
}

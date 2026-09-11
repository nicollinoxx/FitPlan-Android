package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.DASHBOARD_URL

@Suppress("unused")
class DashboardSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "dashboard"
    override val startLocation = DASHBOARD_URL
}

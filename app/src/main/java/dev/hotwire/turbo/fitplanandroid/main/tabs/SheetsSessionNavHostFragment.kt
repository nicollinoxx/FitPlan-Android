package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.SHEETS_URL

@Suppress("unused")
class SheetsSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "sheets"
    override val startLocation = SHEETS_URL
}

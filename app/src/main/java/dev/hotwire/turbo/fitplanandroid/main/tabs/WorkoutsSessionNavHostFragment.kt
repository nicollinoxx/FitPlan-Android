package dev.hotwire.turbo.fitplanandroid.main.tabs

import dev.hotwire.turbo.fitplanandroid.util.WORKOUTS_URL

@Suppress("unused")
class WorkoutsSessionNavHostFragment : TabSessionNavHostFragment() {
    override val sessionName = "workouts"
    override val startLocation = WORKOUTS_URL
}

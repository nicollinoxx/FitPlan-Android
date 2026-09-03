package dev.hotwire.turbo.fitplanandroid.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.hotwire.strada.KotlinXJsonConverter
import dev.hotwire.strada.Strada
import dev.hotwire.turbo.activities.TurboActivity
import dev.hotwire.turbo.delegates.TurboActivityDelegate
import dev.hotwire.turbo.fitplanandroid.R

class MainActivity : AppCompatActivity(), TurboActivity {
    override lateinit var delegate: TurboActivityDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        delegate = TurboActivityDelegate(this, R.id.main_nav_host)
        configApp()
    }

    private fun configApp() {
        Strada.config.jsonConverter = KotlinXJsonConverter()
    }
}

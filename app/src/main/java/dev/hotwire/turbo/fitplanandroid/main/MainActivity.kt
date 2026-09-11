package dev.hotwire.turbo.fitplanandroid.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.hotwire.strada.KotlinXJsonConverter
import dev.hotwire.strada.Strada
import dev.hotwire.turbo.activities.TurboActivity
import dev.hotwire.turbo.delegates.TurboActivityDelegate
import dev.hotwire.turbo.fitplanandroid.R

class MainActivity : AppCompatActivity(), TurboActivity {
    override lateinit var delegate: TurboActivityDelegate

    // Associa cada item do menu da bottom nav ao FragmentContainerView da sua aba.
    private val tabContainerIds = mapOf(
        R.id.nav_treino to R.id.nav_host_treino,
        R.id.nav_dieta to R.id.nav_host_dieta,
        R.id.nav_social to R.id.nav_host_social,
        R.id.nav_perfil to R.id.nav_host_perfil,
        R.id.nav_menu to R.id.nav_host_menu
    )

    private var selectedContainerId: Int = R.id.nav_host_treino

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configApp()

        selectedContainerId = savedInstanceState?.getInt(SELECTED_CONTAINER_KEY)
            ?: R.id.nav_host_treino

        setupBottomNavigation()
        showTab(selectedContainerId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_CONTAINER_KEY, selectedContainerId)
    }

    private fun configApp() {
        Strada.config.jsonConverter = KotlinXJsonConverter()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.selectedItemId = tabContainerIds.entries
            .first { it.value == selectedContainerId }.key

        bottomNav.setOnItemSelectedListener { item ->
            val containerId = tabContainerIds[item.itemId]
                ?: return@setOnItemSelectedListener false

            showTab(containerId)
            true
        }
    }

    /**
     * Mostra a aba [containerId] e esconde as demais. Como cada aba é um
     * TurboSessionNavHostFragment independente, o FragmentTransaction usa
     * show()/hide() (não replace/add) para preservar a WebView e a pilha de
     * navegação de cada aba entre as trocas.
     */
    private fun showTab(containerId: Int) {
        val transaction = supportFragmentManager.beginTransaction()

        tabContainerIds.values.forEach { id ->
            supportFragmentManager.findFragmentById(id)?.let { fragment ->
                if (id == containerId) transaction.show(fragment) else transaction.hide(fragment)
            }
        }

        transaction.commitNow()

        selectedContainerId = containerId
        // O TurboActivityDelegate aponta para um único NavHostFragment por vez,
        // então precisa ser recriado apontando para a aba que ficou visível.
        delegate = TurboActivityDelegate(this, containerId)
    }

    companion object {
        private const val SELECTED_CONTAINER_KEY = "selected_container_id"
    }
}

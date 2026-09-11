package dev.hotwire.turbo.fitplanandroid.main.tabs

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dev.hotwire.strada.Bridge
import dev.hotwire.turbo.config.TurboPathConfiguration
import dev.hotwire.turbo.fitplanandroid.features.native.NumbersFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebBottomSheetFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebHomeFragment
import dev.hotwire.turbo.fitplanandroid.features.web.WebModalFragment
import dev.hotwire.turbo.fitplanandroid.util.customUserAgent
import dev.hotwire.turbo.session.TurboSessionNavHostFragment
import kotlin.reflect.KClass

/**
 * Classe base compartilhada por cada aba da bottom navigation.
 *
 * Cada aba é uma sessão Turbo independente: possui sua própria WebView e sua
 * própria pilha de navegação. É isso que permite trocar de aba e voltar
 * exatamente de onde o usuário parou, sem perder o histórico.
 *
 * As subclasses só precisam definir um [sessionName] único e uma [startLocation].
 */
@Suppress("unused")
abstract class TabSessionNavHostFragment : TurboSessionNavHostFragment() {

    override val registeredActivities: List<KClass<out AppCompatActivity>>
        get() = listOf()

    override val registeredFragments: List<KClass<out Fragment>>
        get() = listOf(
            WebFragment::class,
            WebHomeFragment::class,
            WebModalFragment::class,
            WebBottomSheetFragment::class,
            NumbersFragment::class
        )

    override val pathConfigurationLocation: TurboPathConfiguration.Location
        get() = TurboPathConfiguration.Location(assetFilePath = "json/configuration.json")

    override fun onSessionCreated() {
        super.onSessionCreated()
        session.webView.settings.userAgentString = session.webView.customUserAgent

        // Cada aba tem sua própria instância de WebView, então o Strada
        // precisa ser (re)inicializado para a sessão de cada aba.
        Bridge.initialize(session.webView)
    }
}

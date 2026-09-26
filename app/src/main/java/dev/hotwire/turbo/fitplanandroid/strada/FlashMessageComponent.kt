package dev.hotwire.turbo.fitplanandroid.strada

import android.util.Log
import androidx.fragment.app.Fragment
import dev.hotwire.core.bridge.BridgeComponent
import dev.hotwire.core.bridge.BridgeDelegate
import dev.hotwire.core.bridge.Message
import dev.hotwire.navigation.destinations.HotwireDestination
import dev.hotwire.turbo.fitplanandroid.main.MainActivity
import kotlinx.serialization.Serializable

class FlashMessageComponent(
    name: String,
    private val bridgeDelegate: BridgeDelegate<HotwireDestination>
) : BridgeComponent<HotwireDestination>(name, bridgeDelegate) {

    private val fragment: Fragment
        get() = bridgeDelegate.destination.fragment

    override fun onReceive(message: Message) {
        if (message.event == "connect") {
            handleConnectEvent(message)
        } else {
            Log.w("TurboNative", "Unknown event for message: $message")
        }
    }

    private fun handleConnectEvent(message: Message) {
        val data = message.data<MessageData>() ?: return
        showSnackBar(data)
    }

    private fun showSnackBar(data: MessageData) {
        (fragment.activity as? MainActivity)?.showMessage(data.title)
    }

    @Serializable
    data class MessageData(
        val title: String
    )
}

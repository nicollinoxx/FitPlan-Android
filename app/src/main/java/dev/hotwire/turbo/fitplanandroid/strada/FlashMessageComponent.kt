package dev.hotwire.turbo.fitplanandroid.strada

import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dev.hotwire.strada.BridgeComponent
import dev.hotwire.strada.BridgeDelegate
import dev.hotwire.strada.Message
import dev.hotwire.turbo.fitplanandroid.base.NavDestination
import kotlinx.serialization.Serializable

class FlashMessageComponent(
    name: String,
    private val bridgeDelegate: BridgeDelegate<NavDestination>
) : BridgeComponent<NavDestination>(name, bridgeDelegate) {

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
        Snackbar.make(fragment.requireView(), data.title, Snackbar.LENGTH_SHORT).show()
    }

    @Serializable
    data class MessageData(
        val title: String
    )
}

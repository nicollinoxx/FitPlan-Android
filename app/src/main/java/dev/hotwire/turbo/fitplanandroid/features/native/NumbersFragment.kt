package dev.hotwire.turbo.fitplanandroid.features.native

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import dev.hotwire.turbo.nav.TurboNavGraphDestination
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@TurboNavGraphDestination(uri = "turbo://fragment/numbers")
class NumbersFragment : NativeFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return setContent(inflater, container) {
            NumbersList()
        }
    }
}

@Composable
fun NumbersList() {
    val numbers = 1..100

    LazyColumn {
        items(numbers.count()) { index ->
            ListItem(headlineContent = { Text("Row ${index + 1}") })
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
fun PreviewNumbersList() {
    NumbersList()
}

package br.com.wheatherApp

import SearchBarComponent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import br.com.wheatherApp.ui.theme.WheaterAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheaterAppTheme {
                Scaffold { paddingValues ->
                    SearchBarComponent(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
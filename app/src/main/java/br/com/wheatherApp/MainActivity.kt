package br.com.wheatherApp

import MainViewModel
import SearchBarComponent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.wheatherApp.ui.theme.WheaterAppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WheaterAppTheme {
                WeatherApp()
            }
        }
    }

    @Composable
    fun WeatherApp(viewModel: MainViewModel = viewModel()) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBarComponent(
                        modifier = Modifier.padding(top = 16.dp),
                        onDetailButtonClick = { cityName ->
                            Toast.makeText(
                                this@MainActivity,
                                "Detalhes do clima para $cityName",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
                Column(modifier = Modifier.fillMaxSize()) {
//                    Text(
//                        text = "$location",
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.onSurface,
//                    )
                }
                // Current Location
                // WeatherCardComponent()
            }
        }
    }
}
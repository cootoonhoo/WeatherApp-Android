import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onDetailButtonClick: (String) -> Unit = {}
) {
    val searchText by viewModel.searchText.collectAsState()
    val cities by viewModel.listOfCities.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }
    val searchBarShape = RoundedCornerShape(12.dp)
    val tooltipState = rememberTooltipState()

    var isCitySelected by remember { mutableStateOf(false) }

    val selectedCity = remember(searchText, cities) {
        cities.find { it.cityName == searchText }
    }
    isCitySelected = selectedCity != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                viewModel.onSearchTextChange(it)
                showSuggestions = true
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar cidade",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            trailingIcon = {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        Text("Ver detalhes do clima")
                    },
                    state = tooltipState
                ) {
                    IconButton(
                        onClick = {
                            if (isCitySelected) {
                                onDetailButtonClick(searchText)
                            }
                        },
                        enabled = isCitySelected
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowCircleRight,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (isCitySelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(searchBarShape)
                .background(
                    color = if (isFocused) {
                        Color.White.copy(alpha = 0.2f)
                    } else {
                        Color.White.copy(alpha = 0.1f)
                    }
                )
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    showSuggestions = it.isFocused && cities.isNotEmpty()
                },
            placeholder = { Text("Buscar cidade") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        AnimatedVisibility(
            visible = showSuggestions && cities.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(350)),
            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(350))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cities) { city ->
                        Text(
                            text = city.cityName,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onSearchTextChange(city.cityName)
                                    viewModel.onCitySelected(city)
                                    showSuggestions = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
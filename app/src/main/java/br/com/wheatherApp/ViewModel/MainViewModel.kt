import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


val MOCK_ListOfCities = listOf(
    City("São Paulo"),
    City("Rio de Janeiro"),
    City("Xique Xique")
)

class MainViewModel: ViewModel()
{
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _listOfCities = MutableStateFlow(MOCK_ListOfCities) // Futuramente vai ser SavedCities
    @OptIn(FlowPreview::class)
    val listOfCities = searchText
        .debounce(  700L)
        .onEach { _isSearching.update { true } }
        .combine(_listOfCities){ text, city ->
        if(text.isBlank())
        {
            city
        } else {
            city.filter {
                it.doesMatchSearchQuery(text)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _listOfCities.value
    )

    fun onSearchTextChange(text : String)
    {
        _searchText.value = text
    }
}

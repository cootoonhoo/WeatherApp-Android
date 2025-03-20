## Claude 3.7 Sonnet
- Prompt 1
Você é um programador em Kotlin e está criando um aplicativo de previsão climática que irá fazer acesso a uma API Externa. Esse aplicativo será feito usando a IDE AndroidStudio. Como você construiria a arquitetura de pastas desse projeto?
- Resposta :

  app/
  ├── src/
  │   ├── main/
  │   │   ├── java/com/example/weatherapp/
  │   │   │   ├── data/
  │   │   │   │   ├── api/
  │   │   │   │   │   ├── WeatherApiService.kt       # Interface Retrofit para chamadas API
  │   │   │   │   │   ├── ApiResponse.kt             # Classes para lidar com respostas da API
  │   │   │   │   │   └── interceptors/             # Interceptors para Retrofit/OkHttp
  │   │   │   │   ├── model/
  │   │   │   │   │   ├── WeatherData.kt            # Modelo de dados meteorológicos
  │   │   │   │   │   ├── Location.kt               # Modelo para localização
  │   │   │   │   │   └── Forecast.kt               # Modelo para previsão
  │   │   │   │   ├── repository/
  │   │   │   │   │   ├── WeatherRepository.kt      # Interface do repositório
  │   │   │   │   │   └── WeatherRepositoryImpl.kt  # Implementação do repositório
  │   │   │   │   └── local/
  │   │   │   │       ├── WeatherDatabase.kt        # Configuração do Room Database
  │   │   │   │       ├── dao/                      # Objetos de Acesso a Dados
  │   │   │   │       └── entity/                   # Entidades para persistência local
  │   │   │   ├── di/
  │   │   │   │   ├── NetworkModule.kt              # Módulo de injeção para componentes de rede
  │   │   │   │   ├── DatabaseModule.kt             # Módulo de injeção para banco de dados
  │   │   │   │   └── RepositoryModule.kt           # Módulo de injeção para repositórios
  │   │   │   ├── domain/
  │   │   │   │   ├── usecase/
  │   │   │   │   │   ├── GetWeatherUseCase.kt      # Caso de uso para obter o clima
  │   │   │   │   │   ├── GetForecastUseCase.kt     # Caso de uso para obter previsão
  │   │   │   │   │   └── UpdateLocationUseCase.kt  # Caso de uso para atualizar localização
  │   │   │   │   └── mapper/
  │   │   │   │       └── WeatherMapper.kt          # Mapeia dados entre camadas
  │   │   │   ├── ui/
  │   │   │   │   ├── MainActivity.kt               # Atividade principal
  │   │   │   │   ├── home/
  │   │   │   │   │   ├── HomeFragment.kt           # Fragment da tela inicial
  │   │   │   │   │   ├── HomeViewModel.kt          # ViewModel da tela inicial
  │   │   │   │   │   └── adapter/                  # Adaptadores para RecyclerViews
  │   │   │   │   ├── forecast/
  │   │   │   │   │   ├── ForecastFragment.kt       # Fragment de previsão detalhada
  │   │   │   │   │   └── ForecastViewModel.kt      # ViewModel da previsão
  │   │   │   │   ├── settings/
  │   │   │   │   │   ├── SettingsFragment.kt       # Fragment de configurações
  │   │   │   │   │   └── SettingsViewModel.kt      # ViewModel das configurações
  │   │   │   │   └── common/
  │   │   │   │       ├── BaseFragment.kt           # Fragment base abstrato
  │   │   │   │       ├── BaseViewModel.kt          # ViewModel base abstrato
  │   │   │   │       └── views/                    # Componentes de UI personalizados
  │   │   │   └── util/
  │   │   │       ├── Constants.kt                  # Constantes utilizadas no app
  │   │   │       ├── Extensions.kt                 # Funções de extensão
  │   │   │       └── LocationUtils.kt              # Utilitários relacionados à localização
  │   │   ├── res/
  │   │   │   ├── layout/                          # Layouts XML
  │   │   │   ├── drawable/                        # Imagens e recursos gráficos
  │   │   │   ├── values/                          # Strings, cores, estilos
  │   │   │   └── navigation/                      # Grafos de navegação
  │   │   └── AndroidManifest.xml
  │   └── test/                                     # Testes unitários
  │       ├── java/com/example/weatherapp/
  │       │   ├── data/
  │       │   │   └── repository/
  │       │   │       └── WeatherRepositoryTest.kt
  │       │   └── domain/
  │       │       └── usecase/
  │       │           └── GetWeatherUseCaseTest.kt
  └── build.gradle   
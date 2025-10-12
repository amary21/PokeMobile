# PokeMobile

PokeMobile is an Android application that allows users to browse and view detailed information about Pokémon. The app provides a user-friendly interface for exploring the Pokémon world, with features like authentication, Pokémon listing, searching, and detailed information viewing.

## Features

- **User Authentication**: Register, login, and profile management
- **Pokémon List**: Browse through a paginated list of Pokémon
- **Search Functionality**: Search for specific Pokémon by name
- **Detailed Information**: View detailed information about each Pokémon including:
  - Name
  - Height
  - Weight
  - Order
  - Base Experience
  - Location Area Encounters
  - Abilities (with name, hidden status, and slot)
- **Offline Support**: Store Pokémon data locally for offline access

## Technologies Used

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit for building native Android UI
- **Coroutines**: For asynchronous programming
- **Retrofit**: For API communication
- **Dependency Injection**: Using custom DI modules
- **Clean Architecture**: Separation of concerns with domain, data, and presentation layers
- **MVVM Pattern**: For the presentation layer
- **Navigation Component**: For handling navigation between screens
- **Material Design 3**: For modern UI components and styling

## Architecture

The application follows Clean Architecture principles with three main layers:

1. **Presentation Layer**: Contains UI components (Screens, ViewModels)
   - Uses MVVM pattern with Jetpack Compose
   - Handles user interactions and displays data

2. **Domain Layer**: Contains business logic and use cases
   - Defines models and repository interfaces
   - Implements use cases for different features

3. **Data Layer**: Handles data operations
   - Repository implementation
   - Remote data source (PokeAPI)
   - Local data source (for caching and user data)

## Project Structure

- **app/src/main/java/com/amary/poke/mobile/**
  - **data/**: Data layer implementation
    - **local/**: Local data source implementation
    - **remote/**: Remote API implementation
    - **repository/**: Repository implementations
  - **di/**: Dependency injection modules
  - **domain/**: Domain layer with business logic
    - **model/**: Domain models
    - **repository/**: Repository interfaces
    - **usecase/**: Use cases for different features
  - **presentation/**: UI components
    - **component/**: Reusable UI components
    - **detail/**: Pokemon detail screen
    - **home/**: Home screen with tabs
    - **list/**: Pokemon list screen
    - **login/**: Login screen
    - **profile/**: User profile screen
    - **register/**: Registration screen
    - **splash/**: Splash screen
    - **theme/**: App theme definitions
  - **route/**: Navigation setup

## Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

## Requirements

- Android Studio Arctic Fox or newer
- Minimum SDK: 21 (Android 5.0 Lollipop)
- Target SDK: 34 (Android 14)

## API

This application uses the [PokeAPI](https://pokeapi.co/) to fetch Pokémon data.

## License

This project is for educational purposes. Pokémon and Pokémon character names are trademarks of Nintendo.

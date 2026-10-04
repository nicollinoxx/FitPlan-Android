# FitPlan Android

Android client for **FitPlan**, a platform for managing personalized diet and workout records.
This app is not a standalone rewrite: it is a [Hotwire Native](https://native.hotwired.dev) shell that
renders the FitPlan Rails application inside a `WebView` and promotes selected parts of the interface to
native Android widgets through bridge components.

> **Web application (backend + frontend):** https://github.com/nicollinoxx/FitPlan
>
> The Android app has no business logic or database of its own. Every screen, route and rule comes from
> the Rails app above, so that repository is required to run this one.

Built as part of an undergraduate final project (TCC).

## Technologies

- Kotlin 2.3.0
- Hotwire Native Android 1.3.1 (`dev.hotwire:core` + `dev.hotwire:navigation-fragments`)
- Material 3 for the shell, Jetpack Compose for native screens
- Android Gradle Plugin 8.9.1 / Gradle 8.13
- minSdk 28, compileSdk / targetSdk 35

## Prerequisites / Dependencies

- JDK 17 — the Gradle daemon picks it on its own through `gradle/gradle-daemon-jvm.properties`
- Android SDK Platform 35
- Android Studio (or the Gradle wrapper from the command line)
- A running instance of [FitPlan](https://github.com/nicollinoxx/FitPlan)

## Setup

1. Start the Rails app from the [FitPlan](https://github.com/nicollinoxx/FitPlan) repository on port `3000`.
2. Point the app at that instance in `app/src/main/java/dev/hotwire/turbo/fitplanandroid/util/Constants.kt`:

   ```kotlin
   private const val DEVELOPMENT_URL = "http://10.0.2.2:3000"  // localhost as seen from the emulator
   private const val PRODUCTION_URL  = "https://fitplan.vip"

   const val BASE_URL = DEVELOPMENT_URL
   ```

   `10.0.2.2` is the host machine's loopback address from inside the Android emulator. On a physical
   device, use the machine's IP on the local network instead.
3. Build and install:

   ```sh
   ./gradlew installDebug
   ```

## How the integration works

### Bottom navigation

`main/MainActivity.kt` declares five tabs — Sheets, Shares, Dashboard, Social and Profile — and each one
gets its own navigator and back stack. The start page of every tab is a real route of the Rails app, listed
in `util/Constants.kt`; adding or moving a tab means changing that list, not writing a screen.

Tapping the tab you are already on sends it back to its start page, and a tab whose page was rendered under
a different Rails session is reset the next time you open it, so signing in or out never leaves a tab showing
the previous user.

### Path configuration

`app/src/main/assets/json/path-configuration.json` maps URL patterns from the Rails app to Android
destinations — which fragment renders a path, whether it opens as a modal, whether it replaces the tab root,
and whether pull-to-refresh is enabled. Adding a route to the Rails app that needs different presentation
means adding a rule here.

Rules are matched against the path **and** the query string, so patterns are anchored to the bare path
(`^/sheets(\.html)?$`) to keep filtered URLs such as `/sheets?type=diet` falling through to
`query_string_presentation: replace` instead of being treated as a new page.

### Bridge components

Each native component has a counterpart Stimulus controller in the Rails app. Both sides must agree on the
component name and on the event names, so **these pairs are the contract between the two repositories**:

| Component name  | Android (`.../strada/`)     | Rails (`app/javascript/controllers/bridge/`) | Behavior                                        |
| --------------- | --------------------------- | -------------------------------------------- | ----------------------------------------------- |
| `form`          | `FormComponent.kt`          | `form_controller.js`                         | Moves a form's submit button into the toolbar    |
| `nav-button`    | `NavButtonComponent.kt`     | `nav_button_controller.js`                   | Renders a web link as a native toolbar button    |
| `flash-message` | `FlashMessageComponent.kt`  | `flash_message_controller.js`                | Shows Rails flash messages as a `Snackbar`       |
| `menu`          | `MenuComponent.kt`          | `menu_controller.js`                         | Shows a group of links as a native bottom sheet  |

Components are registered in `FitPlanApplication.kt` and announced to the server through the user agent,
which is how the Rails side knows which components this client supports. On the Rails side the integration
relies on the `@hotwired/hotwire-native-bridge` importmap pin.

## Notes

- **Theme:** the shell follows the device's light/dark setting rather than the web app's, through a Material 3
  `DayNight` theme and a `values-night/` variant. The toolbar and tab icons are tinted from theme attributes
  so they stay legible in both.
- **Social sign in:** Google and Facebook are hidden inside the app. The provider flow opens in a Custom Tab,
  whose cookie jar is separate from the `WebView`'s, so the session it establishes never reaches the app.
  Turning it back on means adopting the native SDKs.
- **Release builds:** there is no signing configuration yet, and `versionCode` / `versionName` are still at
  their initial values.

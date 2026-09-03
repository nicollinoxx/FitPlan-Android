# FitPlan Android

Android client for **FitPlan**, a platform for managing personalized diet and workout records.
This app is not a standalone rewrite: it is a [Turbo Native](https://github.com/hotwired/turbo-android)
shell that renders the FitPlan Rails application inside a `WebView` and promotes selected parts of the
interface to native Android widgets through [Strada](https://github.com/hotwired/strada-android) bridge
components.

> **Web application (backend + frontend):** https://github.com/nicollinoxx/FitPlan
>
> The Android app has no business logic or database of its own. Every screen, route and rule comes from
> the Rails app above, so that repository is required to run this one.

Built as part of an undergraduate final project (TCC).

## Technologies

- Kotlin 2.1.20
- Turbo Android 7.0.0 + Strada 1.0.0-beta2
- Jetpack Compose (Material 3) for native screens
- Android Gradle Plugin 8.9.1 / Gradle 8.13
- minSdk 24, compileSdk / targetSdk 35

## Prerequisites / Dependencies

- JDK 17
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

### Path configuration

`app/src/main/assets/json/configuration.json` maps URL patterns from the Rails app to Android
destinations — which fragment renders a path, whether it opens as a modal or a bottom sheet, and whether
pull-to-refresh is enabled. Adding a route to the Rails app that needs different presentation means adding
a rule here.

### Bridge components

Each native component has a counterpart Stimulus controller in the Rails app. Both sides must agree on the
component name and on the event names, so **these pairs are the contract between the two repositories**:

| Component name  | Android (`.../strada/`)     | Rails (`app/javascript/controllers/bridge/`) | Behavior                                        |
| --------------- | --------------------------- | -------------------------------------------- | ----------------------------------------------- |
| `form`          | `FormComponent.kt`          | `form_controller.js`                         | Moves a form's submit button into the toolbar    |
| `nav-button`    | `NavButtonComponent.kt`     | `nav_button_controller.js`                   | Renders a web link as a native toolbar button    |
| `flash-message` | `FlashMessageComponent.kt`  | `flash_message_controller.js`                | Shows Rails flash messages as a `Snackbar`       |
| `menu`          | `MenuComponent.kt`          | `menu_controller.js`                         | Shows a group of links as a native bottom sheet  |

Components are registered in `strada/BridgeComponentFactories.kt` and announced to the server through the
user agent (`util/Extension.kt`), which is how the Rails side knows which components this client supports.
On the Rails side the integration relies on the `strada-rails` gem and the `@hotwired/strada` importmap pin.

## Notes

- **Edge-to-edge:** Android 15 enforces edge-to-edge for `targetSdk 35` apps, but the layouts that Turbo
  Android 7 inflates internally do not apply window insets, which pushes the toolbar under the status bar.
  `res/values-v35/themes.xml` opts out via `android:windowOptOutEdgeToEdgeEnforcement`. This opt-out is only
  honored up to `targetSdk 35`, so moving to 36 requires handling insets — most likely as part of migrating
  to [Hotwire Native](https://native.hotwired.dev), whose layouts are inset-aware.
- **Turbo and Strada are frozen:** `dev.hotwire:turbo:7.0.0` and `dev.hotwire:strada:1.0.0-beta2` are no
  longer maintained; they were superseded by Hotwire Native. Migrating means changing both repositories
  together, since the bridge protocol and the path configuration URI scheme both change.

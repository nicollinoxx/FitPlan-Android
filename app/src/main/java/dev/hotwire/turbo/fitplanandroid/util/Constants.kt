package dev.hotwire.turbo.fitplanandroid.util

import dev.hotwire.turbo.fitplanandroid.BuildConfig

// Set per build type in app/build.gradle.kts, so a release can never ship
// pointing at a development machine: debug builds talk to the emulator's view of
// localhost, release builds to the real site.
const val BASE_URL    = BuildConfig.BASE_URL
const val SIGN_IN_URL = "$BASE_URL/sign_in"

// Cookie the Rails app stores the signed-in session under (see SessionsController).
const val SESSION_COOKIE = "session_token"

// Cookie the Rails app reports the language it is rendering in (see ApplicationController).
const val LOCALE_COOKIE = "locale"

// Start location of each bottom navigation tab. Every path below is a real
// top-level route of the FitPlan Rails app (see its config/routes.rb).
const val SHEETS_URL    = "$BASE_URL/sheets"
const val SHARES_URL    = "$BASE_URL/sheets/shares"
const val DASHBOARD_URL = "$BASE_URL/dashboard"
const val SOCIAL_URL    = "$BASE_URL/social/profiles"
const val PROFILE_URL   = "$BASE_URL/identity/profile"

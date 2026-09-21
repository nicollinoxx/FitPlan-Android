package dev.hotwire.turbo.fitplanandroid.util

private const val DEVELOPMENT_URL = "http://10.0.2.2:3000"
private const val PRODUCTION_URL  = "https://fitplan.vip"

const val BASE_URL    = DEVELOPMENT_URL
const val SIGN_IN_URL = "$BASE_URL/sign_in"

// Cookie the Rails app stores the signed-in session under (see SessionsController).
const val SESSION_COOKIE = "session_token"

// Start location of each bottom navigation tab. Every path below is a real
// top-level route of the FitPlan Rails app (see its config/routes.rb).
const val WORKOUTS_URL  = "$BASE_URL/"
const val DASHBOARD_URL = "$BASE_URL/dashboard"
const val SOCIAL_URL    = "$BASE_URL/social/profiles"
const val PROFILE_URL   = "$BASE_URL/identity/profile"

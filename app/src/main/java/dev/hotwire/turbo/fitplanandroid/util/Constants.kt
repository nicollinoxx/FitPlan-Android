package dev.hotwire.turbo.fitplanandroid.util

private const val DEVELOPMENT_URL = "http://10.0.2.2:3000"
private const val PRODUCTION_URL  = "https://fitplan.vip"

const val BASE_URL = DEVELOPMENT_URL
const val CURRENT_URL = "$BASE_URL/"
const val SIGN_IN_URL = "$BASE_URL/sign_in"

// Rota inicial de cada aba da bottom navigation.
// TODO: ajuste esses paths para as rotas reais do app Rails (FitPlan), se forem diferentes.
const val TREINO_URL = "$BASE_URL/treinos"
const val DIETA_URL  = "$BASE_URL/dieta"
const val SOCIAL_URL = "$BASE_URL/social"
const val PERFIL_URL = "$BASE_URL/perfil"
const val MENU_URL   = "$BASE_URL/menu"

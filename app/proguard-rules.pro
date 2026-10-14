# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Compile-time annotations that reach Android through transitive dependencies.
# They point at javax.lang.model, which is part of the JDK and absent from the
# platform, so R8 stops on the dangling reference. Nothing calls them at runtime.
-dontwarn javax.lang.model.element.**
-dontwarn com.google.errorprone.annotations.**

# Hotwire builds its navigation graph by asking each destination class what it
# descends from, so the fragment and navigation hierarchies have to survive
# intact. R8 merges classes it believes are unused as separate types, which
# rewrites those superclass links and leaves the graph unbuildable.
-keep class androidx.fragment.app.** { *; }
-keep class androidx.navigation.** { *; }

# The app's own destinations go through the same graph builder, so they need the
# same treatment. The savings come from dropping unused library code, not from
# renaming the handful of classes written here.
-keep class dev.hotwire.turbo.fitplanandroid.** { *; }

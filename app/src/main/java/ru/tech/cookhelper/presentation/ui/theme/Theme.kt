package ru.tech.cookhelper.presentation.ui.theme

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.tech.cookhelper.presentation.settings.components.ColorScheme.*
import ru.tech.cookhelper.presentation.settings.components.NightMode
import ru.tech.cookhelper.presentation.ui.utils.compose.ColorUtils.createInverseSecondaryColor
import ru.tech.cookhelper.presentation.ui.utils.provider.LocalSettingsProvider
import androidx.compose.material3.ColorScheme as Material3ColorScheme

private val ColorScheme.LightThemeColors: Material3ColorScheme
    get() = lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
    )
private val ColorScheme.DarkThemeColors: Material3ColorScheme
    get() = darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
    )

@Composable
fun ProKitchenTheme(
    dynamicColor: Boolean = LocalSettingsProvider.current.dynamicColors,
    content: @Composable () -> Unit
) {
    val darkTheme = when (LocalSettingsProvider.current.nightMode) {
        NightMode.SYSTEM -> isSystemInDarkTheme()
        NightMode.DARK -> true
        NightMode.LIGHT -> false
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getColorScheme(darkTheme)
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !darkTheme
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            val color = TopAppBarDefaults.smallTopAppBarColors().containerColor(
                colorTransitionFraction = 1f
            ).value.createInverseSecondaryColor()

            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
                systemUiController.setNavigationBarColor(
                    color = if (landscape) color else Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }
            content()
        }
    )
}


@Composable
fun getColorScheme(darkTheme: Boolean): Material3ColorScheme {
    when (LocalSettingsProvider.current.colorScheme) {
        BLUE -> ColorScheme.Blue
        VIOLET -> ColorScheme.Violet
        GREEN -> ColorScheme.Green
        YELLOW -> ColorScheme.Yellow
        RED -> ColorScheme.Red
        PINK -> ColorScheme.Pink
        ORANGE -> ColorScheme.Orange
        MINT -> ColorScheme.Mint
    }.apply {
        return if (darkTheme) DarkThemeColors
        else LightThemeColors
    }
}
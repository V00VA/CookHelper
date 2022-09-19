package ru.tech.cookhelper.presentation.ui.utils.compose

import androidx.compose.runtime.*

object StateUtils {
    @Composable
    fun <T> computedStateOf(
        vararg keys: Any,
        calculation: () -> T
    ): State<T> = remember(keys) { derivedStateOf(calculation) }

    @Composable
    fun <T> computedStateOf(
        calculation: () -> T
    ): State<T> = remember { derivedStateOf(calculation) }

    inline fun <T> MutableState<T>.update(
        transform: T.() -> T
    ) = apply { setValue(value = transform(this.value)) }

    fun <T> MutableState<T>.setValue(value: T) {
        this.value = value
    }
}
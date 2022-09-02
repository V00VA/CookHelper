package ru.tech.cookhelper.presentation.registration_screen

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import ru.tech.cookhelper.R
import ru.tech.cookhelper.presentation.app.components.Loading
import ru.tech.cookhelper.presentation.app.components.StrokeTextField
import ru.tech.cookhelper.presentation.app.components.sendToast
import ru.tech.cookhelper.presentation.registration_screen.viewModel.RegistrationViewModel
import ru.tech.cookhelper.presentation.ui.utils.compose.StateUtils.computedStateOf
import ru.tech.cookhelper.presentation.ui.utils.event.Event
import ru.tech.cookhelper.presentation.ui.utils.event.collectWithLifecycle
import ru.tech.cookhelper.presentation.ui.utils.navigation.Screen
import ru.tech.cookhelper.presentation.ui.utils.provider.LocalToastHost
import ru.tech.cookhelper.presentation.ui.utils.provider.goBack

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun RegistrationField(
    scaleModifier: Float,
    authController: NavController<Screen>,
    onGetCredentials: (name: String, email: String, token: String) -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {

    val toastHost = LocalToastHost.current
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var nick by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordRepeat by rememberSaveable { mutableStateOf("") }

    var isPasswordVisible by remember {
        mutableStateOf(false)
    }
    val isPasswordValid by computedStateOf {
        password.length >= 8 && password.toCharArray().any { it.isDigit() }
    }

    val isFormValid by computedStateOf {
        name.isNotEmpty() && surname.isNotEmpty() && nick.isNotEmpty() && password.isNotEmpty() && email.isValid() && passwordRepeat == password && viewModel.checkLoginState.error.isEmpty() && isPasswordValid
    }

    val focusManager = LocalFocusManager.current

    BackHandler { authController.goBack() }

    Text(
        stringResource(R.string.register),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.size(8.dp * scaleModifier))
    Text(
        stringResource(R.string.create_your_new_account),
        style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center
    )
    Spacer(Modifier.size(32.dp * scaleModifier))
    AnimatedContent(viewModel.registrationState.isLoading) { isLoading ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) Loading(Modifier.fillMaxWidth())
            else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.trim() },
                    label = { Text(stringResource(R.string.name)) },
                    singleLine = true,
                    isError = name.isEmpty(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.width(TextFieldDefaults.MinWidth),
                    trailingIcon = {
                        if (name.isNotBlank())
                            IconButton(onClick = { name = "" }) {
                                Icon(Icons.Filled.Clear, null)
                            }
                    }
                )
                Spacer(Modifier.size(8.dp * scaleModifier))
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it.trim() },
                    label = { Text(stringResource(R.string.surname)) },
                    singleLine = true,
                    isError = surname.isEmpty(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.width(TextFieldDefaults.MinWidth),
                    trailingIcon = {
                        if (surname.isNotBlank())
                            IconButton(onClick = { surname = "" }) {
                                Icon(Icons.Filled.Clear, null)
                            }
                    }
                )
                Spacer(Modifier.size(8.dp * scaleModifier))
                StrokeTextField(
                    value = nick,
                    loading = viewModel.checkLoginState.isLoading,
                    onValueChange = { nick = it; viewModel.checkLoginForAvailability(it) },
                    label = { Text(stringResource(R.string.nick)) },
                    singleLine = true,
                    isError = nick.isEmpty() || viewModel.checkLoginState.error.isNotEmpty(),
                    error = {
                        if (nick.isNotEmpty()) Text(
                            stringResource(R.string.nickname_rejected),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.width(TextFieldDefaults.MinWidth),
                    trailingIcon = {
                        if (nick.isNotBlank()) {
                            IconButton(onClick = { nick = "" }) {
                                Icon(Icons.Filled.Clear, null)
                            }
                        }
                    }
                )
                AnimatedVisibility(name.isNotEmpty() && surname.isNotEmpty() && nick.isNotEmpty()) {
                    Column {
                        Spacer(Modifier.size(8.dp * scaleModifier))
                        StrokeTextField(
                            value = email,
                            loading = viewModel.checkEmailState.isLoading,
                            onValueChange = {
                                email =
                                    it; if (email.isValid()) viewModel.checkEmailForAvailability(it)
                            },
                            label = { Text(stringResource(R.string.email)) },
                            singleLine = true,
                            isError = email.isEmpty() || email.isNotValid() || viewModel.checkEmailState.error.isNotEmpty(),
                            error = {
                                if (email.isNotEmpty() && email.isValid()) Text(
                                    stringResource(
                                        R.string.email_rejected
                                    ), color = MaterialTheme.colorScheme.error, fontSize = 12.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.width(TextFieldDefaults.MinWidth),
                            trailingIcon = {
                                if (email.isNotBlank()) {
                                    IconButton(onClick = { email = "" }) {
                                        Icon(Icons.Filled.Clear, null)
                                    }
                                }
                            }
                        )
                        Spacer(Modifier.size(8.dp))
                        StrokeTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            singleLine = true,
                            isError = !isPasswordValid,
                            error = {
                                if (password.isNotEmpty()) {
                                    Text(
                                        stringResource(if (password.length < 8) R.string.password_too_short else R.string.password_must_contain_one_number),
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.width(TextFieldDefaults.MinWidth),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    isPasswordVisible = !isPasswordVisible
                                }) {
                                    Icon(
                                        if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        null
                                    )
                                }
                            }
                        )
                        Spacer(Modifier.size(8.dp * scaleModifier))
                        OutlinedTextField(
                            value = passwordRepeat,
                            onValueChange = { passwordRepeat = it },
                            label = { Text(stringResource(R.string.repeat_password)) },
                            singleLine = true,
                            isError = passwordRepeat.isEmpty() || passwordRepeat != password,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.width(TextFieldDefaults.MinWidth),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                if (isFormValid) viewModel.registerWith(
                                    name.capitalize(),
                                    surname.capitalize(),
                                    nick, email, password
                                )
                            }),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    isPasswordVisible = !isPasswordVisible
                                }) {
                                    Icon(
                                        if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        null
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    Spacer(Modifier.size(32.dp * scaleModifier))
    Button(
        enabled = if (!viewModel.registrationState.isLoading) isFormValid else false,
        onClick = {
            viewModel.registerWith(
                name.capitalize(),
                surname.capitalize(),
                nick, email, password
            )
        },
        modifier = Modifier.defaultMinSize(
            minWidth = TextFieldDefaults.MinWidth
        ), content = { Text(stringResource(R.string.sign_up)) }
    )
    Spacer(Modifier.size(64.dp * scaleModifier))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(Modifier.size(12.dp))
        TextButton(
            onClick = { authController.navigate(Screen.Authentication.Login) },
            content = { Text(stringResource(R.string.log_in_have_acc)) })
    }
    Spacer(Modifier.size(16.dp * scaleModifier))

    viewModel.eventFlow.collectWithLifecycle {
        when (it) {
            is Event.ShowToast -> toastHost.sendToast(
                Icons.Rounded.ErrorOutline,
                it.text.asString(context)
            )
            is Event.SendData -> {
                onGetCredentials(it["name"], it["email"], it["token"])
            }
            is Event.NavigateTo -> {
                authController.navigate(it.screen)
            }
            else -> {}
        }
    }
}

fun String.capitalize() = lowercase().replaceFirstChar { it.titlecase() }
fun String.isValid(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isNotValid() = !isValid()
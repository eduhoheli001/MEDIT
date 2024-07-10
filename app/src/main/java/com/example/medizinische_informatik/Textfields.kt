package com.example.medizinische_informatik

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun PasswordTextField(
    text: String,
    modifier: Modifier = Modifier,
    labelText: String = stringResource(id = R.string.label_password),
    validateStrengthPassword: Boolean = false,
    hasError: Boolean = false,
    onHasStrongPassword: (isStrong: Boolean) -> Unit = {},
    onTextChanged: (text: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val showPassword = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier,
            value = text,
            label = { Text(labelText) },
            onValueChange = onTextChanged,
            placeholder = {
                Text(
                    text = labelText,
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            isError = hasError,
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (icon, iconColor) = if (showPassword.value) {
                    Pair(Icons.Filled.Visibility, Color.Gray)
                } else {
                    Pair(Icons.Filled.VisibilityOff, Color.Gray)
                }
                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(icon, contentDescription = "Visibility", tint = iconColor)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (validateStrengthPassword && text.isNotEmpty()) {
            val strengthPasswordType = strengthChecker(text)
            if (strengthPasswordType == StrengthPasswordTypes.STRONG) {
                onHasStrongPassword(true)
            } else {
                onHasStrongPassword(false)
            }
            Text(
                modifier = Modifier
                    .semantics { contentDescription = "StrengthPasswordMessage" }
                    .align(Alignment.Start),
                text = if (strengthPasswordType == StrengthPasswordTypes.STRONG) {
                    stringResource(id = R.string.password_strong)
                } else {
                    stringResource(id = R.string.password_weak)
                },
                fontSize = 12.sp,
                color = if (strengthPasswordType == StrengthPasswordTypes.STRONG) Color.Green else colorResource(id = R.color.colorSoftRed300)
            )
        }
    }
}

@Composable
fun ConfirmPasswordTextField(
    text: String,
    confirmText: String,
    modifier: Modifier = Modifier,
    labelText: String = stringResource(id = R.string.label_confirm_password),
    hasError: Boolean = false,
    onTextChanged: (text: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val showPassword = remember { mutableStateOf(false) }
    val matchError = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier,
            value = text,
            onValueChange = onTextChanged,
            label = { Text(labelText) },
            placeholder = {
                Text(
                    text = labelText,
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            isError = hasError || matchError.value,
            visualTransformation =
            if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (icon, iconColor) = if (showPassword.value) {
                    Pair(Icons.Filled.Visibility, Color.Gray)
                }
                else {
                    Pair(Icons.Filled.VisibilityOff, Color.Gray)
                }
                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(icon, contentDescription = "Visibility", tint = iconColor)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (confirmText != text) {
            Text(
                text = stringResource(id = R.string.error_password_no_match),
                color = colorResource(id = R.color.colorSoftRed300),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { contentDescription = "ConfirmPasswordMessage" },
            )
            matchError.value = true
        } else {
            matchError.value = false
        }
    }
}

private fun strengthChecker(password: String): StrengthPasswordTypes =
    when {
        REGEX_STRONG_PASSWORD.toRegex().containsMatchIn(password) -> StrengthPasswordTypes.STRONG
        else -> StrengthPasswordTypes.WEAK
    }

enum class StrengthPasswordTypes {
    STRONG,
    WEAK
}
private const val REGEX_STRONG_PASSWORD = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{8,})"

@Composable
fun ValidationTextField(
    value: String,
    mode: TextFieldMode = TextFieldMode.NONE,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.label_text_input),
    placeholder: String = stringResource(id = R.string.placeholder_enter_text),
) {
    val focusManager = LocalFocusManager.current
    val isError = mode != TextFieldMode.NONE && value.isNotEmpty() && !isValidText(value, mode)

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        isError = isError,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.DarkGray,
                fontSize = 16.sp,
            )
        },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
    )
}

//hier können neue Regex hinzugefügt werden
enum class TextFieldMode {
    NONE,
    MAIL,
    USERNAME,
    GENERIC,
}

fun isValidText(text: String, mode: TextFieldMode): Boolean {
    return when (mode) {
        TextFieldMode.MAIL -> text.matches(Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"))
        TextFieldMode.USERNAME -> text.matches(Regex("[a-zA-Z0-9]+"))
        TextFieldMode.GENERIC -> text.matches(Regex("[a-zA-Z]+"))
        TextFieldMode.NONE -> true
    }
}

@Composable
fun LabelledCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String = stringResource(id = R.string.checkbox_label),
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckedChange(!checked) }
            )
            .requiredHeight(ButtonDefaults.MinHeight)
            .padding(4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Spacer(Modifier.size(6.dp))

        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun TripleRadioButton(
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    label: String = stringResource(id = R.string.radio_button_label),
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = label,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                stringResource(id = R.string.radio_button_option_yes),
                stringResource(id = R.string.radio_button_option_no),
                stringResource(id = R.string.radio_button_option_maybe)
            ).forEachIndexed { index, text ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedOption == index,
                        onClick = { onOptionSelected(index) }
                    )
                    Text(text = text)
                }
            }
        }
    }
}

@Composable
fun TextFieldAddButton(
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(id = R.string.text_field_add_placeholder),
    textFieldValue: String,
    listItems: SnapshotStateList<String>,
    onChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = modifier,
            value = textFieldValue,
            onValueChange = onChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                )
            },
            label = { Text(placeholder) },
            trailingIcon = {
                val (icon, iconColor) = Pair(Icons.Filled.AddCircleOutline, Color.Gray)
                IconButton(onClick = {
                    if (textFieldValue.isNotBlank()) {
                        listItems.add(textFieldValue)
                        onChange("")
                    }
                }) {
                    Icon(icon, contentDescription = "Add", tint = iconColor)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = modifier,
        ) {
            listItems.forEachIndexed { index, item ->
                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { listItems.removeAt(index) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete"
                        )
                    }
                    Text(
                        text = item,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
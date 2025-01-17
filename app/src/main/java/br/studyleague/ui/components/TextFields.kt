package br.studyleague.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DefaultPasswordField(password: String, onValueChange: (String) -> Unit) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    DefaultOutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                val icon =
                    if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                Icon(icon, contentDescription = null)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color(0xFFEFEFEF),
            focusedContainerColor = Color(0xFFEFEFEF)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DefaultFormInput(
    label: String, placeholder: String, value: String, onValueChange: (String) -> Unit
) {
    DefaultFormInput(label = label) {
        DefaultOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFEFEFEF),
                focusedContainerColor = Color(0xFFEFEFEF)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DefaultFormInput(label: String, textField: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            label, fontSize = 18.sp, modifier = Modifier.padding(start = 7.dp)
        )

        textField()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(15.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black
    ),
    contentPadding: PaddingValues = PaddingValues(20.dp),
    focusedBorderThickness: Dp = 1.dp,
    unfocusedBorderThickness: Dp = 2.dp,
) {
    BasicTextField(value = value,
        modifier = modifier.defaultMinSize(
            minWidth = OutlinedTextFieldDefaults.MinWidth,
            minHeight = OutlinedTextFieldDefaults.MinHeight
        ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = contentPadding,
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        enabled,
                        isError,
                        interactionSource,
                        colors,
                        shape,
                        focusedBorderThickness,
                        unfocusedBorderThickness
                    )
                })
        })
}


@Composable
fun NumberText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Light,
        textAlign = TextAlign.Center,
        color = Color.Black
    ),
) {
    Text(
        text = text, style = textStyle, modifier = modifier
    )
}

@Composable
fun NumberTextField(
    modifier: Modifier = Modifier,
    value: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(
        fontSize = 16.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.Center
    ),
) {
    var auxValue by remember { mutableStateOf(value) }

    Box(modifier = modifier) {
        BasicTextField(
            value = auxValue,
            onValueChange = {
                auxValue = it

                if (it.toIntOrNull() != null) {
                    onValueChange(it)
                }
            },
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            readOnly = readOnly,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(15.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(20.dp)
) {
    BasicTextField(value = value,
        modifier = modifier.defaultMinSize(
            minWidth = TextFieldDefaults.MinWidth, minHeight = TextFieldDefaults.MinHeight
        ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = contentPadding,
                container = {
                    TextFieldDefaults.ContainerBox(
                        enabled, isError, interactionSource, colors, shape
                    )
                })
        })
}

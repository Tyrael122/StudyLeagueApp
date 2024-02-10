package br.studyleague.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDropdownMenu(
    modifier: Modifier = Modifier,

    // Can change later to a List<Pair<Long, String>>.
    // You could provide another function, onValueChanged, that would return the id of the selected item, when there was a match in the list.
    // Provide a parameter called selectedValue too.
    // Then, you wouldn't need to override the selectedOptionText. If you still did, you would need to override onSelectionChanged too.

    options: List<String>,
    selectedOptionText: String,
    onSelectionChanged: (String) -> Unit,
    isSearchable: Boolean = true,
    textFieldColors: TextFieldColors = ExposedDropdownMenuDefaults.textFieldColors(
        unfocusedContainerColor = Color(0xFFEEEEEE),
        focusedContainerColor = Color(0xFFEEEEEE),
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
    ),
    placeholder: @Composable (() -> Unit)? = null,
    textFieldShape: Shape = RoundedCornerShape(10.dp),
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        val filteredOptions = if (isSearchable) {
            options.filter { it.contains(selectedOptionText, ignoreCase = true) }
        } else {
            options
        }

        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .focusRequester(focusRequester)
                .onFocusChanged {
                    expanded = it.isFocused

                    if (!filteredOptions.contains(selectedOptionText)) {
                        onSelectionChanged("")
                    }
                },
            value = selectedOptionText,
            readOnly = !isSearchable,
            onValueChange = onSelectionChanged,
            placeholder = placeholder,
            shape = textFieldShape,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = textFieldColors,
        )

        if (filteredOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .exposedDropdownSize(true)
                    .background(Color.White),
                properties = createPopUpProperties(isSearchable),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                filteredOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            expanded = false
                            focusManager.clearFocus()
                            onSelectionChanged(selectionOption)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

fun createPopUpProperties(isSearchable: Boolean): PopupProperties {
    return if (isSearchable) {
        PopupProperties(
            focusable = false,
            dismissOnClickOutside = false // This is false because the loss of focus is handled by the TextField. (See onFocusChanged)
        )
    } else {
        PopupProperties(
            focusable = true, dismissOnClickOutside = true, dismissOnBackPress = true
        )
    }
}
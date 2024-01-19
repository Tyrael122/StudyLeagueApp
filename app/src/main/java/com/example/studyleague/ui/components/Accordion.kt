package com.example.studyleague.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Accordion(
    modifier: Modifier = Modifier,
    title: String,
    body: @Composable () -> Unit,
    initialExpandedState: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initialExpandedState) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 10.dp)
    ) {
        AccordionHeader(
            title = title,
            isExpanded = isExpanded,
            onExpandedChange = { isExpanded = it })

        AnimatedVisibility(visible = isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Divider(
                    color = Color(0x40000000), modifier = Modifier.fillMaxWidth()
                )

                body()
            }
        }
    }
}

@Composable
private fun AccordionHeader(
    title: String, isExpanded: Boolean, onExpandedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Light)

        IconButton(onClick = { onExpandedChange(!isExpanded) }) {
            Icon(
                if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0x80000000),
            )
        }
    }
}

object AccordionBody {
    @Composable
    fun TextRow(items: List<List<String>>) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)

            items.forEach {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it[0], style = textStyle)

                    Text(it[1], style = textStyle)
                }
            }
        }
    }

    @Composable
    fun TextFieldRow(items: List<List<String>>, onValueChange: (String) -> Unit) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)

            items.forEach {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it[0], style = textStyle)

                    BasicTextField(
                        value = it[1],
                        onValueChange = onValueChange,
                        textStyle = textStyle.copy(textAlign = TextAlign.Center),
                        modifier = Modifier
                            .width(64.dp)
                            .background(Color(0xFFF1F3F4), RoundedCornerShape(5.dp))
                            .padding(vertical = 7.dp)
                    )
                }
            }
        }
    }
}

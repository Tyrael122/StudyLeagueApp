package br.studyleague.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    onNavigationIconClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = title, navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }, modifier = modifier
    )
}

object TopBarTitleHelper {
    fun buildTextComposable(
        title: String, textStyle: TextStyle = TopBarTitleStyles.big()
    ): @Composable () -> Unit {
        return {
            Text(
                text = title,
                style = textStyle,
            )
        }
    }
}

object TopBarTitleStyles {
    fun big(): TextStyle {
        return TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp)
    }

    fun medium(): TextStyle {
        return TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp)
    }
}

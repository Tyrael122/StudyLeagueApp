package br.studyleague.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    items: List<NavigationItem>,
    currentRoute: String,
    onLogout: () -> Unit,
    closeDrawer: () -> Unit,
    isCompactMode: Boolean = true,
    userInfoTitle: String,
    userInfoSubtitle: String,
) {
    ModalDrawerSheet(
        drawerShape = RectangleShape,
        drawerContainerColor = Color(0xFFFEFBFF),
        modifier = modifier.widthIn(max = 300.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()
        ) {
            Column {
                if (!isCompactMode) {
                    UserInfo(
                        title = userInfoTitle,
                        subtitle = userInfoSubtitle,
                        modifier = Modifier.padding(top = 20.dp, bottom = 45.dp)
                    )
                }

                items.forEach { item ->
                    NavigationDrawerItem(label = {
                        Text(
                            text = item.label,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                        icon = item.icon,
                        selected = item.route == currentRoute,
                        onClick = {
                            closeDrawer()
                            item.onClick()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFEEEEEE),
                            unselectedContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp)
                    )
                }
            }

            if (!isCompactMode) {
                LogoutButton(
                    onClick = onLogout,
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun UserInfo(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = title, style = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
            ), modifier = Modifier.padding(top = 10.dp)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ), modifier = modifier
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.ExitToApp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(30.dp)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = "Logout",
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

data class NavigationItem(
    val label: String,
    val route: String,
    val icon: @Composable () -> Unit = {},
    val onClick: () -> Unit
)

class NavigationItemBuilder(private val navController: NavHostController) {
    private val navItems: MutableList<NavigationItem> = mutableListOf()

    fun addNavigationItem(
        label: String, route: String, imageVector: ImageVector
    ): NavigationItem {
        val navItem = NavigationItem(label = label,
            route = route,
            onClick = { navController.navigate(route) },
            icon = {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(30.dp)
                )
            })

        navItems.add(navItem)

        return navItem
    }

    fun build(): List<NavigationItem> {
        return navItems
    }
}
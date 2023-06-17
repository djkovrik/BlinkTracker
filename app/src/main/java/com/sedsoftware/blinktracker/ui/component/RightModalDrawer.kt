package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.R.string
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun RightModalDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            modifier = modifier,
            drawerContent = {
                SideDrawerContent()
            },
            drawerState = drawerState,
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                content()
            }
        }
    }
}

@Composable
private fun SideDrawerContent() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        ModalDrawerSheet(
            drawerShape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp)
            ) {
                Text(
                    text = stringResource(id = string.info_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Text(
                    text = stringResource(id = string.info1),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(id = string.info2),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSideDrawerContentLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            SideDrawerContent()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSideDrawerContentDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            SideDrawerContent()
        }
    }
}

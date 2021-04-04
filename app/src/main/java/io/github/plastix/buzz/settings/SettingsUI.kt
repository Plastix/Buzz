package io.github.plastix.buzz.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.plastix.buzz.BuildConfig
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.BuzzTheme
import io.github.plastix.buzz.util.CustomDialog

@Composable
fun SettingsUi(
    onBack: () -> Unit
) {
    BuzzTheme {
        // Because I'm too lazy to move this state out of the UI layer
        var showInfoDialog = remember { mutableStateOf(false) }
        TopAppBar(
            title = {
                Text(stringResource(R.string.settings_title))
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {
                IconButton(onClick = { showInfoDialog.value = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Help,
                        contentDescription = stringResource(R.string.puzzle_detail_toolbar_info)
                    )
                }
            }
        )
        if (showInfoDialog.value) {
            CustomDialog(
                onDismiss = { showInfoDialog.value = false },
                title = stringResource(R.string.about_dialog_title)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.about_dialog_body),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("v ${BuildConfig.VERSION_NAME}")
                    Text("(${BuildConfig.VERSION_CODE})")
                }
            }
        }
    }
}
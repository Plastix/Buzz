package io.github.plastix.buzz.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.plastix.buzz.BuildConfig
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.BuzzTheme
import io.github.plastix.buzz.util.CustomDialog
import io.github.plastix.buzz.util.noRippleClickable

@Composable
fun SettingsUi(
    onBack: () -> Unit,
    onGiveFeedback: () -> Unit,
    devMenuToggled: () -> Unit
) {
    BuzzTheme {
        // Because I'm too lazy to move this state out of the UI layer
        val showInfoDialog = rememberSaveable { mutableStateOf(false) }
        SmallTopAppBar(
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
            InfoDialog(
                onDismiss = { showInfoDialog.value = false },
                onGiveFeedback = onGiveFeedback,
                devMenuToggled = devMenuToggled
            )
        }
    }
}

@Composable
fun InfoDialog(
    onDismiss: () -> Unit,
    onGiveFeedback: () -> Unit,
    devMenuToggled: () -> Unit
) {
    CustomDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.about_dialog_title)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.about_dialog_body),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = R.drawable.ic_bee_black),
                contentDescription = stringResource(R.string.puzzle_list_empty_icon),
            )
            Spacer(Modifier.height(16.dp))
            val clickCounter = remember { mutableStateOf(0) }
            Text(text = "v ${BuildConfig.VERSION_NAME}",
                modifier = Modifier.noRippleClickable {
                    if (clickCounter.value >= 4) {
                        devMenuToggled.invoke()
                        clickCounter.value = 0
                    } else {
                        clickCounter.value++
                    }
                }
            )
            Text(stringResource(R.string.about_dialog_build_number, BuildConfig.VERSION_CODE))
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onGiveFeedback,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Text(stringResource(R.string.about_dialog_give_feedback))
            }
        }
    }
}

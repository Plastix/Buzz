package io.github.plastix.buzz.settings

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.BuzzTheme

@Composable
fun SettingsUi(
    onBack: () -> Unit
) {
    BuzzTheme {
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
        )
    }
}
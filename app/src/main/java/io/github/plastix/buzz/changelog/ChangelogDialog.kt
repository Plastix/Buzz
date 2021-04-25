package io.github.plastix.buzz.changelog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.plastix.buzz.util.CustomDialog

@Composable
fun ChangeLogDialog(onDismiss: () -> Unit) {
    CustomDialog(onDismiss = onDismiss, title = "Release Notes", isScrolling = false) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(CHANGELOG, key = { changes -> changes.versionCode }) { item ->
                Column {
                    Text(item.versionName)
                    Spacer(Modifier.height(8.dp))
                    Text(item.message)
                    Spacer(Modifier.height(12.dp))
                    val changes = item.changes.groupBy { it::class.java }
                    val new = changes[Change.New::class.java].orEmpty()
                    if (new.isNotEmpty()) {
                        Section(
                            "New", Color(255, 149, 0), new
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    val improvements = changes[Change.Improvement::class.java].orEmpty()
                    if (improvements.isNotEmpty()) {
                        Section("Improvements", Color(51, 153, 255), improvements)
                    }
                    Spacer(Modifier.height(4.dp))
                    val fixes = changes[Change.Fixed::class.java].orEmpty()
                    if (fixes.isNotEmpty()) {
                        Section("Fixes", Color(0, 179, 0), fixes)
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun Section(header: String, color: Color, changes: List<Change>) {
    Column {
        Surface(
            shape = RoundedCornerShape(50),
            color = color
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = header,
                color = Color.White
            )
        }
        changes.forEach {
            Text(it.message)
        }
    }
}
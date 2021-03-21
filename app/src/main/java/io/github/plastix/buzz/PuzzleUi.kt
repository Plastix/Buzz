package io.github.plastix.buzz

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PuzzleDetail(response: PuzzleResponse?) {
    if (response != null) {
        Text(response.printDate)
    }
}

@Preview
@Composable
fun DefaultPreview() {
    PuzzleDetail(null)
}


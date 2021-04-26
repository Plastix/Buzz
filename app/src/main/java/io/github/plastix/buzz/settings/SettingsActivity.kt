package io.github.plastix.buzz.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import dagger.hilt.android.AndroidEntryPoint
import io.github.plastix.buzz.R
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder, SettingsFragment())
                .commit()
        }

        findViewById<ComposeView>(R.id.compose_view).setContent {
            SettingsUi(this::finish, this::openFeedbackEmail, this::toggleDevMenu)
        }
    }

    private fun openFeedbackEmail() {
        startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_feedback_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject))
        })
    }

    private fun toggleDevMenu() {
        val newValue = preferences.toggleDevMenuEnabled()

        val message = if (newValue) {
            R.string.puzzle_detail_debug_tool_enabled
        } else {
            R.string.puzzle_detail_debug_tool_disabled
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

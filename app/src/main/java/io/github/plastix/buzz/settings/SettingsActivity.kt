package io.github.plastix.buzz.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import io.github.plastix.buzz.R

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder, SettingsFragment())
                .commit()
        }

        findViewById<ComposeView>(R.id.compose_view).setContent {
            SettingsUi(this::finish, this::openFeedbackEmail)
        }
    }

    private fun openFeedbackEmail() {
        startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_feedback_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject))
        })
    }
}
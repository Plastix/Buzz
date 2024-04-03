package io.github.plastix.buzz.settings

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

abstract class SharedPreferenceLiveData<T>(
    protected val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: T
) : LiveData<T>(), SharedPreferences.OnSharedPreferenceChangeListener {

    abstract fun readFromPreferences(key: String, defaultValue: T): T

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (this.key == key) {
            value = readFromPreferences(key, defaultValue)
        }
    }

    override fun onActive() {
        super.onActive()
        value = readFromPreferences(key, defaultValue)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onInactive()
    }
}

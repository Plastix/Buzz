package io.github.plastix.buzz.settings

import android.content.SharedPreferences

class BooleanSharedPreferenceLiveData(
    sharedPreferences: SharedPreferences,
    key: String,
    defaultValue: Boolean = false
) : SharedPreferenceLiveData<Boolean>(sharedPreferences, key, defaultValue) {
    override fun readFromPreferences(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}


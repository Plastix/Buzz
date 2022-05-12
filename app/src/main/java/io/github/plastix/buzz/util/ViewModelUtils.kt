package io.github.plastix.buzz.util

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Utility function for creating view models with a saved state block. Useful for view models that use assisted
 * injection.
 */
inline fun <reified T : ViewModel> ComponentActivity.viewModels(crossinline viewModelBlock: (SavedStateHandle) -> T) =
    viewModels<T> {
        object : AbstractSavedStateViewModelFactory(this, Bundle()) {
            override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelBlock.invoke(handle) as T
            }
        }
    }


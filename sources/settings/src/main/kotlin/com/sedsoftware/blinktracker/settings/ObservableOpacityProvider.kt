package com.sedsoftware.blinktracker.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObservableOpacityProvider(context: Context) {
    private val minimizedOpacityKey: Preferences.Key<Float> = floatPreferencesKey(PreferenceKey.OPACITY)

    val opacity: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[minimizedOpacityKey] ?: 0f
    }
}

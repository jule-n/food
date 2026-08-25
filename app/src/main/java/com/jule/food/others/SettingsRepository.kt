package com.jule.food.others

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jule.food.ui.main.dataStore

class SettingsRepository(
    val context: Context
) {
    val settingsFlow = context.dataStore.data

    companion object {
        val SELECTED_LIST_ID = intPreferencesKey("selectedListId")
    }

    suspend fun setSelectedListId(listId: Int) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_LIST_ID] = listId
        }
    }
}
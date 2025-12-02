package com.icure.cardinal.compose.multiplatform.context

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.icure.cardinal.compose.multiplatform.BuildConfig
import com.icure.cardinal.sdk.storage.StorageFacade
import com.icure.cardinal.sdk.storage.impl.DataStorePreferenceStorage

private val Context.cardinalDataStore: DataStore<Preferences> by preferencesDataStore(
    name = buildString {
        append(PlatformContext.applicationId)
        append(".cardinal")
    }
)

actual object PlatformContext {

    private var _applicationContext: Application? = null

    actual val applicationId: String?
        get() = BuildConfig.applicationId.takeIf { it.isNotEmpty() }

    actual val processId: String
        get() = BuildConfig.processId

    actual val specId: String
        get() = BuildConfig.externalServicesSpecId

    actual val cardinalStorageFacade: StorageFacade by lazy {
        requireNotNull(_applicationContext) {
            "applicationContext is not initialized. Call setupValues() before using this property."
        }
        DataStorePreferenceStorage(_applicationContext!!.cardinalDataStore)
    }

    fun setupValues(
        applicationContext: Application
    ) {
        _applicationContext = applicationContext
    }
}
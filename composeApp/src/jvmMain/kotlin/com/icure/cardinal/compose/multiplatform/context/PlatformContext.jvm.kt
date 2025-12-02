package com.icure.cardinal.compose.multiplatform.context

import com.icure.cardinal.sdk.storage.StorageFacade

actual object PlatformContext {
    actual val applicationId: String
        get() = TODO("Not yet implemented")

    actual val processId: String
        get() = TODO("Not yet implemented")

    actual val specId: String
        get() = TODO("Not yet implemented")

    actual val cardinalStorageFacade: StorageFacade
        get() = TODO("Not yet implemented")
}
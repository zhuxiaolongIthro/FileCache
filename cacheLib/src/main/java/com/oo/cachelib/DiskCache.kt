package com.oo.cachelib

import android.content.Context
import androidx.core.app.ActivityCompat
import java.io.File

class DiskCache(val context:Context):IDiskCache {

    var cacheDir:File? = context.externalCacheDir


    override fun checkCacheState(key:String): CacheState {
        return CacheState.CACHE_STATE_NOTEXISIST
    }

    override fun cacheFile(fileName: String) {
        val file = File(cacheDir, fileName)
    }

    override fun cancelCache(fileName: String) {
        val file = File(cacheDir, fileName)
    }

    override fun clearCache(fileName:String) {
        val file = File(cacheDir, fileName)
    }

    override fun getCachedFile(fileName:String):File {
        val file = File(cacheDir, fileName)
        return file
    }


}
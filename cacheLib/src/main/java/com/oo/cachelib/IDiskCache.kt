package com.oo.cachelib

import java.io.File

interface IDiskCache {
    fun checkCacheState(path:String):CacheState
    fun cacheFile(path: String)
    fun cancelCache(path: String)
    fun clearCache(path:String)
    fun getCachedFile(path:String):File

}
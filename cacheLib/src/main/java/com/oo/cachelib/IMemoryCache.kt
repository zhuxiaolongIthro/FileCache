package com.oo.cachelib

interface IMemoryCache {
    //监测当前app内存使用情况 富裕 较少 没有更多
    fun checkMemoryState():CacheMemoryState

}
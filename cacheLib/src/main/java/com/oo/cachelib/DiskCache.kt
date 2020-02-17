package com.oo.cachelib

import android.content.Context
import androidx.core.app.ActivityCompat
import java.io.File

class DiskCache(val context: Context) : IDiskCache {

    var cacheDir: File? = context.externalCacheDir
    val DEFAULT_CACHE_SIZE = 100 * 1024 * 1024L//M
    var config=HashMap<String,String>()

    var CONFIG_CACHE_MAX_SIZE=0//设置缓存文件最大值
    var CONFIG_CACHEED_FILES=ArrayList<ConfigFile>()

    data class ConfigFile(val name:String,var state:CacheState)

    /**
     * 设置缓存配置
     * */
    fun addConfig(key:String,value:String){
        config.put(key, value)
    }
    private fun saveConfig(){

    }
    private fun getConfig(){

    }


    override fun checkCacheState(fileName: String): CacheState {
        val file = File(cacheDir, fileName)
        if (file.exists()) {
            return CacheState.CACHE_STATE_UNCOMPLET
        }
        return CacheState.CACHE_STATE_NOTEXISIST
    }

    private fun remainCacheSpace(): Long {
        val usedSpace = if (cacheDir != null) { cacheDir?.length()?:0L } else { 0L }
        return DEFAULT_CACHE_SIZE - usedSpace
    }

    override fun cacheFile(fileName: String) {
        val file = File(cacheDir, fileName)
    }

    override fun cancelCache(fileName: String) {
        val file = File(cacheDir, fileName)
    }

    override fun clearCache(fileName: String) {
        val file = File(cacheDir, fileName)
    }

    override fun getCachedFile(fileName: String): File {
        val file = File(cacheDir, fileName)
        return file
    }


}
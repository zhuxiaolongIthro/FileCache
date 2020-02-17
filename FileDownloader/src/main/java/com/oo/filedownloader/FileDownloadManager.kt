package com.oo.filedownloader

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.oo.cachelib.CacheState
import com.oo.cachelib.DiskCache
import java.io.File
import java.util.concurrent.Executors

/**
 * 文件下载管理器
 * */
class FileDownloadManager(val context: Context) {
    val TAG = "FileDownloadManager"

    val diskCache=DiskCache(context)
    /**
     * 本地文件
     * */
    var cacheDir:File? = context.externalCacheDir
    var downloadDir:File?=context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

    /**
     * 线程池 并行
     * */
    val threadPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Executors.newWorkStealingPool()
    } else {
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }
    /**
     * 队列线程池
     * */
    val singlePool = Executors.newSingleThreadExecutor()

    val taskMap = HashMap<String,DownloadTask>()


    private fun compressKey(url:String):String{
        return url
    }
    private fun splitFileName(url: String):String {
        val split = url.split("/")
        return split.last()
    }
    fun download(url:String,callback:DownloadCallback){
        val compressKey = splitFileName(url)

        when (diskCache.checkCacheState(compressKey)) {
            CacheState.CACHE_STATE_UNCOMPLET,
            CacheState.CACHE_STATE_NOTEXISIST->{
                val cachedFile = diskCache.getCachedFile(compressKey)
                val downloadTask = DownloadTask(url, cachedFile)
                taskMap.put(compressKey,downloadTask)
                downloadTask.listener = object :DownloadTask.Listener{
                    override fun started() {
                        Log.i(TAG, "started: ")
                    }

                    override fun canceled() {
                        Log.i(TAG, "canceled: ")
                        if (taskMap.contains(compressKey)) {
                            taskMap.remove(compressKey)
                        }
                    }

                    override fun finished() {
                        Log.i(TAG, "finished: ")
                        if (taskMap.contains(compressKey)) {
                            taskMap.remove(compressKey)
                        }
                        callback.downloadSuccess()
                    }

                    override fun process(process: Int) {
                        Log.i(TAG, "process: $process")
                        callback.downloadProcess(process)

                    }

                }
                singlePool.submit(downloadTask)
            }
        }
    }
    fun cancelDownload(url: String){
        val splitFileName = splitFileName(url)
        if (taskMap.containsKey(splitFileName)) {
            taskMap.get(splitFileName)?.cancel()
        }
    }



    interface DownloadCallback{
        fun downloadSuccess()
        fun downloadFailed()
        fun downloadProcess(process:Int)
    }







}
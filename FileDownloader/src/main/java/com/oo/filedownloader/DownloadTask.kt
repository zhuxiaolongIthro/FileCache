package com.oo.filedownloader

import android.os.Build
import android.util.Log
import java.io.*
import java.lang.StringBuilder
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * 下载任务
 *  需要目标保存地址
 *  url ？？
 * */
class DownloadTask(val url:String,val targetFile: File) :Runnable{
    val TAG="DownloadTask"
    /**
     * 当前下载百分百 0~100
     * */
    var process:Int=0
    /**
     * 现在进度回调
     * */
    var listener:Listener?=null
    /**
     * 取消标志位
     * */
    var cancelFlag = false
    /**
     * 执行下载任务
     * */
    override fun run() {
        //首先检查 文件状态
        var currentFileLengh = 0L
        var totalSize = 0L

        if (targetFile.exists().not()) {
            targetFile.createNewFile()
        }
        currentFileLengh=targetFile.length()
        val buildConn = buildConn(url, currentFileLengh, -1)
        buildConn.connect()
        if(buildConn.responseCode==HttpsURLConnection.HTTP_OK||buildConn.responseCode==HttpsURLConnection.HTTP_PARTIAL){
            val inputStream = buildConn.inputStream
            val bis = BufferedInputStream(inputStream)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                totalSize = buildConn.contentLengthLong
            }else{
                totalSize = buildConn.contentLength.toLong()
            }

            if (totalSize == targetFile.length()) {
                listener?.finished()
                return
            }
            if(targetFile.length()>0){//断点续传
                randomWriteFile(bis, totalSize, buildConn)
            }else{//新下载
                writeToEmptyFile(bis, totalSize, buildConn)
            }
        }else{
            val bufferedReader = BufferedReader(InputStreamReader(buildConn.errorStream))
            Log.e(TAG, "run: ${bufferedReader.readText()}")
        }

    }

    private fun randomWriteFile(
        bis: BufferedInputStream,
        totalSize: Long,
        buildConn: HttpsURLConnection
    ) {
        val randomAccessFile = RandomAccessFile(targetFile, "rwd")
        var byteArray = ByteArray(1024 * 10000)
        var end = 0
        listener?.started()
        while (!cancelFlag && bis.read(byteArray).also { end = it } != -1) {
            //写入文件
            randomAccessFile.write(byteArray, 0, end)
            //回调进度
            listener?.process((100 * targetFile.length() / totalSize).toInt())
        }
        //写入完成
        randomAccessFile.close()
        bis.close()
        buildConn.disconnect()
        if (cancelFlag) {//取消 很可能会继续下载
            // TODO: 2020/2/17 是否可以服用connect
            listener?.canceled()
        } else {
            listener?.finished()
        }
    }

    private fun writeToEmptyFile(
        bis: BufferedInputStream,
        totalSize: Long,
        buildConn: HttpsURLConnection
    ) {
        val ops = targetFile.outputStream()
        var byteArray = ByteArray(1024 * 1000*100)
        var end = 0
        listener?.started()
        while (!cancelFlag && bis.read(byteArray).also { end = it } != -1) {
            //写入文件
            ops.write(byteArray, 0, end)
            //回调进度
            listener?.process((100 * targetFile.length() / totalSize).toInt())
        }
        //写入完成
        ops.flush()
        ops.close()
        bis.close()
        buildConn.disconnect()
        if (cancelFlag) {//取消 很可能会继续下载
            // TODO: 2020/2/17 是否可以服用connect
            listener?.canceled()
        } else {
            listener?.finished()
        }
    }

    private fun buildConn(urlStr:String,offset:Long,l:Long):HttpsURLConnection{
        val openConnection = URL(urlStr).openConnection() as HttpsURLConnection
        openConnection.readTimeout=10000
        openConnection.connectTimeout=10000
        if (offset!=0L) {
            val rangeValue = StringBuilder()
            rangeValue.append("bytes:$offset-")
            if(l>0){
                rangeValue.append("${offset+l}")
            }
            openConnection.setRequestProperty("Range",rangeValue.toString())
        }
        return openConnection
    }

    /**
     * 取消下载任务
     * */
    fun cancel(){
        cancelFlag = true
    }


    interface Listener{
        fun started()
        fun canceled()
        fun finished()
        fun process(process:Int)
    }

}
package com.oo.filecache

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.oo.cachelib.CacheState
import com.oo.cachelib.DiskCache
import com.oo.cachelib.IDiskCache
import com.oo.filedownloader.FileDownloadManager

class MainActivity : AppCompatActivity(), FileDownloadManager.DownloadCallback {

    
    val fileDownloadManager = FileDownloadManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.download_image).setOnClickListener {
            //下载图片  1、根据url获取文件名 或者 直接采用url 获取图片文件的唯一标识 用于缓存
            //如果有缓存文件 返回缓存路径
            //如果没有缓存文件 返回null
            //执行下载操作
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
                return@setOnClickListener
            }
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
                return@setOnClickListener
            }

            fileDownloadManager.download("http://img2.imgtn.bdimg.com/it/u=24025528,2806576048&fm=26&gp=0.jpg",this)

        }
        findViewById<Button>(R.id.download_file).setOnClickListener {

        }
    }

    override fun downloadSuccess() {

    }

    override fun downloadFailed() {
    }
}
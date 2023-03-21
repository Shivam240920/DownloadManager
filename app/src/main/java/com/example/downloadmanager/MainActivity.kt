package com.example.downloadmanager

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgname = "demo"
        val imgurl = "https://i.pinimg.com/564x/e2/05/4b/e2054b0c108f943fa58d98b8a4d37cd5.jpg"

        val downloadbtn = findViewById<Button>(R.id.downloadbtn)
        downloadbtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),0)
            }
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
            }
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.INTERNET),0)
            }

            val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageVolumes = storageManager.storageVolumes
            val storageVolume = storageVolumes.get(0)

            val file = File(storageVolume.directory?.path + "/Download/" + imgname)

            if(file.exists())
                Toast.makeText(this, "File already exists", Toast.LENGTH_SHORT).show()
            else {
                GlobalScope.launch(Dispatchers.Unconfined) {
                    val downloadid = downloadimg(imgurl, imgname)
                }
            }
        }
    }

    private fun downloadimg(url:String, outputimgname :String):Long{
        val downloadmanager = applicationContext.getSystemService(DownloadManager::class.java)
        val request = DownloadManager.Request(url.toUri())
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , outputimgname)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(outputimgname)
            .setMimeType("image")

        return downloadmanager.enqueue(request)
    }
}
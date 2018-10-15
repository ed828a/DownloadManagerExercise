package com.example.ed828.downloadmanagerexercise

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var downloadId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDownload.setOnClickListener {
            startDownload()
        }

        btnCancel.setOnClickListener {
            cancelDownload()
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val broadcastedDownloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (broadcastedDownloadId != null && broadcastedDownloadId == downloadId) {
                    if (getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL){
                        Toast.makeText(this@MainActivity, "Download complete.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Download not complete.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }, filter)
    }

    private fun getDownloadStatus(): Int {
        val query = DownloadManager.Query()

        return if (downloadId != null) {
            query.setFilterById(downloadId!!)
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                cursor.getInt(columnIndex)
            } else {
                DownloadManager.ERROR_UNKNOWN
            }

        } else {
            DownloadManager.ERROR_UNKNOWN
        }
    }

    private fun cancelDownload() {
        downloadId?.let{
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.remove(it)
        }
    }

    private fun startDownload() {
        val uri = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/2/2d/Snake_River_%285mb%29.jpg")

        val request = DownloadManager.Request(uri)
        with(request) {
            setTitle("Downloading")
            setDescription("Download file from the internet.")
            setDestinationInExternalFilesDir(
                this@MainActivity,
                Environment.DIRECTORY_DOWNLOADS,
                "LargeImage.jpg"
            )
        }
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
    }


}

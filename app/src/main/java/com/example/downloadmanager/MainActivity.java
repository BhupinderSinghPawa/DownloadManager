package com.example.downloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private long downloadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=findViewById(R.id.download);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginDownload();
            }
        });

        // Once the BroadcastReceiver is created, register for ACTION_DOWNLOAD_COMPLETE.
        registerReceiver(onDownloadComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // Responding to download notification clicks
        // IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        // registerReceiver(receiver, filter);

    }

    // It is also important that you unregister the BroadcastReceiver in onDestroy.
    // This ensures you only listen for this broadcast as long as the activity is active
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    private void beginDownload(){
        // create a new file name in default directory
        File file=new File(getExternalFilesDir(null),"speedtest10Mb.db");

        // Create a DownloadManager.Request with all the information necessary to start the download
        DownloadManager.Request request= new DownloadManager.Request(
                Uri.parse("http://speedtest.ftp.otenet.gr/files/test10Mb.db"))
                .setTitle("Dummy File")// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file - arbitrary path
                .setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

        // to ensure a large fi le is downloaded only when connected to Wi-Fi:
        // request.setAllowedNetworkTypes(request.NETWORK_WIFI);

        // get system service DOWNLOAD_SERVICE
        DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        // enqueue puts the download request in the queue.
        downloadID = downloadManager.enqueue(request);
    }

    // Create a BroadcastReceiver.
    // Check if the received broadcast is for our download
    // by matching the received download id with our enqueued download.
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(MainActivity.this,
                        "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }

    };

    /*
    // Responding to download notification clicks
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String extraID = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
            long[] references = intent.getLongArrayExtra(extraID);
            for (long reference : references)
                if (reference == myDownloadReference) {
                // Do something with downloading file.
                }
        }
    };
    */
}

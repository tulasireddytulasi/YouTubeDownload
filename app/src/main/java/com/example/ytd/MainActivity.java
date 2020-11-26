package com.example.ytd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button button, paste;
    private String url;
    private ProgressBar progressBar;
    private TextView textView, info;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.linear1);
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.download);
        paste = findViewById(R.id.paste);
        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.percent);
        info = findViewById(R.id.info);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadClip();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection(getApplicationContext())){
                    Download();
                }else {
                    Toasty.warning(getApplicationContext(), "Check your Internet Connection...", Toasty.LENGTH_LONG).show();
                }
            }
        });
    }

    private void Download() {
        url = editText.getText().toString().trim();
        if (!url.isEmpty()){
            if(url.contains("https://www.youtube.com/watch?v=") || url.contains("https://youtu.be/")){
                CheckPermission();
            }else {
                info.setText("Enter Only YouTube Url");
                Toasty.warning(getApplicationContext(), "Enter Only YouTube Url", Toasty.LENGTH_LONG).show();
            }
        }else {
            info.setText("Enter YouTube Url");
            Toasty.warning(getApplicationContext(), "Enter YouTube Url", Toasty.LENGTH_LONG).show();
        }
    }

    private boolean checkInternetConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifi != null && wifi.isConnected()) || (mobi != null && mobi.isConnected())){
            return true;
        }else {
            return false;
        }
    }

    private void loadClip() {

        if (clipboardManager.hasPrimaryClip()){
            ClipData clipData = clipboardManager.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            info.setText(item.getText().toString());
            editText.setText(item.getText().toString());
            Log.e("c",item.getText().toString());
        }
    }

    private void CheckPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                       if(multiplePermissionsReport.areAllPermissionsGranted()){
                           button.setEnabled(false);
                           YTDownload(18);

                       }else if(multiplePermissionsReport.isAnyPermissionPermanentlyDenied()){
                           MultiplePermissionsListener dialogMultiplePermissionsListener =
                                   DialogOnAnyDeniedMultiplePermissionsListener.Builder
                                           .withContext(MainActivity.this)
                                           .withTitle("Read & Write Storage permissions")
                                           .withMessage("Both Read and Write permission are needed to download YouTube Video.")
                                           .withButtonText(android.R.string.ok)
                                           .withIcon(R.mipmap.ic_launcher)
                                           .build();
                       }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
    }



    public void YTDownload(final int itag) {
        String VideoURLDownload = editText.getText().toString().trim();
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if ((ytFiles != null)) {
                    String downloadURL = ytFiles.get(itag).getUrl();
                    Log.e("Download URL:", downloadURL);

                    if (downloadURL != null) {
                        button.setEnabled(false);
                        Download_video(downloadURL, videoTitle);
                    }
                } else {
                    info.setText("Error");
                    Toasty.error(getApplicationContext(), "Error", Toasty.LENGTH_LONG).show();
                }
            }
        };
        youTubeUriExtractor.execute(VideoURLDownload);
    }



    private void Download_video(final String uri, final String filename){
        Log.e("Download URL:", uri);

        File myDirectory = new File(Environment.getExternalStorageDirectory(), "YouTube_Videos");
        if(myDirectory.exists()) {
           // Toast.makeText(MainActivity.this, "No Folder", Toast.LENGTH_SHORT).show();
            Log.e("dirs", "No Folder");
        }else{
            myDirectory.mkdirs();
            Toast.makeText(MainActivity.this, myDirectory.toString(), Toast.LENGTH_SHORT).show();
            Log.e("dirs", myDirectory.toString());
        }

        Log.e("dir", myDirectory.toString());
        info.setText("Download Started, Please wait untill download complete...");
        linearLayout.setVisibility(View.VISIBLE);

        final File file = new File(myDirectory, filename);
        AndroidNetworking.download(uri, myDirectory.toString(), filename+".mp4")
                .setTag("downloadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        float progress = (float) bytesDownloaded / totalBytes * 100;
                        progressBar.setProgress((int)progress);
                        textView.setText((int)progress+"%");
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        button.setEnabled(true);
                        info.setText(filename);
                        Toasty.success(getApplicationContext(), "Downloading Completed Successfully...", Toast.LENGTH_LONG, true).show();
                        Log.d("Tag", "Scan finished. You can view the image in the DDDDDDD now.");
                        try {
                            MediaScannerConnection.scanFile(getApplicationContext(),
                                    new String[]{file.toString()}, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.i("ExternalStorage", "Scanned " + path + ":");
                                            Log.i("ExternalStorage", "-> uri=" + uri);
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Toasty.error(getApplicationContext(), "Error: " + error, Toast.LENGTH_LONG, true).show();
                        Log.e("e", error.toString());
                        button.setEnabled(true);
                        info.setText(error.toString());
                    }
                }); // === end download ===
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadClip();
        Log.e("c","stated");
    }
}
package com.example.ytd;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.Date;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private String url;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.download);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Downloading Video, Please wait");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        String youtubeLink = "https://www.youtube.com/watch?v=oka9Vzlja-4";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                url = editText.getText().toString().trim();
//                if (url != null && !url.isEmpty()){
//
//                    if(url.contains("https://www.youtube.com/watch?v=") || url.contains("https://youtu.be/")){
//                        CheckPermission();
//                    }else {
//                        Toast.makeText(MainActivity.this," Enter Only YouTube Url", Toast.LENGTH_SHORT).show();
//                    }
//
//                }else {
//                    Toast.makeText(MainActivity.this," Enter YouTube Url", Toast.LENGTH_SHORT).show();
//                }
CheckPermission();
            }
        });

//        new YouTubeExtractor(this) {
//            @Override
//            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
//                if (ytFiles != null) {
//                    int itag = 22;
//                    String downloadUrl = ytFiles.get(itag).getUrl();
//                    Log.d("yt", downloadUrl);
//                    Toast.makeText(MainActivity.this, downloadUrl, Toast.LENGTH_LONG).show();
//
//                    if (downloadUrl != null) {
//                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
//                        request.setTitle(ytFiles.ge);
//                        request.setDestinationInExternalPublicDir("/Downloads/YouTube-Downloader/", videoTitle + ".mp4");
//                        if (downloadManager != null) {
//                            downloadManager.enqueue(request);
//                        }
//                    }
//                } else Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
//                }
//            }
//        }.extract(youtubeLink, true, true);
    }

    private void CheckPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                       if(multiplePermissionsReport.areAllPermissionsGranted()){
                           YTDownload(18);
//                           File myDirectory = new File(Environment.getExternalStorageDirectory(), "Movies");
//
//                           if(myDirectory.exists()) {
//                               Toast.makeText(MainActivity.this, "No Folder", Toast.LENGTH_SHORT).show();
//                               Log.e("dirs", "No Folder");
//                           }else{
//                               myDirectory.mkdirs();
//                               Toast.makeText(MainActivity.this, myDirectory.toString(), Toast.LENGTH_SHORT).show();
//                               Log.e("dirs", myDirectory.toString());
//                           }
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

    private void CheckPermissions() {
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                       // YTDownload(18 );
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Permision Required")
                                    .setMessage("Permission Required to access image")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("pakage", getPackageName(), null));
                                            startActivityForResult(intent, 51);
                                        }
                                    })
                                    .setNegativeButton("Cancel",null)
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public void YTDownload(final int itag) {
       // String VideoURLDownload = "https://www.youtube.com/watch?v=oka9Vzlja-4";
        String VideoURLDownload = editText.getText().toString().trim();
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if ((ytFiles != null)) {
                    String downloadURL = ytFiles.get(itag).getUrl();
                  //  Log.e("Download URL:", downloadURL);

                    if (downloadURL != null) {
//                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
//                        request.setTitle(videoTitle);
//                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, videoTitle + ".mp4");
//                        if (downloadManager != null) {
//                            downloadManager.enqueue(request);
//                        }

                       // DownloadManager2(downloadURL, videoTitle);
                        Download_video(downloadURL, videoTitle);

                    }
                } else Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        };
        youTubeUriExtractor.execute(VideoURLDownload);
    }

    private void DownloadManager2(final String uri, final String filename) {
        DownloadManager downloadManager;
        downloadManager = (DownloadManager) getSystemService(getApplicationContext().DOWNLOAD_SERVICE);
        Uri urls = Uri.parse(uri);
        DownloadManager.Request request = new DownloadManager.Request(urls);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle(filename)
                .setDescription("Something useful. No, really.")  // "ImageName.jpg"
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, filename + ".mp4")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }

    private void Download_video(final String uri, final String filename){
        progressDialog.show();
        Log.e("Download URL:", uri);
        File dirpath = new File(Environment.getExternalStorageDirectory().getPath()
                + "/MyVideos");
        if (!dirpath.exists()) {
            dirpath.mkdirs();
        }


        File myDirectory = new File(Environment.getExternalStorageDirectory(), "Movies12");

                           if(myDirectory.exists()) {
                               Toast.makeText(MainActivity.this, "No Folder", Toast.LENGTH_SHORT).show();
                               Log.e("dirs", "No Folder");
                           }else{
                               myDirectory.mkdirs();
                               Toast.makeText(MainActivity.this, myDirectory.toString(), Toast.LENGTH_SHORT).show();
                               Log.e("dirs", myDirectory.toString());
                           }



        Log.e("dir", dirpath.toString());

//        File dirpath = new File(Environment.DIRECTORY_MOVIES);
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
                        progressDialog.setProgress((int)progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), filename + " Downloaded", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        Log.e("e", error.toString());
                        progressDialog.dismiss();
                    }
                }); // === end download ===
    }

}
package com.rahbarbazaar.poller.android.Models;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.rahbarbazaar.poller.android.BuildConfig;
import com.rahbarbazaar.poller.android.Network.ServiceProvider;
import com.rahbarbazaar.poller.android.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class DownloadQueue extends Job {

    private String download_url, update_version;

    private String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    private String getUpdate_version() {
        return update_version;
    }

    public void setUpdate_version(String update_version) {
        this.update_version = update_version;
    }

    public DownloadQueue() {
        super(new Params(1).requireNetwork().persist());
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        initialNotificationBuilder();
        sendProgressNotification(0);
        requestDownload();

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    private void requestDownload() {

        ServiceProvider provider = new ServiceProvider(getApplicationContext());
        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(provider.getmService().downloadWithUrl(getDownload_url()).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {

                        try {
                            progressDownload(responseBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    private void progressDownload(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();

        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File outputFile = new File(path, "poller v" + getUpdate_version() + ".apk");

        OutputStream outputStream = new FileOutputStream(outputFile);
        long total = 0;

        while ((count = inputStream.read(data)) != -1) {

            total += count;
            int progress = (int) ((total * 100) / fileSize);
            //send notify progress:
            sendProgressNotification(progress);
            outputStream.write(data, 0, count);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();

        //download complete:
        onDownloadCompleted(outputFile);
    }

    private void sendProgressNotification(int progress) {

        builder.setContentTitle("دانلود برنامه");
        builder.setContentText("دانلود در حال اجرا است...");
        builder.setSmallIcon(R.drawable.poller_icon);
        builder.setProgress(100, progress, true);

        if (manager != null)
            manager.notify(11, builder.build());
    }

    private void initialNotificationBuilder() {

        Context context = getApplicationContext();
        String channel_id = "download_channel_id";
        builder = new NotificationCompat.Builder(context, channel_id);

        builder.setAutoCancel(false);
        builder.setChannelId(channel_id);
        builder.setOnlyAlertOnce(true);

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "download", NotificationManager.IMPORTANCE_DEFAULT);
            if (manager != null)
                manager.createNotificationChannel(notificationChannel);
        }
    }

    private void onDownloadCompleted(File file) {

        Context context = getApplicationContext();

        if (manager != null) {

            manager.cancel(11);
            builder.setProgress(0, 0, false);
            builder.setContentTitle("دانلود برنامه");
            builder.setContentText("دانلود به اتمام رسید");
            builder.setSmallIcon(R.drawable.poller_icon);
            manager.notify(11, builder.build());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else {

            Uri apkUri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}

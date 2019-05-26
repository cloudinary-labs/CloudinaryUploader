package com.cloudinary.uploader;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

final class CloudinaryCallback implements UploadCallback {

    ErrorInfo lastErrorObject = null;
    Map lastSuccess = null;
    ErrorInfo lastReschedule = null;
    Context mContext;

    CloudinaryCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onStart(String requestId) {
    }

    @Override
    public void onProgress(String requestId, long bytes, long totalBytes) {
    }

    @Override
    public void onSuccess(String requestId, Map resultData) {
        this.lastSuccess = resultData;
        this.lastErrorObject = null;
        showNotificationWithMessage(1, "Upload Done");
    }

    @Override
    public void onError(String requestId, ErrorInfo errorObject) {
        this.lastErrorObject = errorObject;
        showNotificationWithMessage(1, "Upload Error");
    }

    public boolean hasResponse(){
        return lastErrorObject != null || lastSuccess != null || lastReschedule != null;
    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) {
        this.lastReschedule = error;
    }

    private void showNotificationWithMessage(int notifyID, String message) {
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "Cloudinary Uploader";

        Notification notification = createNotification(message, CHANNEL_ID);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(notifyID , notification);
    }

    private Notification createNotification(String message, String CHANNEL_ID) {
        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(mContext)
                    .setContentTitle("Cloudinary Uploader")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID)
                    .build();
        } else {
            notification = new Notification.Builder(mContext)
                    .setContentTitle("Cloudinary Uploader")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentText(message)
                    .build();
        }

        return notification;
    }
}
package com.cloudinary.uploader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.android.MediaManager;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryUploadActivity extends AppCompatActivity {

    public final String LOG_TAG = "CloudinaryUploader";
    private final String PREF_NAME = "cloudinary_uploader_pref";
    private final String CLOUD_NAME_PREF_NAME = "cloud_name";
    private final String UPLOAD_PRESET_PREF_NAME = "upload_preset";

    private String prefCloudName, prefUploadPreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudinary_upload);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        final SharedPreferences sharedPref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefCloudName = sharedPref.getString(CLOUD_NAME_PREF_NAME, "");
        prefUploadPreset = sharedPref.getString(UPLOAD_PRESET_PREF_NAME, "");

        if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
            try {
                handleSendImage(intent); // Handle single image being sent
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null && type.startsWith("image/")) {
            handleSendMultipleImages(intent); // Handle multiple images being sent
        } else {
            // Handle other intents, such as being started from the home screen
            // Error?
        }
    }

    void handleSendImage(Intent intent) throws FileNotFoundException {
        final Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            final ImageView imgView = findViewById(R.id.previewImage);
            imgView.setImageURI(imageUri);

            Button uploadButton = findViewById(R.id.uploadButton);
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                showNotificationWithMessage(1, "Uploading image...");
                    CloudinaryUploaderApplication.counter = -1;
                try {
                    InputStream imageInputStream = getContentResolver().openInputStream(imageUri);
                    String requestId = MediaManager.get()
                            .upload(IOUtils.toByteArray(imageInputStream))
                            .unsigned(prefUploadPreset)
                            .callback(new CloudinaryCallback(CloudinaryUploadActivity.this))
                            .startNow(CloudinaryUploadActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                moveTaskToBack(true);
                }
            });
        }
    }

    void handleSendMultipleImages(Intent intent) {
        final ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            final ImageView imgView = findViewById(R.id.previewImage);
            imgView.setImageResource(R.drawable.ic_launcher_foreground);
            TextView multipleTextView = findViewById(R.id.multipleFilesText);
            multipleTextView.setText("Uploading " + imageUris.size() + " files");
            multipleTextView.setTextSize(20);

            Button uploadButton = findViewById(R.id.uploadButton);
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                showNotificationWithMessage(1, "Uploading images...");
                CloudinaryUploaderApplication.counter = 0;
                for (Uri imageUri : imageUris) {
                    try {
                        InputStream imageInputStream = getContentResolver().openInputStream(imageUri);
                        String requestId = MediaManager.get()
                                .upload(IOUtils.toByteArray(imageInputStream))
                                .unsigned(prefUploadPreset)
                                .callback(new CloudinaryCallback(CloudinaryUploadActivity.this, imageUris.size()))
                                .dispatch();
                        Thread.sleep(50);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                moveTaskToBack(true);
                }
            });
        }
    }

    private void showNotificationWithMessage(int notifyID, String message) {
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "Cloudinary Uploader";

        Notification notification = createNotification(message, CHANNEL_ID);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(notifyID , notification);
    }

    private Notification createNotification(String message, String CHANNEL_ID) {
        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setContentTitle("Cloudinary Uploader")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("Cloudinary Uploader")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentText(message)
                    .build();
        }

        return notification;
    }
}

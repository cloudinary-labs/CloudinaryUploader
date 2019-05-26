package com.cloudinary.uploader;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryUploaderApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!

    public static Map config;
    private final String PREF_NAME = "cloudinary_uploader_pref";
    private final String CLOUD_NAME_PREF_NAME = "cloud_name";

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!

        config = new HashMap();
        final SharedPreferences sharedPref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String prefCloudName = sharedPref.getString(CLOUD_NAME_PREF_NAME, "");
        config.put("cloud_name", prefCloudName);
        MediaManager.init(this, config);
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
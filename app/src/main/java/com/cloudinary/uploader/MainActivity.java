package com.cloudinary.uploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String PREF_NAME = "cloudinary_uploader_pref";
    private final String CLOUD_NAME_PREF_NAME = "cloud_name";
    private final String UPLOAD_PRESET_PREF_NAME = "upload_preset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","support@cloudinary.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Cloudinary Uploader for Android - Support");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        final SharedPreferences sharedPref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String prefApiKey = sharedPref.getString(CLOUD_NAME_PREF_NAME, "");
        String prefUploadPreset = sharedPref.getString(UPLOAD_PRESET_PREF_NAME, "");
        if (prefApiKey != "") {
            TextInputEditText editText = findViewById(R.id.textInputCloudinaryApiKey);
            editText.setText(prefApiKey);
        }

        if (prefUploadPreset != "") {
            TextInputEditText editText = findViewById(R.id.textInputUploadPreset);
            editText.setText(prefUploadPreset);
        }

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                EditText editTextApiKey = findViewById(R.id.textInputCloudinaryApiKey);
                String inputCloudName = editTextApiKey.getText().toString();
                EditText editTextUploadPreset = findViewById(R.id.textInputUploadPreset);
                String inputUploadPreset = editTextUploadPreset.getText().toString();
                editor.putString(CLOUD_NAME_PREF_NAME, inputCloudName);
                editor.putString(UPLOAD_PRESET_PREF_NAME, inputUploadPreset);
                editor.commit();
                //Intent intent = getIntent();
                finish();
                //startActivity(intent);
                System.exit(0);
            }
        });
    }
}

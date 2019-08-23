package com.android.personal.usersystem.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class BaseActivity extends AppCompatActivity {

    File fileSaveLocation = null;
    String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e(TAG,  paramThrowable.getMessage());
            }
        });

        fileSaveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    protected void toastMessage(final String message){
        BaseActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,  message);
                Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}

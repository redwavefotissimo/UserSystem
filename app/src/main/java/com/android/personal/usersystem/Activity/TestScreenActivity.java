package com.android.personal.usersystem.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.personal.usersystem.R;
import com.common.EmailAPI.EmailAPI;
import com.common.EmailAPI.GMailAPI;
import com.common.ExcelAPI.ExcelAPI;
import com.common.HTMLAPI.HTMLAPI;
import com.common.WriterManagerInfo;

import java.io.File;

public class TestScreenActivity extends AppCompatActivity {

    final String TAG = "TestScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
              public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                  Log.e(TAG,  paramThrowable.getMessage());
              }
          });

        Button button = (Button) this.findViewById(R.id.buttonTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandlerThread mMyHandlerThread = new HandlerThread("test");
                mMyHandlerThread.start();

                Handler mHandler = new Handler(mMyHandlerThread.getLooper());
                mHandler.post(new Runnable() {
                    public void run() {
                        //testEmailSend();
                        //testExcelWrite();
                        //testHTML();
                        testGmailSend();
                    }
                });
            }
        });
    }

    private void testHTML(){
        try {
            File saveFileLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = "test";

            HTMLAPI htmlAPI = new HTMLAPI(saveFileLocation, fileName);

            WriterManagerInfo info = new WriterManagerInfo();
            info.value = "test1";
            info.contentAlignment = WriterManagerInfo.ContentAlignment.Center;
            info.contentStyle = WriterManagerInfo.ContentStyle.BoldUnderLine;
            htmlAPI.insertRow(info);

            info = new WriterManagerInfo();
            info.value = "test2test2test2test2test2";
            htmlAPI.insertRow(info);

            info = new WriterManagerInfo();
            info.value = "test3";
            htmlAPI.insertRow(info);

            htmlAPI.write();
            Toast.makeText(TestScreenActivity.this, "done excel create", Toast.LENGTH_LONG).show();
            Log.e(TAG, "done excel create");
        }catch (Exception ex){
            Toast.makeText(TestScreenActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void testExcelWrite(){
        try {
            File saveFileLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = "test";

            ExcelAPI excelAPI = new ExcelAPI(saveFileLocation, fileName);

            WriterManagerInfo info = new WriterManagerInfo();
            info.value = "test1";
            excelAPI.insertRow(info);

            info = new WriterManagerInfo();
            info.value = "test2";
            excelAPI.insertRow(info);

            info = new WriterManagerInfo();
            info.value = "test3";
            excelAPI.insertRow(info);

            excelAPI.write();
            Toast.makeText(TestScreenActivity.this, "done excel create", Toast.LENGTH_LONG).show();
            Log.e(TAG, "done excel create");
        }catch (Exception ex){
            Toast.makeText(TestScreenActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void testGmailSend(){
        try{

            GMailAPI gMailAPI = new GMailAPI("redwavefotissimo@gmail.com");

            /*gMailAPI.sendMessage(gMailAPI.processCredential(),
                    gMailAPI.createEmail("redwavefotissimo@gmail.com", "subject", "body test"));
            */
            gMailAPI.processCredential();
            Toast.makeText(TestScreenActivity.this, "done send", Toast.LENGTH_LONG).show();
            Log.e(TAG, "done send");
        }
        catch (Exception ex){
            Toast.makeText(TestScreenActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void testEmailSend(){
        try {
            EmailAPI emailAPI = new EmailAPI();
            emailAPI.setSSLProps("redwavefotissimo@gmail.com", "Prettycure1", "465", "smtp.gmail.com");
            emailAPI.sendEmail("redwavefotissimo@gmail.com", "redwavefotissimo@gmail.com", "", "subject",
                    "content test", null, EmailAPI.ContentType.Plain);
            Toast.makeText(TestScreenActivity.this, "done send", Toast.LENGTH_LONG).show();
            Log.e(TAG, "done send");
        }
        catch (Exception ex){
            Toast.makeText(TestScreenActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }
}

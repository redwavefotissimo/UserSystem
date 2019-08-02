package com.android.personal.usersystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.personal.usersystem.R;
import com.common.BoxNetAPI.BoxNetAPI;
import com.common.EmailAPI.EmailAPI;
import com.common.ExcelAPI.ExcelAPI;
import com.common.HTMLAPI.HTMLAPI;
import com.common.AbstractOrInterface.WriterManagerInfo;
import com.common.OpenDriveAPI.OpenDriveAPI;
import com.common.SMSAPI.SMSAPI;
import com.common.recaptchaAPI.ReCaptcha;
import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

import java.io.File;

public class TestScreenActivity extends AppCompatActivity {

    final String TAG = "TestScreenActivity";
    final int PermissionRequestCode = 1212;
    final int PickContact = 1211;

    TextView fileLoc;
    HandlerThread mMyHandlerThread;
    File fileToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);

        mMyHandlerThread = new HandlerThread("test");
        mMyHandlerThread.start();

        new AskPermission.Builder(this)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS)
                .setCallback(new PermissionCallback(){
                    @Override
                    public void onPermissionsGranted(int requestCode) {
                        Toast.makeText(TestScreenActivity.this, "All Permissions Granted", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionsDenied(int requestCode) {
                        Toast.makeText(TestScreenActivity.this, "some Permissions not Granted", Toast.LENGTH_LONG).show();
                    }
                })
                .setErrorCallback(new ErrorCallback(){
                    @Override
                    public void onShowRationalDialog(PermissionInterface permissionInterface, int requestCode) {
                        // Alert user by Dialog or any other layout that you want.
                        // When user press OK you must need to call below method.
                        permissionInterface.onDialogShown();
                    }

                    @Override
                    public void onShowSettings(PermissionInterface permissionInterface, int requestCode) {
                        // Alert user by Dialog or any other layout that you want.
                        // When user press OK you must need to call below method.
                        // It will open setting screen.
                        permissionInterface.onSettingsShown();
                    }
                })
                .request(PermissionRequestCode);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
              public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                  Log.e(TAG,  paramThrowable.getMessage());
              }
          });

        Button button = (Button) this.findViewById(R.id.buttonTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Handler mHandler = new Handler(mMyHandlerThread.getLooper());
                mHandler.post(new Runnable() {
                    public void run() {
                        //testEmailSend();
                        //testExcelWrite();
                        //testHTML();
                        //testOpenDrive();
                        //testBoxNet();
                        //testSMSSendAndRead();
                        testRecaptcha();
                    }
                });
            }
        });

        Button fileBrowser = (Button) this.findViewById(R.id.buttonFileBrowser);
        fileBrowser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = null;

                FilePickerDialog dialog = new FilePickerDialog(TestScreenActivity.this,properties);
                dialog.setTitle("Select a File");

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                       if(files.length > 0){
                           fileToUpload = new File(files[0]);
                           fileLoc.setText(fileToUpload.getAbsolutePath());
                       }
                    }
                });

                dialog.show();
            }
        });

        fileLoc = (TextView) this.findViewById(R.id.fileLocation);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PickContact) :
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri contactData = data.getData();
                        Cursor c = managedQuery(contactData, null, null, null, null);
                        if (c.moveToFirst()) {
                            String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                            if (hasPhone.equalsIgnoreCase("1")) {
                                final SMSAPI smsapi = new SMSAPI();
                                final Cursor phones = getContentResolver().query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                        null, null);
                                phones.moveToFirst();
                                Handler mHandler = new Handler(mMyHandlerThread.getLooper());
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        try {
                                            smsapi.send("test code is 111", phones.getString(phones.getColumnIndex("data1")));
                                        }catch (Exception ex){
                                            Log.e(TAG, ex.toString());
                                            Toast.makeText(TestScreenActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            //String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        }
                    }catch (Exception ex){
                        Log.e(TAG, ex.toString());
                        Toast.makeText(TestScreenActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
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

    private void testOpenDrive(){
        OpenDriveAPI ODAPI = new OpenDriveAPI("redwavefotissimo@gmail.com", "Prettycure1");

        try {
            ODAPI.getSessionID();
            ODAPI.getLoginResponseInfo();
            ODAPI.getPublicFolder();
            ODAPI.uploadFile(fileToUpload);
        } catch (Exception e){
            Log.e(TAG,  e.toString());
            Toast.makeText(TestScreenActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void testBoxNet(){
        try {
            BoxNetAPI boxNetAPI = new BoxNetAPI(this);
        }
        catch (Exception e){
            Log.e(TAG,  e.toString());
            Toast.makeText(TestScreenActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void testSMSSendAndRead(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PickContact);
    }

    private void testRecaptcha(){
        ReCaptcha reCaptcha = new ReCaptcha(this);
        final Handler handler = new Handler(this.getMainLooper());
        reCaptcha.setReCaptchaResults(new ReCaptcha.ReCaptchaResults() {
            @Override
            public void onSuccess(final String success) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestScreenActivity.this, success , Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onError(final String error) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestScreenActivity.this, error , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        reCaptcha.connectGoogleApiClient();
        reCaptcha.useReCaptcha();
    }
}

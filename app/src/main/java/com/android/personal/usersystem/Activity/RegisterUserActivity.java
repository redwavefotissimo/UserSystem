package com.android.personal.usersystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.personal.usersystem.R;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySQLAPIResponse;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI;
import com.common.BoxNetAPI.BoxItemSimpleInfo;
import com.common.BoxNetAPI.BoxNetAPI;
import com.common.Utils;

import java.io.File;


public class RegisterUserActivity extends AppCompatActivity {

    final String TAG = "RegisterUserActivity";
    final String profileFolder = "PROFILE_FOLDER";
    final String attachmentFOlder = "ATTACHMENT_FOLDER";

    public static final int requestTakeCamera = 1122;

    EditText firstName, lastName, userName, password, retypePassword;
    Button submitBTN, takePictureBTN;
    ProgressBar progressBar;

    String fileLocation;
    Handler handler;
    HandlerThread handlerThread;
    UserSystemMySqlAPI UserSystemMySqlAPI;
    BoxNetAPI boxNetAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e(TAG,  paramThrowable.getMessage());
            }
        });

        try {

            handlerThread = new HandlerThread("backgroundThread");
            handlerThread.start();

            handler = new Handler(handlerThread.getLooper());
            UserSystemMySqlAPI = new UserSystemMySqlAPI(this);

            progressBar = (ProgressBar) this.findViewById(R.id.progressBar);

            firstName = (EditText) this.findViewById(R.id.firstName);
            lastName = (EditText) this.findViewById(R.id.lastName);
            userName = (EditText) this.findViewById(R.id.userName);
            password = (EditText) this.findViewById(R.id.password);
            retypePassword = (EditText) this.findViewById(R.id.retypepassword);

            submitBTN = (Button) this.findViewById(R.id.submitBTN);
            submitBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            enableProgress(true);
                            BoxItemSimpleInfo BoxItemSimpleInfo = null;
                            try {
                                if (boxNetAPI == null) {
                                    boxNetAPI = new BoxNetAPI(RegisterUserActivity.this);
                                }

                                if(!password.getText().toString().isEmpty() && retypePassword.getText().toString().isEmpty())
                                {
                                    toastMessage("retype password is required!");
                                    return;
                                }

                                if(password.getText().toString().isEmpty() && !retypePassword.getText().toString().isEmpty())
                                {
                                    toastMessage("password is required!");
                                    return;
                                }

                                if (!password.getText().toString().equals(retypePassword.getText().toString())
                                        && !password.getText().toString().isEmpty()
                                        && !retypePassword.getText().toString().isEmpty()) {
                                    toastMessage("password and retype password are not equal");
                                    return;
                                }

                                if (fileLocation == null || fileLocation.isEmpty()) {
                                    toastMessage("profile picture required");
                                    return;
                                }

                                UserInfo userInfo = new UserInfo();
                                userInfo.firstName = firstName.getText().toString();
                                userInfo.lastName = lastName.getText().toString();
                                userInfo.userName = userName.getText().toString();
                                userInfo.password = Utils.convertStringToSha512(password.getText().toString(), UserSystemMySqlAPI.salt);

                                boxNetAPI.getUserSystemFolder();

                                BoxItemSimpleInfo = boxNetAPI.getUserPrivateFolder(userInfo.userName);
                                if (BoxItemSimpleInfo == null) {
                                    BoxItemSimpleInfo = new BoxItemSimpleInfo();
                                    BoxItemSimpleInfo.id = boxNetAPI.createUserPrivateFolder(userInfo.userName).id;
                                }

                                userInfo.profileFolderId = boxNetAPI.createUserPrivateFolder(profileFolder, BoxItemSimpleInfo.id).id;
                                userInfo.attachementFolderId = boxNetAPI.createUserPrivateFolder(attachmentFOlder, BoxItemSimpleInfo.id).id;

                                userInfo.profileId = boxNetAPI.updloadFile(new File(fileLocation), userInfo.profileFolderId).entries[0].id;

                                Thread.sleep(1000);

                                boxNetAPI.setFileItemAsSharable(userInfo.profileId);

                                UserSystemMySQLAPIResponse UserSystemMySQLAPIResponse = UserSystemMySqlAPI.insertUser(userInfo);
                                toastMessage(UserSystemMySQLAPIResponse.message);
                                if (UserSystemMySQLAPIResponse.statusCode != 200) {
                                    deleteAllCreatedBoxContent(BoxItemSimpleInfo);
                                }
                            } catch (Exception ex) {
                                deleteAllCreatedBoxContent(BoxItemSimpleInfo);
                                toastMessage(ex.toString());
                            } finally {
                                enableProgress(false);
                            }
                        }
                    });
                }
            });

            takePictureBTN = (Button) this.findViewById(R.id.takePictureBTN);
            takePictureBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(RegisterUserActivity.this, CameraActivity.class);
                    startActivityForResult(i, requestTakeCamera);
                }
            });
        }
        catch(Exception e){
            toastMessage(e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==requestTakeCamera && resultCode == Activity.RESULT_OK)
        {
            fileLocation=data.getExtras().getString(CameraActivity.FILE_LOCATION);
            //Toast.makeText(RegisterUserActivity.this, "Saved:" + fileLocation, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAllCreatedBoxContent(BoxItemSimpleInfo BoxItemSimpleInfo){
        try {
            if (BoxItemSimpleInfo != null) {
                boxNetAPI.deleteAllItemInFolder(BoxItemSimpleInfo.id);
            }
        }catch(Exception ex2){
            toastMessage(ex2.toString());
        }
    }

    private void toastMessage(final String message){
        RegisterUserActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterUserActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enableControls(final boolean enable){
        RegisterUserActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstName.setEnabled(enable);
                lastName.setEnabled(enable);
                userName.setEnabled(enable);
                password.setEnabled(enable);
                retypePassword.setEnabled(enable);
                takePictureBTN.setEnabled(enable);
                submitBTN.setEnabled(enable);
            }
        });
    }

    private void enableProgress(final boolean enable){
        RegisterUserActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable){
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setEnabled(enable);
                enableControls(!enable);
            }
        });
    }

}

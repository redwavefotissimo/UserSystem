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
import android.widget.TextView;
import android.widget.Toast;

import com.android.personal.usersystem.R;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySQLAPIResponse;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI;
import com.android.personal.usersystem.data.SharedStaticClass;
import com.common.BoxNetAPI.BoxItemInfo;
import com.common.BoxNetAPI.BoxItemSimpleInfo;
import com.common.BoxNetAPI.BoxNetAPI;
import com.common.Utils;

import java.io.File;

public class UserInfoActivity extends AppCompatActivity {

    final String TAG = "UserInfoActivity";

    public static final int requestTakeCamera = 1122;

    TextView userName;
    EditText firstName, lastName, newpassword, retypePassword, oldpassword;
    Button updateBTN, takePictureBTN;
    ProgressBar progressBar;

    String fileLocation;
    Handler handler;
    HandlerThread handlerThread;
    com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI UserSystemMySqlAPI;
    BoxNetAPI boxNetAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

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
            userName = (TextView) this.findViewById(R.id.userName);
            newpassword = (EditText) this.findViewById(R.id.newpassword);
            retypePassword = (EditText) this.findViewById(R.id.retypepassword);
            oldpassword = (EditText) this.findViewById(R.id.oldpassword);

            updateBTN = (Button) this.findViewById(R.id.updateBtn);
            updateBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            enableProgress(true);
                            BoxItemSimpleInfo BoxItemSimpleInfo = null;
                            try {
                                if (boxNetAPI == null) {
                                    boxNetAPI = new BoxNetAPI(UserInfoActivity.this);
                                }

                                if(!oldpassword.getText().toString().isEmpty()) {
                                    UserInfo userInfo = new UserInfo();
                                    userInfo.userName = userName.getText().toString();
                                    userInfo.password = Utils.convertStringToSha512(oldpassword.getText().toString(), UserSystemMySqlAPI.salt);

                                    UserSystemMySQLAPIResponse response = UserSystemMySqlAPI.loginUser(userInfo);
                                    if (response.statusCode  != 200) {
                                        toastMessage(getString(R.string.oldPasswordWrong));
                                        return;
                                    }
                                }

                                if(!newpassword.getText().toString().isEmpty() && retypePassword.getText().toString().isEmpty())
                                {
                                    toastMessage("retype password is required!");
                                    return;
                                }

                                if(newpassword.getText().toString().isEmpty() && !retypePassword.getText().toString().isEmpty())
                                {
                                    toastMessage("new password is required!");
                                    return;
                                }

                                if (!newpassword.getText().toString().equals(retypePassword.getText().toString())
                                        && !newpassword.getText().toString().isEmpty()
                                        && !retypePassword.getText().toString().isEmpty()) {
                                    toastMessage("new password and retype newpassword are not equal");
                                    return;
                                }

                                UserInfo userInfo = new UserInfo();
                                userInfo.recId = SharedStaticClass.userInfo.recId;
                                userInfo.firstName = firstName.getText().toString();
                                userInfo.lastName = lastName.getText().toString();
                                userInfo.userName = userName.getText().toString();

                                userInfo.profileFolderId = SharedStaticClass.userInfo.profileFolderId;
                                userInfo.attachementFolderId = SharedStaticClass.userInfo.attachementFolderId;

                                if(!newpassword.getText().toString().isEmpty()) {
                                    userInfo.password = Utils.convertStringToSha512(newpassword.getText().toString(), UserSystemMySqlAPI.salt);
                                }else{
                                    userInfo.password = SharedStaticClass.userInfo.password;
                                }

                                if(fileLocation != null && !fileLocation.isEmpty()) {
                                    BoxItemInfo itemInfo = boxNetAPI.getItemInfo(SharedStaticClass.userInfo.profileId);

                                    BoxItemSimpleInfo fileForRemoval = new BoxItemSimpleInfo();
                                    fileForRemoval.etag = itemInfo.etag;
                                    fileForRemoval.id = itemInfo.id;

                                    boxNetAPI.deleteItem(fileForRemoval);
                                    userInfo.profileId = boxNetAPI.updloadFile(new File(fileLocation), userInfo.profileFolderId).entries[0].id;
                                    boxNetAPI.setFileItemAsSharable(userInfo.profileId);
                                }
                                else{
                                    userInfo.profileId = SharedStaticClass.userInfo.profileId;
                                }

                                Thread.sleep(1000);

                                UserSystemMySQLAPIResponse UserSystemMySQLAPIResponse = UserSystemMySqlAPI.updateUser(userInfo);
                                toastMessage(UserSystemMySQLAPIResponse.message);

                                if(UserSystemMySQLAPIResponse.statusCode == 200){
                                    SharedStaticClass.userInfo.firstName = userInfo.firstName;
                                    SharedStaticClass.userInfo.lastName = userInfo.lastName;
                                    SharedStaticClass.userInfo.userName = userInfo.userName;
                                    SharedStaticClass.userInfo.profileFolderId = userInfo.profileFolderId;
                                    SharedStaticClass.userInfo.attachementFolderId = userInfo.attachementFolderId;
                                    SharedStaticClass.userInfo.password = userInfo.password;
                                    SharedStaticClass.userInfo.profileId = userInfo.profileId;
                                    fileLocation = "";
                                }
                            } catch (Exception ex) {
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
                    Intent i = new Intent(UserInfoActivity.this, CameraActivity.class);
                    startActivityForResult(i, requestTakeCamera);
                }
            });

            firstName.setText(SharedStaticClass.userInfo.firstName);
            lastName.setText(SharedStaticClass.userInfo.lastName);
            userName.setText(SharedStaticClass.userInfo.userName);

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

    private void toastMessage(final String message){
        UserInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserInfoActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enableControls(final boolean enable){
        UserInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstName.setEnabled(enable);
                lastName.setEnabled(enable);
                userName.setEnabled(enable);
                newpassword.setEnabled(enable);
                retypePassword.setEnabled(enable);
                takePictureBTN.setEnabled(enable);
                updateBTN.setEnabled(enable);
            }
        });
    }

    private void enableProgress(final boolean enable){
        UserInfoActivity.this.runOnUiThread(new Runnable() {
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

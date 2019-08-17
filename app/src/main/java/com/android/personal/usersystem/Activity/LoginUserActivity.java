package com.android.personal.usersystem.Activity;

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

import androidx.appcompat.app.AppCompatActivity;

import com.android.personal.usersystem.R;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySQLAPIResponse;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI;
import com.android.personal.usersystem.data.SharedStaticClass;
import com.common.Utils;

public class LoginUserActivity extends AppCompatActivity {

    final String TAG = "LoginUserActivity";


    EditText userName, password;
    Button loginBTN, registerBTN;
    ProgressBar progressBar;

    Handler handler;
    HandlerThread handlerThread;
    com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI UserSystemMySqlAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

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

            progressBar = (ProgressBar) this.findViewById(R.id.loading);

            userName = (EditText) this.findViewById(R.id.username);
            password = (EditText) this.findViewById(R.id.password);

            loginBTN = (Button) this.findViewById(R.id.login);
            loginBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            enableProgress(true);

                            try {
                                UserInfo userInfo = new UserInfo();
                                userInfo.userName = userName.getText().toString();
                                userInfo.password = Utils.convertStringToSha512(password.getText().toString(), UserSystemMySqlAPI.salt);
                                UserSystemMySQLAPIResponse response = UserSystemMySqlAPI.loginUser(userInfo);
                                if (response.statusCode == 200) {

                                    Object data = UserSystemMySqlAPI.getUserInfo(userInfo);

                                    if(data instanceof UserSystemMySQLAPIResponse){
                                        toastMessage(((UserSystemMySQLAPIResponse) data).message);
                                    }else{
                                        SharedStaticClass.userInfo = (UserInfo) data;
                                        Intent i = new Intent(LoginUserActivity.this, UserInfoActivity.class);
                                        startActivity(i);
                                        toastMessage(response.message);
                                    }
                                }else{
                                    toastMessage(getString(R.string.loginFailed));
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

            registerBTN = (Button) this.findViewById(R.id.register);
            registerBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(LoginUserActivity.this, RegisterUserActivity.class);
                    startActivity(i);
                }
            });
        }
        catch(Exception e){
            toastMessage(e.toString());
        }
    }


    private void toastMessage(final String message){
        LoginUserActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginUserActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enableControls(final boolean enable){
        LoginUserActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userName.setEnabled(enable);
                password.setEnabled(enable);
                loginBTN.setEnabled(enable);
                registerBTN.setEnabled(enable);
            }
        });
    }

    private void enableProgress(final boolean enable){
        LoginUserActivity.this.runOnUiThread(new Runnable() {
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

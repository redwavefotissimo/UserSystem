package com.android.personal.usersystem.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.ProgressBar;

import com.android.personal.usersystem.R;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserAttachmentInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserAttachmentListInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySQLAPIResponse;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI;
import com.android.personal.usersystem.data.SharedStaticClass;
import com.common.BoxNetAPI.BoxNetAPI;

public class UserAttachmentsActivity extends BaseActivity {

    ProgressBar progressBar;

    Handler handler;
    HandlerThread handlerThread;
    com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI UserSystemMySqlAPI;

    BoxNetAPI boxNetAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attachments);

        TAG = "UserAttachmentsActivity";

        try {
            handlerThread = new HandlerThread("backgroundThread");
            handlerThread.start();

            handler = new Handler(handlerThread.getLooper());
            UserSystemMySqlAPI = new UserSystemMySqlAPI(this);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        UserAttachmentInfo UserAttachmentInfo = new UserAttachmentInfo();
                        UserAttachmentInfo.userRecId = SharedStaticClass.userInfo.recId;
                        Object data = UserSystemMySqlAPI.getUserAttachmentInfo(UserAttachmentInfo);
                        if(data instanceof UserSystemMySQLAPIResponse){
                            UserSystemMySQLAPIResponse UserSystemMySQLAPIResponse = (UserSystemMySQLAPIResponse) data;
                            if(UserSystemMySQLAPIResponse.statusCode != 200){
                                toastMessage(UserSystemMySQLAPIResponse.message);
                            }
                        }else{
                            UserAttachmentListInfo UserAttachmentListInfo = (UserAttachmentListInfo)  data;
                        }
                    }catch(Exception ex){
                        toastMessage(ex.toString());
                    }
                }
            });
        }
        catch(Exception e){
            toastMessage(e.toString());
        }
    }
}

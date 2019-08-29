package com.android.personal.usersystem.Activity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ProgressBar;
import com.android.personal.usersystem.Adapter.UserAttachmentRowAdapter;
import com.android.personal.usersystem.R;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserAttachmentInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserAttachmentListInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySQLAPIResponse;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI;
import com.android.personal.usersystem.data.SharedStaticClass;
import com.common.BoxNetAPI.BoxItemInfo;
import com.common.BoxNetAPI.BoxNetAPI;

public class UserAttachmentsActivity extends BaseActivity {

    ProgressBar progressBar;

    Handler handler;
    HandlerThread handlerThread;
    UserSystemMySqlAPI UserSystemMySqlAPI;
    BoxNetAPI boxNetAPI;

    UserAttachmentRowAdapter userAttachmentRowAdapter;

    RecyclerView userAttachmentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attachments);

        TAG = "UserAttachmentsActivity";

        try {

            userAttachmentRecyclerView = (RecyclerView) this.findViewById(R.id.userAttachmentRecyclerView);

            progressBar = (ProgressBar) this.findViewById(R.id.progressBar);

            handlerThread = new HandlerThread("backgroundThread");
            handlerThread.start();

            handler = new Handler(handlerThread.getLooper());
            UserSystemMySqlAPI = new UserSystemMySqlAPI(this);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        enableProgress(true);
                        if(boxNetAPI == null){
                            boxNetAPI = new BoxNetAPI(UserAttachmentsActivity.this);
                        }

                        UserAttachmentInfo UserAttachmentInfo = new UserAttachmentInfo();
                        UserAttachmentInfo.userRecId = SharedStaticClass.userInfo.recId;
                        Object data = UserSystemMySqlAPI.getUserAttachmentInfo(UserAttachmentInfo);
                        if(data instanceof UserSystemMySQLAPIResponse){
                            UserSystemMySQLAPIResponse UserSystemMySQLAPIResponse = (UserSystemMySQLAPIResponse) data;
                            if(UserSystemMySQLAPIResponse.statusCode != 200){
                                toastMessage(UserSystemMySQLAPIResponse.message);
                            }
                        }else{
                            final UserAttachmentListInfo UserAttachmentListInfo = (UserAttachmentListInfo)  data;

                            for(UserAttachmentInfo info : UserAttachmentListInfo.UserAttachmentInfo){
                                BoxItemInfo boxItemInfo = boxNetAPI.getItemInfo(info.attachmentId);
                                info.attachmentName = boxItemInfo.name;
                                if(boxItemInfo.shared_link != null){
                                    info.attachmentLink = boxItemInfo.shared_link.download_url;
                                }
                            }

                            UserAttachmentsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    userAttachmentRowAdapter = new UserAttachmentRowAdapter(UserAttachmentListInfo.UserAttachmentInfo, UserAttachmentsActivity.this);
                                    userAttachmentRecyclerView.setHasFixedSize(true);
                                    userAttachmentRecyclerView.setLayoutManager(new LinearLayoutManager(UserAttachmentsActivity.this));
                                    userAttachmentRecyclerView.setAdapter(userAttachmentRowAdapter);
                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userAttachmentRecyclerView.getContext(),
                                            DividerItemDecoration.HORIZONTAL);
                                    userAttachmentRecyclerView.addItemDecoration(dividerItemDecoration);
                                    userAttachmentRowAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }catch(Exception ex){
                        toastMessage(ex.toString());
                    }
                    finally{
                        enableProgress(false);
                    }
                }
            });
        }
        catch(Exception e){
            toastMessage(e.toString());
        }
    }

    private void enableControls(final boolean enable){
        UserAttachmentsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void enableProgress(final boolean enable){
        UserAttachmentsActivity.this.runOnUiThread(new Runnable() {
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

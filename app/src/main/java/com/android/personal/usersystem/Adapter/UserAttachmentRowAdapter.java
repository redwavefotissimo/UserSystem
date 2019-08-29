package com.android.personal.usersystem.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.personal.usersystem.R;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserAttachmentInfo;

import java.util.List;

public class UserAttachmentRowAdapter extends RecyclerView.Adapter<UserAttachmentRowAdapter.MyViewHolder> {

    private List<UserAttachmentInfo> userAttachmentInfoList;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;

        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
        }
    }

    public UserAttachmentRowAdapter(List<UserAttachmentInfo> userAttachmentInfoList, Activity activity) {
        this.userAttachmentInfoList = userAttachmentInfoList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_attachment_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UserAttachmentInfo attachment = userAttachmentInfoList.get(position);
        holder.itemName.setText(attachment.attachmentName);
        holder.itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(attachment.attachmentLink != null && !attachment.attachmentLink.isEmpty()){
                    Uri uri = Uri.parse(attachment.attachmentLink); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAttachmentInfoList.size();
    }
}

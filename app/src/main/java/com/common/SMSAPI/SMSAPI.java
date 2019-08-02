package com.common.SMSAPI;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPIBasic;

import java.util.ArrayList;

public class SMSAPI {

    final String TAG = "SMSAPI";

    final String fromRecipient = "UserSystem";
    final String APIUSerName = "APIL4JMR98TRJ";
    final String APIPassword = "APIL4JMR98TRJL4JMR";
    final String MTURL = "http://gateway.onewaysms.hk:10002/api.aspx";

    public void send(String messageContent, String toRecipient) throws Exception{

        if(!toRecipient.startsWith("852")){
            toRecipient = "852" + toRecipient;
        }

        RestAPIBasic RestAPIBasic = new RestAPIBasic();

        ArrayList<RestAPIInfo> RestAPIInfos = new ArrayList<RestAPIInfo>();

        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "apiusername";
        restAPIInfo.fieldData = APIUSerName;
        RestAPIInfos.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "apipassword";
        restAPIInfo.fieldData = APIPassword;
        RestAPIInfos.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "mobileno";
        restAPIInfo.fieldData = toRecipient;
        RestAPIInfos.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "senderid";
        restAPIInfo.fieldData = fromRecipient;
        RestAPIInfos.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "languagetype";
        restAPIInfo.fieldData = "1";
        RestAPIInfos.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "message";
        restAPIInfo.fieldData = messageContent;
        RestAPIInfos.add(restAPIInfo);

        String reqResponseString = RestAPIBasic.GET(MTURL, RestAPIInfos);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }
    }

    public String read(Context context){

        String message = "";
        Uri SMSInbox = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(SMSInbox, null, "read = 0", null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {

                if(cursor.getString(cursor.getColumnIndexOrThrow("address")).toString().equals(fromRecipient)){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Exception while sleeping markSmsAsReadThread: " + e.getMessage());
                    }

                    message = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();

                    // cannot mark as read since kitkat (4.4)
                    break;
                }
            } while (cursor.moveToNext());
        }

        return message;
    }
}

package com.android.personal.usersystem.UserSystemMySqlAPI;

import android.app.Activity;

import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPIBasicJson;
import com.common.Utils;

import java.util.ArrayList;

public class UserSystemMySqlAPI {

    final String baseURI = "http://usersystem.ap-east-1.elasticbeanstalk.com/RestAPI/";
    public final String salt = "@#$6salt%^*";

    RestAPIBasicJson RestAPIBasicJson;

    public UserSystemMySqlAPI(Activity activity) throws Exception{
        RestAPIBasicJson = new RestAPIBasicJson();
        //RestAPISSLJson.setCustomCert(activity, "t1.crt");
    }

    public UserSystemMySQLAPIResponse insertUser(UserInfo userInfo) throws Exception{
        return basicPostToUserSystemMySQLAPI(userInfo, "InsertUser.jsp");
    }

    public UserSystemMySQLAPIResponse updateUser(UserInfo userInfo) throws Exception{
        return basicPostToUserSystemMySQLAPI(userInfo, "UpdateUser.jsp");
    }

    public UserSystemMySQLAPIResponse loginUser(UserInfo userInfo) throws Exception{
        return basicPostToUserSystemMySQLAPI(userInfo, "LoginUser.jsp");
    }

    public Object getUserInfo(UserInfo userInfo) throws Exception{
        ArrayList<RestAPIInfo> restAPIInfos = new ArrayList<RestAPIInfo>();
        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldData = Utils.objectToJsonString(userInfo);
        restAPIInfos.add(restAPIInfo);

        String reqResponseString = RestAPIBasicJson.POST(baseURI + "GetUserInfo.jsp", restAPIInfos);

        if(reqResponseString.startsWith("ERROR:")){
            reqResponseString = reqResponseString.replaceFirst("ERROR:", "");
            return (UserSystemMySQLAPIResponse) Utils.JsonStringToObject(reqResponseString, UserSystemMySQLAPIResponse.class);
        }
        else{
            return (UserInfo) Utils.JsonStringToObject(reqResponseString, UserInfo.class);
        }
    }

    public UserSystemMySQLAPIResponse insertUserAttachment(UserAttachmentInfo userAttachmentInfo) throws Exception{
        return basicPostToUserSystemMySQLAPI(userAttachmentInfo, "InsertUserAttachment.jsp");
    }

    public Object getUserAttachmentInfo(UserAttachmentInfo userAttachmentInfo) throws Exception{
        ArrayList<RestAPIInfo> restAPIInfos = new ArrayList<RestAPIInfo>();
        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldData = Utils.objectToJsonString(userAttachmentInfo);
        restAPIInfos.add(restAPIInfo);

        String reqResponseString = RestAPIBasicJson.POST(baseURI + "GetUserAttachments.jsp", restAPIInfos);

        if(reqResponseString.startsWith("ERROR:")){
            reqResponseString = reqResponseString.replaceFirst("ERROR:", "");
            return (UserSystemMySQLAPIResponse) Utils.JsonStringToObject(reqResponseString, UserSystemMySQLAPIResponse.class);
        }
        else{
            return (UserAttachmentListInfo) Utils.JsonStringToObject(reqResponseString, UserAttachmentListInfo.class);
        }
    }

    private UserSystemMySQLAPIResponse basicPostToUserSystemMySQLAPI(Object objectClass, String restAPILocation) throws Exception {
        ArrayList<RestAPIInfo> restAPIInfos = new ArrayList<RestAPIInfo>();
        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldData = Utils.objectToJsonString(objectClass);
        restAPIInfos.add(restAPIInfo);

        String reqResponseString = RestAPIBasicJson.POST(baseURI + restAPILocation, restAPIInfos);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        return (UserSystemMySQLAPIResponse) Utils.JsonStringToObject(reqResponseString, UserSystemMySQLAPIResponse.class);
    }

}

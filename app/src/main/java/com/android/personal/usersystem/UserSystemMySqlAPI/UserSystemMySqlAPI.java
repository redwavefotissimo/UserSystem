package com.android.personal.usersystem.UserSystemMySqlAPI;

import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPIBasicJson;
import com.common.Utils;

import java.util.ArrayList;

public class UserSystemMySqlAPI {

    final String baseURI = "http://usersystem.ap-east-1.elasticbeanstalk.com/";
    public final String salt = "@#$6salt%^*";

    RestAPIBasicJson RestAPIBasicJson;

    public UserSystemMySqlAPI(){
        RestAPIBasicJson = new RestAPIBasicJson();
    }

    public UserSystemMySQLAPIResponse insertUser(UserInfo userInfo) throws Exception{

        ArrayList<RestAPIInfo> restAPIInfos = new ArrayList<RestAPIInfo>();
        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldData = Utils.objectToJsonString(userInfo);
        restAPIInfos.add(restAPIInfo);

        String reqResponseString = RestAPIBasicJson.POST(baseURI + "InsertUser.jsp", restAPIInfos);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        return (UserSystemMySQLAPIResponse) Utils.JsonStringToObject(reqResponseString, UserSystemMySQLAPIResponse.class);
    }
}

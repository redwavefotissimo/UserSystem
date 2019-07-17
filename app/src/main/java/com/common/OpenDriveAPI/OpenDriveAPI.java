package com.common.OpenDriveAPI;

import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPISSLJson;
import com.common.Utils;

import java.io.File;
import java.util.ArrayList;

public class OpenDriveAPI {

    private final String APIBaseURI = "https://dev.opendrive.com/api/v1/";

    ODLoginRequestInfo loginRequestInfo;
    ODLoginResponseInfo loginResponseInfo;
    ODFolderResponseInfo folderResponseInfo;

    RestAPISSLJson RestAPISSLJson;

    public OpenDriveAPI(String userID, String password){
        RestAPISSLJson = new RestAPISSLJson();

        loginRequestInfo = new ODLoginRequestInfo();
        loginRequestInfo.username = userID;
        loginRequestInfo.passwd = password;
    }

    public ODLoginResponseInfo getLoginResponseInfo(){
        return loginResponseInfo;
    }

    private ArrayList<RestAPIInfo> setupJsonDataObject(String jsonString){
        RestAPIInfo RestAPIInfo = new RestAPIInfo();
        RestAPIInfo.fieldData = jsonString;

        ArrayList<RestAPIInfo> RestAPIInfos = new ArrayList<>();

        RestAPIInfos.add(RestAPIInfo);

        return RestAPIInfos;
    }

    public void getSessionID() throws Exception{
        String reqResponseString = RestAPISSLJson.POST(this.APIBaseURI + "session/login.json",
                setupJsonDataObject(Utils.objectToJsonString(this.loginRequestInfo)));

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        loginResponseInfo = (ODLoginResponseInfo) Utils.JsonStringToObject(reqResponseString, ODLoginResponseInfo.class);
    }

    public ODFolderListResponseInfo getFolderFromRoot() throws Exception{
        return getFolderList("0");
    }

    public ODFolderListResponseInfo getFolderList(String fromFolder) throws Exception{
        String reqResponseString = RestAPISSLJson.GET(APIBaseURI + "folder/list.json/" + loginResponseInfo.SessionID + "/" + fromFolder, null);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        return (ODFolderListResponseInfo) Utils.JsonStringToObject(reqResponseString, ODFolderListResponseInfo.class);
    }

    public void getDocumentsFolder() throws Exception{
        for(ODFolderResponseInfo folderInfo : getFolderFromRoot().Folders){
            if(folderInfo.Name.equals("Documents")){
                folderResponseInfo = folderInfo;
                break;
            }
        }

        if(folderResponseInfo == null){
            throw new Exception("Folder Not Found in OpenDrive: Documents");
        }
    }

    public void UploadFile(File fileToUpload, String folderId) throws Exception{

        ODFileCreateRequestInfo fileCreateRequestInfo = new ODFileCreateRequestInfo();
        fileCreateRequestInfo.session_id = loginResponseInfo.SessionID;
        fileCreateRequestInfo.folder_id = folderResponseInfo.FolderID;
        fileCreateRequestInfo.file_name = fileToUpload.getName();

        String reqResponseString = RestAPISSLJson.POST(this.APIBaseURI + "upload/create_file.json",
                setupJsonDataObject(Utils.objectToJsonString(fileCreateRequestInfo)));

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        ODFileCreateResponseInfo fileCreateResponseInfo = (ODFileCreateResponseInfo) Utils.JsonStringToObject(reqResponseString, ODFileCreateResponseInfo.class);

        if(fileCreateResponseInfo.RequireHashOnly != 1){
            //TODO: upload logic
        }

        ODCloseFileUploadRequestInfo closeFileUploadRequestInfo = new ODCloseFileUploadRequestInfo();

        reqResponseString = RestAPISSLJson.POST(this.APIBaseURI + "upload/close_file_upload.json",
                setupJsonDataObject(Utils.objectToJsonString(closeFileUploadRequestInfo)));

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        ODCloseFileUploadResponseInfo closeFileUploadResponseInfo = (ODCloseFileUploadResponseInfo) Utils.JsonStringToObject(reqResponseString, ODCloseFileUploadResponseInfo.class);
    }


}

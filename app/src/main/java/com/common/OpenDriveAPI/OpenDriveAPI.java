package com.common.OpenDriveAPI;

import com.common.AbstractOrInterface.RestAPI;
import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPISSL;
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
    RestAPISSL RestAPISSL;

    public OpenDriveAPI(String userID, String password){
        RestAPISSLJson = new RestAPISSLJson();
        RestAPISSL = new RestAPISSL();

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

    private void getFolder(String folderName) throws Exception{
        for(ODFolderResponseInfo folderInfo : getFolderFromRoot().Folders){
            if(folderInfo.Name.equals(folderName)){
                folderResponseInfo = folderInfo;
                break;
            }
        }

        if(folderResponseInfo == null){
            throw new Exception("Folder Not Found in OpenDrive: Documents");
        }
    }

    public void getDocumentsFolder() throws Exception{
        this.getFolder("Documents");
    }

    public void getPublicFolder() throws Exception{
        this.getFolder("Public Folder");
    }

    public void uploadFile(File fileToUpload) throws Exception{

        ODFileCreateRequestInfo fileCreateRequestInfo = new ODFileCreateRequestInfo();
        fileCreateRequestInfo.session_id = loginResponseInfo.SessionID;
        fileCreateRequestInfo.folder_id = folderResponseInfo.FolderID;
        fileCreateRequestInfo.file_name = fileToUpload.getName();
        fileCreateRequestInfo.file_size = fileToUpload.length();
        fileCreateRequestInfo.file_hash = Utils.getFileMD5Hash(fileToUpload.getAbsolutePath());
        //fileCreateRequestInfo.access_folder_id = folderResponseInfo.getAccessFolderId();

        String reqResponseString = RestAPISSLJson.POST(this.APIBaseURI + "upload/create_file.json",
                setupJsonDataObject(Utils.objectToJsonString(fileCreateRequestInfo)));

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        ODFileCreateResponseInfo fileCreateResponseInfo = (ODFileCreateResponseInfo) Utils.JsonStringToObject(reqResponseString, ODFileCreateResponseInfo.class);

        if(fileCreateResponseInfo.RequireHashOnly != 1){

            ODOpenFileUploadRequestInfo openFileUploadRequestInfo = new ODOpenFileUploadRequestInfo();
            openFileUploadRequestInfo.session_id = loginResponseInfo.SessionID;
            openFileUploadRequestInfo.file_id = fileCreateResponseInfo.FileId;
            openFileUploadRequestInfo.file_size = fileToUpload.length();
            openFileUploadRequestInfo.file_hash = fileCreateRequestInfo.file_hash;
            //openFileUploadRequestInfo.access_folder_id = folderResponseInfo.getAccessFolderId();

            reqResponseString = RestAPISSLJson.POST(this.APIBaseURI + "upload/open_file_upload.json",
                    setupJsonDataObject(Utils.objectToJsonString(openFileUploadRequestInfo)));

            if(reqResponseString.startsWith("ERROR:")){
                throw new Exception(reqResponseString);
            }

            ODOpenFileUploadResponseInfo openFileUploadResponseInfo = (ODOpenFileUploadResponseInfo) Utils.JsonStringToObject(reqResponseString, ODOpenFileUploadResponseInfo.class);

            ArrayList<RestAPIInfo> restAPIInfos = new ArrayList<RestAPIInfo>();
            RestAPIInfo RestAPIInfo = new RestAPIInfo();
            RestAPIInfo.fieldName = "session_id";
            RestAPIInfo.fieldData = loginResponseInfo.SessionID;
            restAPIInfos.add(RestAPIInfo);

            RestAPIInfo = new RestAPIInfo();
            RestAPIInfo.fieldName = "file_id";
            RestAPIInfo.fieldData = fileCreateResponseInfo.FileId;
            restAPIInfos.add(RestAPIInfo);

            RestAPIInfo = new RestAPIInfo();
            RestAPIInfo.fieldName = "temp_location";
            RestAPIInfo.fieldData = openFileUploadResponseInfo.TempLocation;
            restAPIInfos.add(RestAPIInfo);

            RestAPIInfo = new RestAPIInfo();
            RestAPIInfo.fieldName = "chunk_offset";
            RestAPIInfo.fieldData = "0";
            restAPIInfos.add(RestAPIInfo);

            RestAPIInfo = new RestAPIInfo();
            RestAPIInfo.fieldName = "chunk_size";
            RestAPIInfo.fieldData = String.valueOf(fileToUpload.length());
            restAPIInfos.add(RestAPIInfo);

            RestAPIInfo = new RestAPIInfo();
            RestAPIInfo.fieldName = "file_data";
            RestAPIInfo.fieldData = fileToUpload.getAbsolutePath();
            RestAPIInfo.isFile = true;
            restAPIInfos.add(RestAPIInfo);

            reqResponseString = RestAPISSL.POST(this.APIBaseURI + "upload/upload_file_chunk.json", restAPIInfos);

            if(reqResponseString.startsWith("ERROR:")){
                throw new Exception(reqResponseString);
            }
        }

        ODCloseFileUploadRequestInfo closeFileUploadRequestInfo = new ODCloseFileUploadRequestInfo();
        closeFileUploadRequestInfo.session_id = loginResponseInfo.SessionID;
        closeFileUploadRequestInfo.file_id = fileCreateResponseInfo.FileId;
        closeFileUploadRequestInfo.file_size = fileToUpload.length();
        closeFileUploadRequestInfo.temp_location = fileCreateResponseInfo.TempLocation;
        closeFileUploadRequestInfo.file_hash = fileCreateRequestInfo.file_hash;
        closeFileUploadRequestInfo.file_time = fileToUpload.lastModified();
        //closeFileUploadRequestInfo.access_folder_id = folderResponseInfo.getAccessFolderId();

        reqResponseString = RestAPISSLJson.POST(this.APIBaseURI + "upload/close_file_upload.json",
                setupJsonDataObject(Utils.objectToJsonString(closeFileUploadRequestInfo)));

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        ODCloseFileUploadResponseInfo closeFileUploadResponseInfo = (ODCloseFileUploadResponseInfo) Utils.JsonStringToObject(reqResponseString, ODCloseFileUploadResponseInfo.class);

        if(closeFileUploadResponseInfo.Name.isEmpty()) {
            throw new Exception("File Not Uploaded: " + fileCreateRequestInfo.file_name);
        }
    }


}

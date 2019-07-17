package com.common.OpenDriveAPI;

public class ODLoginResponseInfo {

    public String SessionID;
    public String UserName;
    public String UserFirstName;
    public String UserLastName;
    public String AccType;
    public String UserLang;
    public String UserID;
    public int IsAccountUser;
    public String DriveName;
    public String UserLevel;
    public String UserPlan;
    public String FVersioning;
    public String UserDomain;
    public String PartnerUsersDomain;
    public long UploadSpeedLimit;
    public long DownloadSpeedLimit;
    public int UploadsPerSecond;
    public int DownloadsPerSecond;

    public String getAccType(){
        if(AccType.equals("1")){
            return "personal";
        }else{
            return "business";
        }
    }

    public String getUserLang(){
        UserLang = UserLang.toLowerCase();
        if(UserLang.equals("en")){
            return "English";
        }else if(UserLang.equals("es")){
            return "Spanish";
        }else if(UserLang.equals("pt")){
            return "Portuguese";
        }else if(UserLang.equals("de")){
            return "German";
        }else if(UserLang.equals("fr")){
            return "French";
        }else if(UserLang.equals("zhs")){
            return "Simplified Chinese";
        }else if(UserLang.equals("zht")){
            return "Traditional Chinese";
        }else if(UserLang.equals("cz")){
            return "Czech";
        }else if(UserLang.equals("hu")){
            return "Hungarian";
        }else if(UserLang.equals("nl")){
            return "Dutch";
        }else if(UserLang.equals("pl")){
            return "Polish";
        }else if(UserLang.equals("ru")){
            return "Russian";
        }else {
            return "Slovak";
        }
    }
}

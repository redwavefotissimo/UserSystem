package com.common.OpenDriveAPI;

public class ODFolderResponseInfo {
    public String FolderID;
    public String Name;
    public long DateCreated;
    public long DirUpdateTime;
    public int Access;
    public long DateModified;
    public String Shared;
    public String ChildFolders;
    public String Link;
    public String Encrypted;

    public String getAccessFolderId(){
        switch (this.Access){
            case 2:
                return "Hidden";
            case 3:
                return "Private";
            default:
                return "Public";
        }
    }
}

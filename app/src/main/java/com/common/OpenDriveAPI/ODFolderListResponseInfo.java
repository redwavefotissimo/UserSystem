package com.common.OpenDriveAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class ODFolderListResponseInfo {

    public long DirUpdateTime;
    public String Name;
    public String ParentFolderID;
    public String DirectFolderLink;
    public String ResponseType;
    public ODFolderResponseInfo[] Folders;
    public ODFileResponseInfo[] Files;
}

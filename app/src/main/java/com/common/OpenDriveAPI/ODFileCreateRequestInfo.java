package com.common.OpenDriveAPI;

public class ODFileCreateRequestInfo {

    public String session_id;
    public String folder_id;
    public String file_name;
    public String file_description;
    public String access_folder_id;
    public long file_size;
    public String file_hash;
    public String sharing_id;
    public int open_if_exists = 1;
}

package com.common.BoxNetAPI;

public class BoxItemInfo {
    public String type;
    public String id;
    public String sequence_id;
    public String etag;
    public String name;
    public String sha1;
    public String created_at;
    public String modified_at;
    public String description;
    public long size;
    public BoxItemCollectionInfo path_collection;
    public BoxUserInfo created_by;
    public BoxUserInfo modified_by;
    public String trashed_at;
    public String purged_at;
    public String content_created_at;
    public String content_modified_at;
    public BoxUserInfo owned_by;
    public BoxSharedLinkInfo shared_link;
    public String folder_upload_email;
    public BoxItemSimpleInfo parent;
    public String item_status;
    public BoxItemCollectionInfo item_collection;
    public BoxFileVersionInfo file_version;
}

package com.jothub.pojo;

@lombok.Data
public class Folders {
    private int folderId;        // 文件夹ID
    private String folderName;   // 文件夹名称
    private int parentFolderId;  // 上级文件夹ID，可能为null
    private java.sql.Timestamp createdAt;   // 文件夹创建时间
    private java.sql.Timestamp updatedAt;   // 文件夹更新时间
    private String type;
}

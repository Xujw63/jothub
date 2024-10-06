package com.jothub.pojo;

@lombok.Data
public class Files {
    private int fileId;             // 文件ID
    private String fileName;        // 文件名
    private String filePath;        // 文件存储路径
    private long fileSize;          // 文件大小
    private String fileType;        // 文件类型
    private java.sql.Timestamp createdAt;   // 文件创建时间
    private java.sql.Timestamp updatedAt;   // 文件更新时间
    private int folderId;       // 文件所属文件夹ID，可能为null
    private String type;
}

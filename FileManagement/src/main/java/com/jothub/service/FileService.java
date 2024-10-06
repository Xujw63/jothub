package com.jothub.service;

import com.jothub.pojo.Files;

import java.util.List;

public interface FileService {
    void fileUpload(String fileName, long fileSize, String fileType, String filePath, int folderId);

    List<Files> fileGetByAttributes(int fileId, String fileName, String fileType, int folderId);

    void fileDeleteByIds(List<Integer> ids);

    void fileUpdate(int fileId, String fileName, int folderId);

    Files getFileById(int fileId);

    List<String> getFilePathsByIds(List<Integer> ids);
}

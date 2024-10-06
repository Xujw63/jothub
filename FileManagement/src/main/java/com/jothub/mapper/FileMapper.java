package com.jothub.mapper;

import com.jothub.pojo.Files;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FileMapper {


    void fileUpload(String fileName,  long fileSize,  String fileType,  String filePath, int folderId);

    List<Files> fileGetByAttributes(Integer fileId,  String fileName,  String fileType,  Integer folderId);

    void fileDeleteByIds(List<Integer> ids);

    void fileUpdate(Integer fileId,  String fileName,  Integer folderId);

    Files getFileById(int fileId);

    List<String> getFilePathsByIds(List<Integer> ids);

    List<Integer> getFileIdsByFolderIds(List<Integer> folderIdsToDelete);
}


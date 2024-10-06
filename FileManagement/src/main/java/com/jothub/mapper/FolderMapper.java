package com.jothub.mapper;

import com.jothub.pojo.Folders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FolderMapper {
    void folderCreate(String folderName, int parentFolderId);

    void folderUpdate(int folderId, String folderName, int parentFolderId);

    void folderDelete(List<Integer> ids);

    List<Folders> folderGetByIds(List<Integer> ids);

    List<Folders> folderGetByParentFolderId(int parentFolderId);

    List<Integer> getSubfolderIdsByParentId(int parentFolderId);
}

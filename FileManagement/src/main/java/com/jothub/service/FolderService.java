package com.jothub.service;

import com.jothub.pojo.Folders;

import java.util.List;

public interface FolderService {
    void folderCreate(String folderName, int parentFolderId);
    List<Folders> folderGetByIds(List<Integer> ids);
    void folderUpdate(int folderId, String folderName, int parentFolderId);
    List<Folders> folderGetByParentFolderId(int parentFolderId);

    // 递归获取文件夹内的所有子文件夹ID
    List<Integer> getAllSubfolderIds(int parentFolderId);

    // 删除文件夹及其子文件夹中的文件
    void deleteFolderAndFiles(List<Integer> ids);
}

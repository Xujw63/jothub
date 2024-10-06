package com.jothub.service.impl;

import com.jothub.mapper.FolderMapper;
import com.jothub.mapper.FileMapper;
import com.jothub.pojo.Folders;
import com.jothub.service.FolderService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    final
    FolderMapper folderMapper;
    FileMapper fileMapper;

    public FolderServiceImpl(FolderMapper folderMapper, FileMapper fileMapper) {
        this.folderMapper = folderMapper;
        this.fileMapper = fileMapper;
    }

    @Override
    public void folderCreate(String folderName, int parentFolderId) {
        folderMapper.folderCreate(folderName, parentFolderId);
    }

    @Override
    public List<Folders> folderGetByIds(List<Integer> ids) {
        return folderMapper.folderGetByIds(ids);
    }


    @Override
    public void folderUpdate(int folderId, String folderName, int parentFolderId) {
        folderMapper.folderUpdate(folderId, folderName, parentFolderId);
    }

    @Override
    public List<Folders> folderGetByParentFolderId(int parentFolderId) {
        return folderMapper.folderGetByParentFolderId(parentFolderId);
    }

    // 递归获取文件夹内的所有子文件夹ID
    @Override
    public List<Integer> getAllSubfolderIds(int parentFolderId) {
        List<Integer> allFolderIds = new ArrayList<>();
        List<Integer> subfolderIds = folderMapper.getSubfolderIdsByParentId(parentFolderId);

        // 如果子文件夹存在，递归获取其子文件夹
        for (Integer subfolderId : subfolderIds) {
            allFolderIds.add(subfolderId);
            allFolderIds.addAll(getAllSubfolderIds(subfolderId));
        }
        return allFolderIds;
    }

    // 删除文件夹及其子文件夹中的文件
    @Override
    public void deleteFolderAndFiles(List<Integer> ids) {

        List<Integer> folderIdsToDelete = new ArrayList<>();

        for(int id : ids) {
            folderIdsToDelete.addAll(getAllSubfolderIds(id));
        }
        folderIdsToDelete.addAll(ids);// 包含当前文件夹

        // 获取所有需要删除的文件
        List<Integer> fileIdsToDelete = fileMapper.getFileIdsByFolderIds(folderIdsToDelete);

        // 删除文件记录和文件物理存储
        for (String filePath : fileMapper.getFilePathsByIds(fileIdsToDelete)) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }

        // 从数据库删除文件夹记录
        folderMapper.folderDelete(ids);
    }
}

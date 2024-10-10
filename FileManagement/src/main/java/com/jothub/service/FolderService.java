package com.jothub.service;

import com.jothub.pojo.Folders;
import java.util.List;

public interface FolderService {

    /**
     * 创建新文件夹
     *
     * @param folderName 新文件夹的名称
     * @param parentFolderId 新文件夹的父文件夹ID，0表示根目录
     */
    void folderCreate(String folderName, int parentFolderId);

    /**
     * 根据文件夹ID列表获取文件夹信息
     *
     * @param ids 文件夹ID列表
     * @return 匹配ID的文件夹列表
     */
    List<Folders> folderGetByIds(List<Integer> ids);

    /**
     * 更新文件夹信息
     *
     * @param folderId 要更新的文件夹ID
     * @param folderName 新文件夹名称，可选参数
     * @param parentFolderId 新父文件夹ID，可选参数
     */
    void folderUpdate(int folderId, String folderName, int parentFolderId);

    /**
     * 根据父文件夹ID获取子文件夹信息
     *
     * @param parentFolderId 父文件夹ID
     * @return 匹配父文件夹ID的子文件夹列表
     */
    List<Folders> folderGetByParentFolderId(int parentFolderId);

    /**
     * 递归获取文件夹内的所有子文件夹ID
     *
     * @param parentFolderId 父文件夹ID
     * @return 包含所有子文件夹ID的列表
     */
    List<Integer> getAllSubfolderIds(int parentFolderId);

    /**
     * 在数据库和服务器删除文件夹及其子文件夹中的文件
     *
     * @param ids 要删除的文件夹ID列表
     */
    void deleteFolderAndFiles(List<Integer> ids);
}

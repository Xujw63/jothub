package com.jothub.service;

import com.jothub.pojo.Files;
import java.util.List;

public interface FileService {

    /**
     * 上传文件
     *
     * @param fileName 文件名
     * @param fileSize 文件大小（字节）
     * @param fileType 文件类型（扩展名）
     * @param filePath 文件在服务器上的存储路径
     * @param folderId 文件所属的文件夹ID
     */
    void fileUpload(String fileName, long fileSize, String fileType, String filePath, int folderId);

    /**
     * 根据文件属性查询文件列表
     *
     * @param fileId 文件ID，可选参数
     * @param fileName 文件名，可选参数
     * @param fileType 文件类型，可选参数
     * @param folderId 文件夹ID，可选参数
     * @return 匹配条件的文件列表
     */
    List<Files> fileGetByAttributes(int fileId, String fileName, String fileType, int folderId);

    /**
     * 根据文件ID列表删除多个文件
     *
     * @param ids 要删除的文件ID列表
     */
    void fileDeleteByIds(List<Integer> ids);

    /**
     * 更新文件信息
     *
     * @param fileId 文件ID
     * @param fileName 新文件名，可选参数
     * @param folderId 新父文件夹ID，可选参数
     */
    void fileUpdate(int fileId, String fileName, int folderId);

    /**
     * 根据文件ID获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息对象
     */
    Files getFileById(int fileId);

    /**
     * 根据文件ID列表获取文件在服务器上的物理路径
     *
     * @param ids 要查询的文件ID列表
     * @return 文件的物理路径列表
     */
    List<String> getFilePathsByIds(List<Integer> ids);
}

package com.jothub.service.impl;

import com.jothub.pojo.Files;
import com.jothub.service.FileService;
import org.springframework.stereotype.Service;
import com.jothub.mapper.FileMapper;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;

    public FileServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @Override
    public void fileUpload(String fileName, long fileSize, String fileType, String filePath, int folderId) {
        fileMapper.fileUpload(fileName, fileSize, fileType, filePath, folderId);
    }

    @Override
    public List<Files> fileGetByAttributes(int fileId, String fileName, String fileType, int folderId) {
        return fileMapper.fileGetByAttributes(fileId, fileName, fileType, folderId);
    }

    @Override
    public void fileDeleteByIds(List<Integer> ids) {
        fileMapper.fileDeleteByIds(ids);
    }

    @Override
    public void fileUpdate(int fileId, String fileName, int folderId) {
        fileMapper.fileUpdate(fileId, fileName, folderId);
    }

    @Override
    public Files getFileById(int fileId) {
        return fileMapper.getFileById(fileId);
    }

    @Override
    public List<String> getFilePathsByIds(List<Integer> ids) {
        return fileMapper.getFilePathsByIds(ids);
    }
}

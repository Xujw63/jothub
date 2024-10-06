package com.jothub.controller;

import com.jothub.pojo.Files;
import com.jothub.pojo.Result;
import com.jothub.service.FileService;
import com.jothub.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/files")
public class FileController {

    final
    FileService fileService;

    final
    UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result fileUpload(@RequestHeader("token") String token, // 接收JWT令牌
                             @RequestParam(value = "parentFolderId", defaultValue = "0") int folderId,
                             @RequestParam(value = "file") MultipartFile file) throws IOException {


        // 判断是否为管理员
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以上传文件");
        }


        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = Objects.requireNonNull(originalFilename).substring(0, originalFilename.lastIndexOf("."));
        String fileType = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        long fileSize = file.getSize();


        //构造唯一的文件名（防止同名文件覆盖） - uuid（通用唯一识别码）
        String newFileName = UUID.randomUUID() + fileType;
        String filePath = "/jothub/upload" + "/" + newFileName;

        //将文件储存在服务器磁盘目录中
        file.transferTo(new File(filePath));

        fileService.fileUpload(fileName, fileSize, fileType, filePath, folderId);

        return Result.success();
    }


    @GetMapping("")
    public Result fileGetByAttributes(@RequestParam(value = "fileId", required = false, defaultValue = "0") int fileId,
                                      @RequestParam(value = "fileName", required = false, defaultValue = "") String fileName,
                                      @RequestParam(value = "fileType", required = false, defaultValue = "") String fileType,
                                      @RequestParam(value = "parentFolderId", required = false, defaultValue = "0") int folderId) {
        List<Files> files = fileService.fileGetByAttributes(fileId, fileName, fileType, folderId);
        return Result.success(files);
    }

    @DeleteMapping("/{ids}")
    public Result fileDeleteByIds(
            @RequestHeader("token") String token,
            @PathVariable List<Integer> ids){

        // 判断是否为管理员
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以删除文件");
        }

        // 查询每个文件的物理路径
        List<String> filePaths = fileService.getFilePathsByIds(ids);

        // 遍历路径并尝试删除文件
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleteResult = file.delete();
                if (!deleteResult) {
                    return Result.error("文件 " + " 删除失败");
                }
            } else {
                return Result.error("文件 " + " 不存在");
            }
        }

        fileService.fileDeleteByIds(ids);

        return Result.success();
    }

    @PatchMapping("")
    public Result fileUpdate(
            @RequestHeader("token") String token,
            @RequestParam(value = "fileId", required = false, defaultValue = "0") int fileId,
            @RequestParam(value = "fileName", required = false, defaultValue = "null") String fileName,
            @RequestParam(value = "folderId", required = false, defaultValue = "0") int folderId){


        // 判断是否为管理员
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以更改文件属性");
        }

        fileService.fileUpdate(fileId, fileName, folderId);

        return Result.success();
    }

    //下载文件
    @GetMapping("/download/{fileId}")
    public Result fileDownload(@PathVariable int fileId,
                               @RequestHeader("token") String token, // 接收JWT令牌
                               HttpServletResponse response) throws IOException {


        if (!userService.userIsActive(token)) {
            return Result.error("用户未激活，无法下载文件");
        }

        // 从数据库获取文件元数据
        Files file = fileService.getFileById(fileId);

        // 确保文件存在
        if (file == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("文件未找到");
            return Result.error("文件未找到");
        }

        String filePath = file.getFilePath();
        File downloadFile = new File(filePath);

        if (!downloadFile.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("服务器上未找到文件");
            return Result.error("服务器上未找到文件");
        }

        // 设置文件的内容类型和附件头信息
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + file.getFileType() + "\"");


        // 将文件数据写入响应输出流
        try (FileInputStream fileInputStream = new FileInputStream(downloadFile);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return Result.success();
    }

}

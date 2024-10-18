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

    /**
     * 文件上传接口
     *
     * @param token 用于验证用户的JWT令牌
     * @param folderId 指定上传文件的目标文件夹ID，默认值为0表示根目录
     * @param file 要上传的文件对象
     * @return 返回操作结果
     * @throws IOException 如果文件读取或存储过程中发生错误
     */
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result fileUpload(@RequestHeader("token") String token,
                             @RequestParam(value = "parentFolderId", defaultValue = "0") int folderId,
                             @RequestParam(value = "file") MultipartFile file) throws IOException {
        // 检查用户是否有权限执行此操作
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以上传文件");
        }

        // 获取文件的基本信息
        String originalFilename = file.getOriginalFilename();
        String fileName = Objects.requireNonNull(originalFilename).substring(0, originalFilename.lastIndexOf("."));
        String fileType = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        long fileSize = file.getSize();

        // 生成唯一文件名以避免重复
        String newFileName = UUID.randomUUID() + fileType;
        String filePath = "/jothub/upload" + "/" + newFileName;

        // 将文件保存到指定位置
        file.transferTo(new File(filePath));

        // 更新数据库记录
        fileService.fileUpload(fileName, fileSize, fileType, filePath, folderId);

        return Result.success();
    }

    /**
     * 根据文件属性查询文件列表
     *
     * @param fileId 文件ID，可选参数
     * @param fileName 文件名，可选参数
     * @param fileType 文件类型，可选参数
     * @param folderId 父文件夹ID，可选参数
     * @return 包含文件列表的结果对象
     */
    @GetMapping("")
    public Result fileGetByAttributes(@RequestParam(value = "fileId", required = false, defaultValue = "0") int fileId,
                                      @RequestParam(value = "fileName", required = false, defaultValue = "") String fileName,
                                      @RequestParam(value = "fileType", required = false, defaultValue = "") String fileType,
                                      @RequestParam(value = "parentFolderId", required = false, defaultValue = "0") int folderId) {
        List<Files> files = fileService.fileGetByAttributes(fileId, fileName, fileType, folderId);
        if(files==null || files.isEmpty()){
            return Result.error("没有该文件");
        }
        return Result.success(files);
    }

    /**
     * 根据文件ID列表删除多个文件
     *
     * @param token 用户身份验证令牌
     * @param ids 要删除的文件ID列表
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    public Result fileDeleteByIds(@RequestHeader("token") String token,
                                  @PathVariable List<Integer> ids) {
        // 检查用户权限
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以删除文件");
        }

        // 获取所有待删除文件的物理路径
        List<String> filePaths = fileService.getFilePathsByIds(ids);

        // 尝试删除每个文件
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                if (!file.delete()) {
                    return Result.error("文件删除失败");
                }
            } else {
                return Result.error("文件不存在");
            }
        }

        // 从数据库中移除文件记录
        fileService.fileDeleteByIds(ids);

        return Result.success();
    }

    /**
     * 更新文件信息
     *
     * @param token 用户身份验证令牌
     * @param fileId 文件ID
     * @param fileName 新文件名，可选参数
     * @param folderId 新文件夹ID，可选参数
     * @return 操作结果
     */
    @PatchMapping("")
    public Result fileUpdate(@RequestHeader("token") String token,
                             @RequestParam(value = "fileId", required = false, defaultValue = "0") int fileId,
                             @RequestParam(value = "fileName", required = false, defaultValue = "null") String fileName,
                             @RequestParam(value = "folderId", required = false, defaultValue = "0") int folderId) {
        // 检查用户权限
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以更改文件属性");
        }

        // 更新文件信息
        fileService.fileUpdate(fileId, fileName, folderId);

        return Result.success();
    }

    /**
     * 下载指定ID的文件
     *
     * @param fileId 文件ID
     * @param token 用户身份验证令牌
     * @param response HTTP响应对象，用于设置响应头和输出文件内容
     * @throws IOException 如果在读取或发送文件时发生错误
     */
    @GetMapping("/download/{fileId}")
    public void fileDownload(@PathVariable int fileId,
                             @RequestHeader("token") String token,
                             HttpServletResponse response) throws IOException {
        // 检查用户状态
        if (!userService.userIsActive(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(Result.error("用户未激活，无法下载文件").toString());
            return;
        }

        // 从数据库中获取文件信息
        Files file = fileService.getFileById(fileId);

        // 检查用户状态
        if (!userService.userIsActive(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(Result.error("用户未激活，无法下载文件").toString());
            return;
        }

        // 从数据库中获取文件信息
        Files file = fileService.getFileById(fileId);

        // 检查文件是否存在
        if (file == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(Result.error("文件未找到").toString());
            return;
        }

        // 检查服务器上的文件是否存在
        String filePath = file.getFilePath();
        File downloadFile = new File(filePath);
        if (!downloadFile.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(Result.error("服务器上未找到该文件").toString());
            return;
        }

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + file.getFileType() + "\"");

        // 向客户端发送文件内容
        try (FileInputStream fileInputStream = new FileInputStream(downloadFile);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(Result.error("下载失败").toString());
        }
    }
}

package com.jothub.controller;

import com.jothub.pojo.Folders;
import com.jothub.pojo.Result;
import com.jothub.service.FolderService;
import com.jothub.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/folders")
class FolderController {

    private final FolderService folderService;
    private final UserService userService;

    public FolderController(FolderService folderService, UserService userService) {
        this.folderService = folderService;
        this.userService = userService;
    }

    @PostMapping("")
    public Result folderCreate(
            @RequestHeader("token") String token,
            @RequestBody Map<String, Object> requestBody) {

        String folderName = (String) requestBody.get("folderName");
        int parentFolderId = (int) requestBody.get("parentFolderId");

        // 创建新文件夹
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以创建文件夹");
        }

        folderService.folderCreate(folderName, parentFolderId);
        return Result.success();
    }

    @PutMapping("")
    public Result folderUpdate(
            @RequestHeader("token") String token,
            @RequestParam(value = "folderId", required = false, defaultValue = "0") int folderId,
            @RequestParam(value = "folderName", required = false, defaultValue = "") String folderName,
            @RequestParam(value = "parentFolderId", required = false, defaultValue = "0") int parentFolderId) {

        // 判断是否为管理员
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以上传文件");
        }

        folderService.folderUpdate(folderId, folderName, parentFolderId);
        // 更新文件夹信息
        return Result.success();
    }

    @DeleteMapping("/{ids}")
    public Result folderDelete(
            @RequestHeader("token") String token,
            @PathVariable List<Integer> ids) {

        // 判断是否为管理员
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以删除文件夹");
        }
        // 删除文件夹
        folderService.deleteFolderAndFiles(ids);
        return Result.success();
    }

    @GetMapping("/{ids}")
    public Result folderGetByIds(@PathVariable List<Integer> ids) {
        // 根据ID获取文件夹
        List<Folders> folders = folderService.folderGetByIds(ids);
        return Result.success(folders);
    }

    @GetMapping("parent/{parentFolderId}")
    public Result folderGetByParentFolderId(@PathVariable int parentFolderId) {
        // 根据ID获取文件夹
        List<Folders> folders = folderService.folderGetByParentFolderId(parentFolderId);
        return Result.success(folders);
    }
}

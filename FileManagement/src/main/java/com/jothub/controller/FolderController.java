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

    /**
     * 创建新文件夹
     *
     * @param token 用户身份验证令牌
     * @param requestBody 包含文件夹名称和父文件夹ID的请求体
     * @return 操作结果
     */
    @PostMapping("")
    public Result folderCreate(
            @RequestHeader("token") String token,
            @RequestBody Map<String, Object> requestBody) {

        String folderName = (String) requestBody.get("folderName");
        int parentFolderId = (int) requestBody.get("parentFolderId");

        // 检查用户是否有权限创建文件夹
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以创建文件夹");
        }

        // 调用服务层方法创建新文件夹
        folderService.folderCreate(folderName, parentFolderId);
        return Result.success();
    }

    /**
     * 更新文件夹信息
     *
     * @param token 用户身份验证令牌
     * @param folderId 文件夹ID
     * @param folderName 新文件夹名称，可选参数
     * @param parentFolderId 新父文件夹ID，可选参数
     * @return 操作结果
     */
    @PutMapping("")
    public Result folderUpdate(
            @RequestHeader("token") String token,
            @RequestParam(value = "folderId", required = false, defaultValue = "0") int folderId,
            @RequestParam(value = "folderName", required = false, defaultValue = "") String folderName,
            @RequestParam(value = "parentFolderId", required = false, defaultValue = "0") int parentFolderId) {

        // 检查用户是否有权限更新文件夹信息
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以更新文件夹");
        }

        // 调用服务层方法更新文件夹信息
        folderService.folderUpdate(folderId, folderName, parentFolderId);
        return Result.success();
    }

    /**
     * 删除一个或多个文件夹及其包含的所有文件
     *
     * @param token 用户身份验证令牌
     * @param ids 要删除的文件夹ID列表
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    public Result folderDelete(
            @RequestHeader("token") String token,
            @PathVariable List<Integer> ids) {

        // 检查用户是否有权限删除文件夹
        if (!userService.userIsAdmin(token)) {
            return Result.error("无权限，只有管理员可以删除文件夹");
        }

        // 调用服务层方法删除文件夹及其包含的所有文件
        folderService.deleteFolderAndFiles(ids);
        return Result.success();
    }

    /**
     * 根据ID列表获取文件夹信息
     *
     * @param ids 文件夹ID列表
     * @return 包含文件夹信息的结果对象
     */
    @GetMapping("/{ids}")
    public Result folderGetByIds(@PathVariable List<Integer> ids) {
        // 调用服务层方法根据ID列表获取文件夹信息
        List<Folders> folders = folderService.folderGetByIds(ids);
        return Result.success(folders);
    }

    /**
     * 根据父文件夹ID获取子文件夹信息
     *
     * @param parentFolderId 父文件夹ID
     * @return 包含子文件夹信息的结果对象
     */
    @GetMapping("parent/{parentFolderId}")
    public Result folderGetByParentFolderId(@PathVariable int parentFolderId) {
        // 调用服务层方法根据父文件夹ID获取子文件夹信息
        List<Folders> folders = folderService.folderGetByParentFolderId(parentFolderId);
        return Result.success(folders);
    }
}

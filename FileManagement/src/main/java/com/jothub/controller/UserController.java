package com.jothub.controller;

import com.jothub.pojo.Result;
import com.jothub.pojo.User;
import com.jothub.service.UserService;
import com.jothub.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     *
     * @param u 包含用户名和密码的用户对象
     * @return 操作结果
     */
    @PostMapping("/register")
    public Result userRegister(@RequestBody User u) {
        // 调用服务层方法进行用户注册
        userService.userRegister(u.getUsername(), u.getPassword());
        return Result.success();
    }

    /**
     * 用户登录接口
     *
     * @param u 包含用户名和密码的用户对象
     * @return 操作结果，如果登录成功则返回JWT令牌
     */
    @PostMapping("/login")
    public Result userLogin(@RequestBody User u) {
        // 调用服务层方法进行用户登录
        User user = userService.userLogin(u.getUsername(), u.getPassword());
        System.out.println(user);
        if (user != null) {
            // 如果用户存在，生成JWT令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("uid", user.getUid());
            claims.put("username", user.getUsername());

            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        }

        // 如果用户不存在或密码错误，返回错误信息
        return Result.error("用户名或密码错误");
    }

    /**
     * 更新用户密码接口
     *
     * @param u 包含用户名和旧密码的用户对象
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PatchMapping("/updatePassword")
    public Result userLoginPasswordUpdate(
            @RequestBody User u,
            @RequestParam(value = "password") String newPassword) {
        // 调用服务层方法更新用户密码
        if (userService.userLoginPasswordUpdate(u.getUsername(), u.getPassword(), newPassword) == 1) {
            return Result.success();
        }
        // 如果用户名或旧密码错误，返回错误信息
        return Result.error("用户名或密码错误");
    }
}

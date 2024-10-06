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


    @PostMapping("/register")
    public Result userRegister(@RequestBody User u) {
        userService.userRegister(u.getUsername(), u.getPassword());
        return Result.success();
    }

    @PostMapping("/login")
    public Result userLogin(@RequestBody User u) {
        User user = userService.userLogin(u.getUsername(), u.getPassword());
        System.out.println(user);
        if (user != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("uid", user.getUid());
            claims.put("username", user.getUsername());

            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        }

        return Result.error("用户名或密码错误");
    }
    @PatchMapping("/updatePassword")
    public Result userLoginPasswordUpdate(
            @RequestBody User u,
            @RequestParam(value = "password") String newPassword) {
        if(userService.userLoginPasswordUpdate(u.getUsername(), u.getPassword(), newPassword) == 1){
            return Result.success();
        }
        return Result.error("用户名或密码错误");
    }
}

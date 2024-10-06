package com.jothub.service.impl;

import com.jothub.mapper.UserMapper;
import com.jothub.pojo.User;
import com.jothub.service.UserService;
import com.jothub.utils.JwtUtils;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void userRegister(String username, String password) {
        userMapper.userRegister(username, password);
    }

    @Override
    public User userLogin(String username, String password) {
        return userMapper.userLogin(username, password);
    }

    @Override
    public int userLoginPasswordUpdate(String username, String password, String newPassword) {
        return userMapper.userLoginPasswordUpdate(username, password, newPassword);
    }

    @Override
    public boolean userIsActive(String token) {
        Map<String, Object> claims = JwtUtils.parseJWT(token);
        if (claims == null) {
            return false;
        }

        // 提取用户信息
        String username = (String) claims.get("username");

        // 判断是否为管理员
        return userMapper.userIsActive(username);
    }

    @Override
    public boolean userIsAdmin(String token) {

        Map<String, Object> claims = JwtUtils.parseJWT(token);
        if (claims == null) {
            return false;
        }

        // 提取用户信息
        String username = (String) claims.get("username");

        // 判断是否为管理员
        return userMapper.userIsAdmin(username);
    }
}

package com.jothub.service;

import com.jothub.pojo.User;

public interface UserService {

    /**
     * 注册新用户
     *
     * @param username 用户名
     * @param password 密码
     */
    void userRegister(String username, String password);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象，如果用户名或密码错误则返回null
     */
    User userLogin(String username, String password);

    /**
     * 更新用户密码
     *
     * @param username 用户名
     * @param password 当前密码
     * @param newPassword 新密码
     * @return 更新成功返回1，否则返回0
     */
    int userLoginPasswordUpdate(String username, String password, String newPassword);

    /**
     * 检查用户是否处于激活状态
     *
     * @param token 用户身份验证令牌
     * @return 如果用户活跃返回true，否则返回false
     */
    boolean userIsActive(String token);

    /**
     * 检查用户是否为管理员
     *
     * @param token 用户身份验证令牌
     * @return 如果用户是管理员返回true，否则返回false
     */
    boolean userIsAdmin(String token);
}

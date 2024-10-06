package com.jothub.service;

import com.jothub.pojo.User;

public interface UserService {
    void userRegister(String username, String password);
    User userLogin(String username, String password);
    int userLoginPasswordUpdate(String username, String password, String newPassword);
    boolean userIsActive(String token);
    boolean userIsAdmin(String token);
}

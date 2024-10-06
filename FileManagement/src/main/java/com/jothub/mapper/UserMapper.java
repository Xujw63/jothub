package com.jothub.mapper;

import com.jothub.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void userRegister(String username, String password);

    User userLogin(String username, String password);

    int userLoginPasswordUpdate(String username, String password, String newPassword);

    boolean userIsActive(String username);

    boolean userIsAdmin(String username);
}

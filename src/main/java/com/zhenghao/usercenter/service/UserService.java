package com.zhenghao.usercenter.service;

import com.zhenghao.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author D14
* @description 针对表【user】的数据库操作Service
* @createDate 2025-04-15 22:17:19
*/
public interface UserService extends IService<User> {
    /**
     *
     * @param userAccount
     * @param userPassword
     * @param check
     * @return
     */

    long userRegister(String userAccount, String userPassword, String check, String planetCode);
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User orginUser);

    int userLogout(HttpServletRequest request);
}

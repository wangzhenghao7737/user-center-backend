package com.zhenghao.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhenghao.usercenter.common.BaseResponse;
import com.zhenghao.usercenter.common.ErrorCode;
import com.zhenghao.usercenter.common.ResultUtils;
import com.zhenghao.usercenter.exception.BusinessException;
import com.zhenghao.usercenter.model.domain.User;
import com.zhenghao.usercenter.model.request.UserLoginRequest;
import com.zhenghao.usercenter.model.request.UserRegisterRequest;
import com.zhenghao.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zhenghao.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.zhenghao.usercenter.constant.UserConstant.USER_LOGIN_STATUS;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
//            return ResultUtils.error(ErrorCode.NULL_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String check = userRegisterRequest.getCheck();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount,userPassword,check,planetCode)){
            throw  new BusinessException(ErrorCode.PARAMS_NULL);
        }
        long id = userService.userRegister(userAccount, userPassword, check, planetCode);
        return ResultUtils.success(id);
    }
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            return null;
        }
        Integer result = userService.userLogout(request);
        return ResultUtils.success(result);
    }
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username,HttpServletRequest request){
        if (!isAdmin(request)){
            return ResultUtils.fail(null);
        }
        QueryWrapper<User> qw = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            qw.like("username",username);
        }
        List<User> userList = userService.list(qw);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    @PostMapping
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id,HttpServletRequest request){
        if(id == null){
            return ResultUtils.fail(false);
        }
        //验证管理员权限
        if(!isAdmin(request)){
            return ResultUtils.fail(false);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    private boolean isAdmin(HttpServletRequest request){
        //验证管理员权限
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User)userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}

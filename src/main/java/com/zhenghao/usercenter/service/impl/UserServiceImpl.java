package com.zhenghao.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.zhenghao.usercenter.common.ErrorCode;
import com.zhenghao.usercenter.exception.BusinessException;
import com.zhenghao.usercenter.model.domain.User;
import com.zhenghao.usercenter.service.UserService;
import com.zhenghao.usercenter.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zhenghao.usercenter.constant.UserConstant.USER_LOGIN_STATUS;

//import static com.zhenghao.usercenter.constant.UserConstant.USER_LOGIN_STATUS;

/**
* @author D14
* @description 针对表【orginUser】的数据库操作Service实现
* @createDate 2025-04-15 22:17:19
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    private static final String salt ="jackey";

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String check, String planetCode) {
        if(StringUtils.isAnyBlank(userAccount,userPassword,check)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号太短");
        }
        if(userPassword.length()<8 || check.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码太短");
        }
        String validPattern =".*[[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t].*";;
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return -1;
        }
        if(!userPassword.equals(check)){
            return -1;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if(count>0){
            return -1;
        }

        String encryptPWD = DigestUtils.md5DigestAsHex((salt + "mypassword").getBytes());
        User orginUser = new User();
        orginUser.setUserAccount(userAccount);
        orginUser.setUserPassword(encryptPWD);
        orginUser.setPlanetCode(planetCode);
        boolean save = this.save(orginUser);
        if(!save){
            return -1;
        }
        return orginUser.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if(userAccount.length()<4){
            return null;
        }
        if(userPassword.length()<8){
            return null;
        }
        String validPattern =".*[[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t].*";;
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return null;
        }
        String encryptPWD = DigestUtils.md5DigestAsHex((salt + "mypassword").getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPWD);
        User orginUser = userMapper.selectOne(queryWrapper);
        if(orginUser == null){
            log.info("orginUser login failed,can not match pwd");
            return null;
        }

        User safetyUser = getSafetyUser(orginUser);
        request.getSession().setAttribute(USER_LOGIN_STATUS,safetyUser);
        return safetyUser;
    }

    @Override
    public User getSafetyUser(User orginUser){
        User safeUser = new User();
        safeUser.setId(orginUser.getId());
        safeUser.setUsername(orginUser.getUsername());
        safeUser.setUserAccount(orginUser.getUserAccount());
        safeUser.setAvatarUrl(orginUser.getAvatarUrl());
        safeUser.setGender(orginUser.getGender());
        safeUser.setPhone(orginUser.getPhone());
        safeUser.setEmail(orginUser.getEmail());
        safeUser.setUserRole(orginUser.getUserRole());
        safeUser.setUserStatus(orginUser.getUserStatus());
        safeUser.setCreateTime(orginUser.getCreateTime());
        safeUser.setPlanetCode(orginUser.getPlanetCode());
        safeUser.setTags(orginUser.getTags());
        return safeUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }

    @Override
    public List<User> searchUserByTages(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        方式一：使用DQL
//        QueryWrapper<User> qw = new QueryWrapper<>();
//        for (String tag : tagNameList) {
//            qw = qw.like("tags",tag);
//        }
//        List<User> userList = userMapper.selectList(qw);
//        userList.forEach(user->{
//            getSafetyUser(user);
//        });
//        return userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
//        方式二：内存中操作
        Gson gson = new Gson();
        QueryWrapper<User> qw = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(qw);
//        for(User user : userList){
//            String tags = user.getTags();
//            Set<String> tagNameLists = gson.fromJson(tags,new TypeToken<Set<String>>(){}.getType());
//            for(String tagName : tagNameLists){
//                if(!tagNameList.contains(tagName)){
//                    return false;
//                }
//            }
//            return true
//        }
        return userList.stream().filter(user -> {
            String tags = user.getTags();
            if(StringUtils.isBlank(tags)){
                return false;
            }
            Set<String> tagNameLists = gson.fromJson(tags,new TypeToken<Set<String>>(){}.getType());
            for(String tagName : tagNameList){
                if(!tagNameLists.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(user -> getSafetyUser(user)).collect(Collectors.toList());
//        return userList;
    }



}





package com.zhenghao.usercenter.service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zhenghao.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    public void testAdduser(){
        User user = new User();
        user.setUsername("zhenghao");
        user.setUserAccount("123");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister() {

    }
    @Test
    public void searchUserByTags(){
        List<String> list = Arrays.asList("java", "python");
        List<User> user = userService.searchUserByTages(list);
        System.out.println(user);
        Assertions.assertNotNull(user);
    }

}
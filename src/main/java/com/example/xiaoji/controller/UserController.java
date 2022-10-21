package com.example.xiaoji.controller;

import com.example.xiaoji.entity.User;
import com.example.xiaoji.common.R;
import com.example.xiaoji.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机号短信验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        return userService.sendMsg(user,session);
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {
        return userService.login(map,session);
    }

    /**
     * 移动端用户退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        return userService.logout(session);
    }
}

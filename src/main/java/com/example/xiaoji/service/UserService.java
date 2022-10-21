package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {
    R<String> sendMsg(User user, HttpSession session);

    R<User> login(Map<String, String> map, HttpSession session);

    R<String> logout(HttpSession session);
}

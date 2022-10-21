package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.User;
import com.example.xiaoji.mapper.UserMapper;
import com.example.xiaoji.service.UserService;
import com.example.xiaoji.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R<String> sendMsg(User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            //SMSUtils.sendMessage("", "", phone, code);
            log.info("code: {}", code);
            // session.setAttribute(phone, code);
            stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    @Override
    public R<User> login(Map<String, String> map, HttpSession session) {
        String phone = map.get("phone");
        // Object sessionCode = session.getAttribute(phone);
        String code = stringRedisTemplate.opsForValue().get(phone);
        if (code != null && code.equals(map.get("code"))) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setName("user_"+phone);
                user.setPhone(phone);
                save(user);
            }
            session.setAttribute("user", user.getId());
            log.info("手机用户 {} 正在尝试登陆...\t登陆成功", phone);
            stringRedisTemplate.delete(phone);
            return R.success(user);
        }
        log.info("手机用户 {} 正在尝试登陆...\t登陆失败", phone);
        return R.error("登陆失败");
    }

    @Override
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}

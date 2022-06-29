package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //校验手机号，应该前端做
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机格式有误");
        }
        //符合生成，不符合返回错误
        String code = RandomUtil.randomNumbers(6);
        //保存验证码
        session.setAttribute("code",code);
        //发送验证码
        log.debug("发送验证码成功，验证码：：{}",code);
        return null;
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //校验
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机格式有误");
        }
        //校验验证码
        Object code = session.getAttribute("code");
        String formCode = loginForm.getCode();
        if (code == null || !code.toString().equals(formCode)) {
            return Result.fail("验证码有误");
        }
        //查询用户
        User user = query().eq("phone", phone).one();
        if (user == null) {
            user = createUserWithPhone(phone);
        }
        session.setAttribute("user",user);
        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(10));
        save(user);
        return user;
    }
}

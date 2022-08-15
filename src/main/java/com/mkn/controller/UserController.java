package com.mkn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mkn.common.BaseContext;
import com.mkn.common.R;
import com.mkn.entity.User;
import com.mkn.service.UserService;
import com.mkn.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * redis缓存验证码,登录成功则删除
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
//            session.setAttribute("code",code);
            ValueOperations valueOperations = redisTemplate.opsForValue();
            valueOperations.set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机短信验证码发送成功");
        }
        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
//        session.setAttribute("code","1234");
//        Object codeInSession = session.getAttribute("code");
        Object codeInfo = redisTemplate.opsForValue().get(phone);
        if (codeInfo!=null && codeInfo.equals(code)){
            //如果能对比成功,说明登陆成功
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
            if (user==null){
                user = new User().setPhone(phone).setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user",user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败!");
    }

}

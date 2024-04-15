package com.xuyuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuyuan.common.LogStatus;
import com.xuyuan.common.R;
import com.xuyuan.entity.User;
import com.xuyuan.service.UserService;
import com.xuyuan.util.SMSUtils;
import com.xuyuan.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info("发送验证码");
        //接收手机号
        String phone = user.getPhone();
        //生成验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();

        System.out.println(code);
      /*  //调用阿里云发送验证码
        try {
            SMSUtils.ssm(phone,code);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //生成验证码保存到session里面去*/
        session.setAttribute(phone,code);

        return R.success("手机验证码短信发送成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取验证码
        String Rightcode = (String) session.getAttribute(phone);
        //对比成功
        if (Rightcode != null && Rightcode.equals(code)){
            //登录成功
            //判断是否是新用户，如果是新用户自动进行注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);
            if (user == null){
                //判断当前用户为新用户,自动完成注册
                 user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute(LogStatus.UserlogStatus,user.getId());
            log.info("登录成功");
            return R.success(user);

        }
        return R.error("验证码错误");
    }
}

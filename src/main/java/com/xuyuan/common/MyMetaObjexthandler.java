package com.xuyuan.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjexthandler implements MetaObjectHandler {
    @Override
    //获取不了session的id
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]。。。。");
        log.info(metaObject.toString());

        //设置初始密码
        metaObject.setValue("password",LogStatus.InitPassword);
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        //LogStatus.getCurrentSessionId(): 获取session中存放的id值
        metaObject.setValue("createUser",LogStatus.getCurrentSessionId());
        System.out.println(LogStatus.getCurrentSessionId());
        metaObject.setValue("updateUser",LogStatus.getCurrentSessionId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]。。。。");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",LogStatus.getCurrentSessionId());
    }
}

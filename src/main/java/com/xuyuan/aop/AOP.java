package com.xuyuan.aop;

import com.xuyuan.service.DishService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Aspect
@Component
public class AOP  {
/*    @Autowired
    private DishService dishService;
    @Pointcut("@annotation(com.xuyuan.aop.MyAop)")
    private void pt(){}
    @Before("pt()")
    public void before(){
        dishService.UpdatedishWithFlavor(dishDto);
        //清理某个修改分类的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
    }*/

    /*@Cacheable(value = "userCahche" ,key = "#id",condition = "#result != null ")
    基于map来实现的*/
}

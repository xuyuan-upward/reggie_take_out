package com.xuyuan.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuyuan.common.LogStatus;
import com.xuyuan.common.R;
import com.xuyuan.entity.Dish;
import com.xuyuan.entity.ShoppingCart;
import com.xuyuan.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或者套餐到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加菜品或者套餐到购物车");

        //设置用户id
        Long currentSessionId = LogStatus.getCurrentSessionId();
        shoppingCart.setUserId(currentSessionId);

        Long dishId = shoppingCart.getDishId();
        //提交的是菜品
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,currentSessionId);
        if (dishId != null){
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询当前数据是否在购物车已经存在，如果存在，原基础数量+1，
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(wrapper);
        if (shoppingCart1 != null ){
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
           shoppingCartService.updateById(shoppingCart1);
        }
        else{
            //不存在默认为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        }

        return R.success(shoppingCart1);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,LogStatus.getCurrentSessionId()).orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }

    @DeleteMapping("clean")
    public R<String> clean(){
        log.info("清空购物车");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,LogStatus.getCurrentSessionId());
        shoppingCartService.remove(wrapper);
        return R.success("清空成功");
    }
}

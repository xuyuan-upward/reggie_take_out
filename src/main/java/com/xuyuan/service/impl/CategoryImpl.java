package com.xuyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuan.entity.Category;
import com.xuyuan.entity.Dish;
import com.xuyuan.entity.Setmeal;
import com.xuyuan.exception.CustomException;
import com.xuyuan.mapper.CategoryMapper;
import com.xuyuan.service.CategoryService;
import com.xuyuan.service.DishService;
import com.xuyuan.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     *
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        //查询分类是否关联了菜品
        LambdaQueryWrapper<Dish> Dishwrapper = new LambdaQueryWrapper<>();
        Dishwrapper.eq(Dish::getCategoryId, ids);
        int Dishcount = dishService.count(Dishwrapper);
        if (Dishcount > 0) {
            //已经关联不能删除
            throw new CustomException("当前分类关联了菜品，不能够被删除");
        }
        //查询分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> Setmealwrapper = new LambdaQueryWrapper<>();
        Setmealwrapper.eq(Setmeal::getCategoryId, ids);
        int Setmealcount = setmealService.count(Setmealwrapper);
        if (Setmealcount > 0) {
            //已经关联不能删除
            throw new CustomException("当前分类关联了套餐，不能够被删除");
        }
        //可以删除
        this.removeById(ids);
    }


}

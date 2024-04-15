package com.xuyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuan.common.LogStatus;
import com.xuyuan.dto.SetmealDto;
import com.xuyuan.entity.Setmeal;
import com.xuyuan.entity.SetmealDish;
import com.xuyuan.exception.CustomException;
import com.xuyuan.mapper.SetmealMapper;
import com.xuyuan.service.SetmealDishService;
import com.xuyuan.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Override
    public void SetmealWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存关联套餐的id到菜品去
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void ReSetmealWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getStatus, LogStatus.OKstatus).in(Setmeal::getId,ids);
        int count = this.count(wrapper);
        if (count > 0){
            //起售中不能够被删除，抛出异常
            throw new CustomException("套餐正在售卖中，不能够被删除....");
        }
       //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        //删除关系表中的数据--->setmeal_dish 其实都是逻辑删除
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        return ;

    }

    /**
     * 根据ID查询套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 根据id查询setmeal表中的基本信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        // 对象拷贝。
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询关联表setmeal_dish的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        //设置套餐菜品属性
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }
    /**
     * 更新套餐信息，不仅要更新setmeal基本信息， 还要更新套餐所对应的菜品到setmeal_dish表
     *
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 保存setmeal表中的基本数据。
        this.updateById(setmealDto);
        // 先删除原来的套餐所对应的菜品数据。
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 更新套餐关联菜品信息。setmeal_dish表。
        // 所以需要处理setmeal_id字段。
        // 先获得套餐所对应的菜品集合。
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //每一个item为SetmealDish对象。
        setmealDishes = setmealDishes.stream().map((item) -> {
            //设置setmeal_id字段。
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 重新保存套餐对应菜品数据
        setmealDishService.saveBatch(setmealDishes);
    }

}

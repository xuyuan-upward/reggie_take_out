package com.xuyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuan.dto.DishDto;
import com.xuyuan.entity.Dish;

public interface DishService extends IService<Dish> {
    void dishWithFlavor(DishDto dishDto);

    DishDto getDishWithFlavor(Long id);

    void UpdatedishWithFlavor(DishDto dishDto);

    void delete(Long ids);
}

package com.xuyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuan.dto.SetmealDto;
import com.xuyuan.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void SetmealWithDish(SetmealDto setmealDto);

    void ReSetmealWithDish(List<Long> ids);
    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}

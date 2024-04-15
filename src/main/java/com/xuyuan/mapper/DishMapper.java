package com.xuyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.entity.Dish;
import com.xuyuan.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}

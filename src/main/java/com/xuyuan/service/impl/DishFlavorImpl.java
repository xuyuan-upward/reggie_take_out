package com.xuyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuan.entity.DishFlavor;
import com.xuyuan.mapper.DishFlavorMapper;
import com.xuyuan.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService{
}

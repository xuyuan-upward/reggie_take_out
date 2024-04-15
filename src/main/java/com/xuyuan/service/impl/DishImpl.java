package com.xuyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuan.dto.DishDto;
import com.xuyuan.entity.Dish;
import com.xuyuan.entity.DishFlavor;
import com.xuyuan.mapper.DishMapper;
import com.xuyuan.service.DishFlavorService;
import com.xuyuan.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     *
     * @param dishDto
     */
    @Transactional
    @Override
    public void dishWithFlavor(DishDto dishDto) {
        //保存菜品基本信息，此时ID还没有出现
        this.save(dishDto);
        //保存好菜品，还没有写进数据库里面，此时ID已经出现
        Long id = dishDto.getId();
        //把菜品id与口味关联
        for (DishFlavor flavor : dishDto.getFlavors()) {
            flavor.setDishId(id);
        }
        //保存口味信息
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 根据ID回显菜品和口味，分类信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getDishWithFlavor(Long id) {
        //获取菜品信息
        Dish dish = this.getById(id);

        //根据菜品id获取口味
        LambdaQueryWrapper<DishFlavor> dishFlavorwrapper = new LambdaQueryWrapper<>();
        dishFlavorwrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorlist = dishFlavorService.list(dishFlavorwrapper);

   /*     //根据菜品id查询分类
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getId,id);
        Category category = categoryService.getOne(categoryLambdaQueryWrapper);
        String name = category.getName();*/

        //把信息封装到DishDto中
        DishDto dishDto = new DishDto();
        /*  dishDto.setCategoryName(name);*/
        dishDto.setFlavors(dishFlavorlist);
        BeanUtils.copyProperties(dish, dishDto);
        return dishDto;
    }

    /**
     * 修改菜品信息
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void UpdatedishWithFlavor(DishDto dishDto) {
        /**
         * 修改口味
         */
        //1 先删除原来存在的口味
        Long id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);
        //2 把口味关联的菜品id set赋值到口味表的dish_id去
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(item -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        //3 然后再保存现在的口味到口味表里面
        dishFlavorService.saveBatch(flavors);
        //修改菜品信息
        this.updateById(dishDto);
    }

    /**
     * 删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void delete(Long ids) {
        //删除口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,ids);
        dishFlavorService.remove(wrapper);
        //删除菜品
        this.removeById(ids);
        return;
    }
}

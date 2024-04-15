package com.xuyuan.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.common.LogStatus;
import com.xuyuan.common.R;
import com.xuyuan.dto.DishDto;
import com.xuyuan.entity.Category;
import com.xuyuan.entity.Dish;
import com.xuyuan.entity.DishFlavor;
import com.xuyuan.service.CategoryService;
import com.xuyuan.service.DishFlavorService;
import com.xuyuan.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.dishWithFlavor(dishDto);
        return R.success("添加成功");

    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("菜品分页查询");
        //构造分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        dishService.page(dishPage, wrapper);
        //对象拷贝
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtoList = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    /**
     * 根据菜品ID修改信息
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("修改菜品信息");
        dishService.UpdatedishWithFlavor(dishDto);
        //清理某个修改分类的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

    /**
     * 根据ID回显菜品信息
     */
    @GetMapping("/{id}")
    public R<DishDto> getId(@PathVariable Long id) {
        log.info("根据id回显菜品信息");
        DishDto dishDto = dishService.getDishWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 删除菜品
     */
    @DeleteMapping()
    public R<String> delete( @RequestParam List<Long> ids){
        log.info("删除菜品");
        dishService.removeByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 启用和禁用菜品
     */
    @PostMapping("status/{status}")
    public R<String> status(@PathVariable Integer status ,@RequestParam List<Long> ids){
        log.info("启用和禁用菜品");
        //根据id查找dish
        List<Dish> dishList = dishService.listByIds(ids);
        //设置状态
        List<Dish> collect = dishList.stream().map(item -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(collect);
        return R.success("修改成功");
    }

    /**
     * 根据分类查找菜品
     * @param dish
     * @return
     */
    @GetMapping("list")
    public R<List<DishDto>> list(Dish dish){
        log.info("根据分类查找菜品");
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_242343_1;
        //先从redis中获取数据
         dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
       if (dishDtoList != null){
           return R.success(dishDtoList);
       }

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId,dish.getCategoryId()).eq(Dish::getStatus, LogStatus.OKstatus)
        .orderByAsc(Dish::getSort);
        List<Dish> list = dishService.list(wrapper);
         dishDtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            //获取当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //查询当前菜品关联的口味数据
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
         //如果不存在，需要查询数据库，将查询到的数据缓存到redis
         redisTemplate.opsForValue().set(key,dishDtoList,6, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}

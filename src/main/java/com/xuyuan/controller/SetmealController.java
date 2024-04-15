package com.xuyuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.common.R;
import com.xuyuan.dto.SetmealDto;
import com.xuyuan.entity.Category;
import com.xuyuan.entity.Setmeal;
import com.xuyuan.service.CategoryService;
import com.xuyuan.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setmealService.SetmealWithDish(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("套餐分页查询");
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        setmealService.page(setmealPage, wrapper);
        //对象拷贝
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtos = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            LambdaQueryWrapper<Category> ctegorywrapper = new LambdaQueryWrapper<>();
            ctegorywrapper.eq(Category::getId, item.getCategoryId());
            Category one = categoryService.getOne(ctegorywrapper);
            String categoryName = one.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtos);

        //为什么返回page，因为page里面有个records包含了所有的setmeal对象信息，而且前端调用的是page.records
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐，删除套餐和菜品的关联数据
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除套餐");
        setmealService.ReSetmealWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 修改商品起售状态
     * @param ids
     * @return
     */
    @PostMapping("status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids){
        log.info("修改商品起售状态");
        //根据id查找dish
        List<Setmeal> setList = setmealService.listByIds(ids);
        //设置状态
        List<Setmeal> collect = setList.stream().map(item -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(collect);
        return R.success("修改状态成功");
    }

    /**
     * 根据id查询套餐信息
     *(套餐信息的回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        log.info("根据id查询套餐信息:{}", id);
        // 调用service执行查询。、
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息。
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info("修改套餐信息{}", setmealDto);
        // 执行更新。
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐信息成功");
    }

    /**
     * 根据分类查询套餐
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId()).eq(Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }
}

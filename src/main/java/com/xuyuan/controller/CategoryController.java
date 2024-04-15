package com.xuyuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.common.R;
import com.xuyuan.entity.Category;
import com.xuyuan.entity.Employee;
import com.xuyuan.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增分类：{}",category.getType());
        categoryService.save(category);
        return R.success("新增菜品成功");
    }

    /**
     * 分类分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("page = {}, pageSize = {}, name = {}", page);

        //构造分页构造器
        Page<Category> pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除的分类：{}",ids);
//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id修改分类
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类：{}",category.getType());
        categoryService.updateById(category);
        return R.success("修改菜品成功");
    }

    /**
     * 查询分类
     * @param category
     * @return
     */
    @GetMapping("list")
    public R<List<Category>> list(Category category){
        log.info("分类查询");
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null,Category::getType,category.getType())
                .orderByAsc(Category::getSort)
                .orderByAsc((Category::getUpdateTime));
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }

}


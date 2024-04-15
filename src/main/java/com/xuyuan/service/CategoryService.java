package com.xuyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuan.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}

package com.mkn.service;

import com.mkn.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}

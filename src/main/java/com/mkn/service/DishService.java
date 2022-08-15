package com.mkn.service;

import com.mkn.dto.DishDto;
import com.mkn.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据
    public void saveWithFlavor(DishDto dishDto);

    //修改菜品,同时修改菜品对应的口味数据
    public void updateWithFlavor(DishDto dishDto);
}

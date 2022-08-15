package com.mkn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mkn.dto.DishDto;
import com.mkn.entity.Dish;
import com.mkn.entity.DishFlavor;
import com.mkn.mapper.DishMapper;
import com.mkn.service.DishFlavorService;
import com.mkn.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品,同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        List<DishFlavor> dishFlavors = dishDto.getFlavors()
                .stream()
                .map(dishFlavor -> dishFlavor.setDishId(dishDto.getId()))
                .collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavors);

    }

    @Override
//    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //修改菜品的基本信息到菜品表dish
        this.updateById(dishDto);
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,dishDto.getId()));
        List<DishFlavor> dishFlavors = dishDto.getFlavors()
                .stream()
                .map(dishFlavor -> dishFlavor.setDishId(dishDto.getId()))
                .collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavors);
    }
}

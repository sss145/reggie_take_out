package com.mkn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mkn.common.CustomException;
import com.mkn.entity.Category;
import com.mkn.entity.Dish;
import com.mkn.entity.Setmeal;
import com.mkn.mapper.CategoryMapper;
import com.mkn.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mkn.service.DishService;
import com.mkn.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private DishService dishService;
    /**
     * 根据Id删除分类,删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        //查询当前分类是否关联了菜品,如果已经关联,抛出一个业务异常
        if (count>0){
            //已关联菜品,抛出业务异常
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }
        //查询当前分类是否关联了套餐,如果已经关联,抛出一个业务异常
        if (setmealService.count(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId,id))>0){
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}

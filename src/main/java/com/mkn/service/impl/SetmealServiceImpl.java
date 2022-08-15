package com.mkn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mkn.dto.SetMealDto;
import com.mkn.entity.Setmeal;
import com.mkn.entity.SetmealDish;
import com.mkn.mapper.SetmealMapper;
import com.mkn.service.SetmealDishService;
import com.mkn.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveSetMeal(SetMealDto setMealDto) {
        this.save(setMealDto);
        setmealDishService.saveBatch(setMealDto.getSetmealDishes()
                .stream()
                .map(setmealDish -> setmealDish.setSetmealId(setMealDto.getId().toString()))
                .collect(Collectors.toList())
                );
    }

    @Override
    @Transactional
    public void updateSetMeal(SetMealDto setMealDto) {
        this.updateById(setMealDto);
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId,setMealDto.getId()));
        setmealDishService.saveBatch(setMealDto.getSetmealDishes()
                .stream()
                .map(setmealDish -> setmealDish.setSetmealId(setMealDto.getId().toString()))
                .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public void deleteSetMeal(List<Long> ids) {
        ids.forEach(id->{
            setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>()
                    .eq(SetmealDish::getSetmealId,id));
            this.removeById(id);
        });
    }
}

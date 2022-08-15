package com.mkn.service;

import com.mkn.dto.SetMealDto;
import com.mkn.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
public interface SetmealService extends IService<Setmeal> {

    public void saveSetMeal(SetMealDto setMealDto);

    public void updateSetMeal(SetMealDto setMealDto);


    void deleteSetMeal(List<Long> ids);
}

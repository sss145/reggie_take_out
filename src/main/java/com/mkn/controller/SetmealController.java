package com.mkn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mkn.common.R;
import com.mkn.dto.SetMealDto;
import com.mkn.entity.Setmeal;
import com.mkn.entity.SetmealDish;
import com.mkn.service.CategoryService;
import com.mkn.service.SetmealDishService;
import com.mkn.service.SetmealService;
import com.mkn.vo.SetMealVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/page")
    public R<IPage<SetMealVo>> page(Integer page, Integer pageSize,String keyWord){
        IPage<Setmeal> setmealPage = new Page<>(page,pageSize);
        IPage<SetMealVo> setMealVoIPage = new Page<>();
        setmealService.page(setmealPage,new LambdaQueryWrapper<Setmeal>()
                .like(StringUtils.isNotEmpty(keyWord),Setmeal::getName,keyWord)
                .orderByDesc(Setmeal::getUpdateTime));

        BeanUtils.copyProperties(setmealPage,setMealVoIPage,"records");
        List<SetMealVo> collect = setmealPage.getRecords().stream()
                .map(setmeal -> {
                    SetMealVo setMealVo = new SetMealVo();
                    BeanUtils.copyProperties(setmeal, setMealVo);
                    setMealVo.setCategoryName(categoryService.getById(setmeal.getCategoryId()).getName());
                    return setMealVo;
                })
                .collect(Collectors.toList());
        setMealVoIPage.setRecords(collect);

        return R.success(setMealVoIPage);
    }

    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R saveSetMeal(@RequestBody SetMealDto setMealDto){
        setmealService.saveSetMeal(setMealDto);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<SetMealDto> get(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        List<SetmealDish> setmealDishes = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));
        SetMealDto setMealDto = new SetMealDto();
        BeanUtils.copyProperties(setmeal,setMealDto);
        setMealDto.setSetmealDishes(setmealDishes);
        return R.success(setMealDto);
    }

    @PutMapping
    public R update(@RequestBody SetMealDto setMealDto){
        setmealService.updateSetMeal(setMealDto);
        return R.success();
    }

    @PostMapping("/status/{status}")
    public R setmealStatusByStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        List<Setmeal> collect = ids.stream()
                .map(id -> new Setmeal().setStatus(status).setId(id))
                .collect(Collectors.toList());
        setmealService.updateBatchById(collect);
        return R.success();
    }


    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R delete(@RequestParam List<Long> ids){
        setmealService.deleteSetMeal(ids);
        return R.success();
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        List<Setmeal> list = setmealService.list(new LambdaQueryWrapper<Setmeal>()
                .eq(Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime));
        return R.success(list);

    }
}

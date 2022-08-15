package com.mkn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mkn.common.R;
import com.mkn.dto.DishDto;
import com.mkn.entity.Category;
import com.mkn.entity.Dish;
import com.mkn.entity.DishFlavor;
import com.mkn.service.CategoryService;
import com.mkn.service.DishFlavorService;
import com.mkn.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //需要在菜品数据之外新加一个菜品分类的数据
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        //分页查询菜品信息
        dishService.page(pageInfo,new LambdaQueryWrapper<Dish>()
                    .eq(name!=null,Dish::getName,name)
                    .orderByAsc(Dish::getSort));

        //深拷贝菜品分页对象的分页数据
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //将菜品数据和菜品分类数据整合到对象中
        List<DishDto> dishDtoRecords = pageInfo.getRecords()
                .stream()
                .map(dish -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(dish,dishDto);
                    String categoryName = categoryService.getById(dish.getCategoryId()).getName();
                    return dishDto.setCategoryName(categoryName);
                })
                .collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoRecords);
        return R.success(dishDtoPage);
    }

    @PostMapping
    public R<Dish> saveDish(@RequestBody DishDto dishDto){
        log.info("{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        String name = categoryService.getById(dish.getCategoryId()).getName();
        dishDto.setCategoryName(name);
        List<DishFlavor> dishFlavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId()));
        dishDto.setFlavors(dishFlavors);
        return R.success(dishDto);
    }

    @PutMapping
    public R<Dish> updateDish(@RequestBody DishDto dishDto){
        log.info("{}",dishDto);
        dishService.updateWithFlavor(dishDto);
        //清理所有菜品的缓存数据
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success();
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        //先从redis中获取缓存数据
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //如果存在,直接返回,无需查询数据库
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList !=null){
            return R.success(dishDtoList);
        }
        List<Dish> list = dishService.list(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, dish.getCategoryId()));
        dishDtoList = list.stream()
                .map(item -> {
                    DishDto dishDto  =new DishDto();
                    BeanUtils.copyProperties(item,dishDto);
                    dishDto.setFlavors(dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,item.getId())));
                    return dishDto;
                })
                .collect(Collectors.toList());
        //如果不存在,需要查询数据库,将查询到的菜品数据缓存到Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,1, TimeUnit.HOURS);
        return R.success(dishDtoList);
    }
}

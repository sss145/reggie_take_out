package com.mkn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mkn.common.BaseContext;
import com.mkn.common.R;
import com.mkn.entity.ShoppingCart;
import com.mkn.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, BaseContext.getCurrentId()));
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:{}",shoppingCart);

        //设置用户id,指定购物车中的是哪个用户的数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询当前菜品或者套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        if (shoppingCart.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else if (shoppingCart.getSetmealId()!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart exists = shoppingCartService.getOne(queryWrapper);
        if (exists != null) {
            shoppingCartService.updateById(exists.setNumber(exists.getNumber()+1));
            return R.success(exists);
        }
        shoppingCartService.save(shoppingCart);
        return R.success(shoppingCart);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart one = shoppingCartService.getOne(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                .eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId, shoppingCart.getSetmealId()));
        if (one.getNumber()>1){
            shoppingCartService.updateById(one.setNumber(one.getNumber()-1));
            return R.success(one);
        }else {
            shoppingCartService.removeById(one.setNumber(one.getNumber()-1));
            return R.success(one);
        }

    }

    @DeleteMapping("/clean")
    public R clean(){
        shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,BaseContext.getCurrentId()));
        return R.success();
    }
}

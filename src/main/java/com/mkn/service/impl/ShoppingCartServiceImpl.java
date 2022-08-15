package com.mkn.service.impl;

import com.mkn.entity.ShoppingCart;
import com.mkn.mapper.ShoppingCartMapper;
import com.mkn.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}

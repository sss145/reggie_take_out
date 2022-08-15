package com.mkn.service.impl;

import com.mkn.entity.OrderDetail;
import com.mkn.mapper.OrderDetailMapper;
import com.mkn.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}

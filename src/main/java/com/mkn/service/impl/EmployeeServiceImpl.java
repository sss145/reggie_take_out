package com.mkn.service.impl;

import com.mkn.entity.Employee;
import com.mkn.mapper.EmployeeMapper;
import com.mkn.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}

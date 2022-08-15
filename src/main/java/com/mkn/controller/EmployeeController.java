package com.mkn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mkn.common.R;
import com.mkn.entity.Employee;
import com.mkn.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.PrintStream;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info(password);
        PrintStream out = System.out;
        //2.根据页面提交的用户名查询数据库
        Employee emp = employeeService.getOne(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getUsername,employee.getUsername()));
        //3.如果没有查询到则返回登陆失败
        if (emp == null){
            return R.error("用户不存在");
        }
        //4.密码比对,如果不一致则返回登录失败结果
        if (!password.equals(emp.getPassword())){
            return R.error("用户名或密码错误");
        }
        if (emp.getStatus()==0){
            return R.error("该用户已被禁用");
        }
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("employee",null);
        return R.success();
    }

    @PostMapping
    public R addEmployee(@RequestBody Employee employee,HttpServletRequest request){
        Long eid = (Long) request.getSession().getAttribute("employee");
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        boolean save = employeeService.save(employee);
        if (save){
            return R.success();
        }
        return R.error("添加失败!");
    }

    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page,Integer pageSize, String name){
        //分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        employeeService.page(pageInfo,new LambdaQueryWrapper<Employee>() //Lambda条件构造器
                .like(StringUtils.isNotEmpty(name),Employee::getName,name) //模糊查询,name为空则忽略
                .orderByDesc(Employee::getUpdateTime)); //降序排序
        return R.success(pageInfo);
    }

    @PutMapping
    public R enableOrDisableEmployee(@RequestBody Employee employee){
        employeeService.updateById(employee);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id来查询员工信息...{${}}",id);
        Employee emp = employeeService.getById(id);
        if (emp!=null){
            return R.success(emp);
        }
        return R.error("没有查询到对应员工信息");
    }
}

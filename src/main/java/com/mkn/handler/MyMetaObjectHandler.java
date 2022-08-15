package com.mkn.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.mkn.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author ：mkn
 * @date ：Created in 2022/8/5 14:55
 * @description：TODO
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert field....");
        this.setFieldValByName("createTime", LocalDateTime.now(),metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(),metaObject);
        this.setFieldValByName("createUser", getEmployeeId(),metaObject);
        this.setFieldValByName("updateUser", getEmployeeId(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

//        log.info("公共字段填充,线程id为:{}",Thread.currentThread().getId());
        this.setFieldValByName("updateTime", LocalDateTime.now(),metaObject);
        this.setFieldValByName("updateUser", getEmployeeId(),metaObject);

    }

    private Long getEmployeeId(){
        return BaseContext.getCurrentId();
    }
}

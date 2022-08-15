package com.mkn.vo;

import com.mkn.entity.Setmeal;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ：mkn
 * @date ：Created in 2022/8/9 8:24
 * @description：TODO
 */
@Data
@Accessors(chain = true)
public class SetMealVo extends Setmeal {

    private String categoryName;

}

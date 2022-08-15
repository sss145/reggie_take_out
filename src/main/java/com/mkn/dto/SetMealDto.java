package com.mkn.dto;

import com.mkn.entity.Setmeal;
import com.mkn.entity.SetmealDish;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author ：mkn
 * @date ：Created in 2022/8/9 9:34
 * @description：TODO
 */
@Data
@Accessors(chain = true)
public class SetMealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

}

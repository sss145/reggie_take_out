package com.mkn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mkn.common.R;
import com.mkn.entity.Category;
import com.mkn.entity.Employee;
import com.mkn.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author mkn
 * @since 2022-08-02
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        categoryService.page(pageInfo,new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        return R.success(pageInfo);
    }

    @PostMapping
    public R<Category> saveDishCategory(@RequestBody Category category){
        if (category!=null){
            categoryService.save(category);
            return R.success();
        }
        return R.error("添加失败!");
    }

    @PutMapping
    public R<Category> updateCategory(@RequestBody Category category){
        if (category!=null){
            categoryService.updateById(category);
            return R.success();
        }
        return R.error("修改失败");
    }

    @DeleteMapping
    public R<Category> deleteCategory(@RequestParam List<Long> ids){
//        if (categoryService.removeByIds(ids)){
//            return R.success();
//        }
        categoryService.remove(ids.get(0));
        return R.success();
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>()
                .eq(category.getType() != null, Category::getType, category.getType())
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime));
        return R.success(list);
    }
}

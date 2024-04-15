package com.xuyuan.dto;
import com.xuyuan.entity.Setmeal;
import com.xuyuan.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    //套餐关联菜品的集合
    private List<SetmealDish> setmealDishes;

    private String categoryName;

}

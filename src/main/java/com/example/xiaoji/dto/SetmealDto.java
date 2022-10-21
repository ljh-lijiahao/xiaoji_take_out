package com.example.xiaoji.dto;

import com.example.xiaoji.entity.Setmeal;
import com.example.xiaoji.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

package com.example.xiaoji.dto;

import com.example.xiaoji.entity.Dish;
import com.example.xiaoji.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

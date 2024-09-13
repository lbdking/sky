package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServerImpl implements DishServer {
    /**
     * 新增菜品和对应口味
     *
     * @param dishDTO
     */
    private final DishFlavorMapper dishFlavorMapper;
    private final DishMapper dishMapper;


    public DishServerImpl(DishFlavorMapper dishFlavorMapper, DishMapper dishMapper) {
        this.dishFlavorMapper = dishFlavorMapper;
        this.dishMapper = dishMapper;
    }

    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //新增菜品数据
        dishMapper.insert(dish);
        //获取主键值
        Long dishId = dish.getId();
        //插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0) {
            for (DishFlavor f : flavors) {
                f.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}

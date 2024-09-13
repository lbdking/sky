package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishServer {

    /**
     * 新增菜品和对应口味数据
     * @param dishDTO
     */
    void save(DishDTO dishDTO);
}

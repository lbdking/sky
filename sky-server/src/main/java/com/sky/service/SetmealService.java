package com.sky.service;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐以及对应菜品
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 套餐停售启售
     * @param status
     * @param setmealId
     */
    void status(Integer status, Long setmealId);
}

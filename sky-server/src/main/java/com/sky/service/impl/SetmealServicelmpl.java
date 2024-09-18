package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class SetmealServicelmpl implements SetmealService {

    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final HttpMessageConverters messageConverters;

    public SetmealServicelmpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper, HttpMessageConverters messageConverters) {
        this.setmealMapper = setmealMapper;
        this.setmealDishMapper = setmealDishMapper;
        this.messageConverters = messageConverters;
    }


    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.save(setmeal);

        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.saveStemealDish(setmealDishes);

    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        long total = page.getTotal();
        List<Setmeal> setmeals = page.getResult();
        return new PageResult(total, setmeals);
    }

    /**
     * 批量删除套餐以及对应菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            //判断套餐是否在售
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus().equals(StatusConstant.DISABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除对应套餐
        setmealMapper.delete(ids);
        //删除套餐对应菜品
        setmealDishMapper.delete(ids);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {

    }
}

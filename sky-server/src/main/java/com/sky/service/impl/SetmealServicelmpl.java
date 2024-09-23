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
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServicelmpl implements SetmealService {

    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final HttpMessageConverters messageConverters;
    private final DishMapper dishMapper;

    public SetmealServicelmpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper, HttpMessageConverters messageConverters, DishMapper dishMapper) {
        this.setmealMapper = setmealMapper;
        this.setmealDishMapper = setmealDishMapper;
        this.messageConverters = messageConverters;
        this.dishMapper = dishMapper;
    }


    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
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
            if(setmeal.getStatus() == (StatusConstant.ENABLE)){
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
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        Long setmealId = setmealDTO.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //删除再增加
        //setmealMapper.deleteOne(setmealMapper.getById(setmealId));
        setmealDishMapper.deleteOne(setmealMapper.getById(setmealId));

        //setmealMapper.save(setmeal);
        //Long setmealId = setmeal.getId();
        //setmealDishes.forEach();
        //setmealDishMapper.saveStemealDish(setmealDishes);
        setmealMapper.update(setmeal);
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.saveStemealDish(setmealDishes);
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> list = setmealDishMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    /**
     * 套餐停售启售
     *
     * @param status
     * @param setmealId
     */
    @Override
    public void status(Integer status, Long setmealId) {
        //如果套餐启售，套餐里的菜品不能有停售的
        if(status==StatusConstant.ENABLE){
            List<SetmealDish> setmealDishes = setmealDishMapper.getById(setmealId);
            for (SetmealDish setmealDish : setmealDishes) {
                Long dishId = setmealDish.getDishId();
                if(dishMapper.getById(dishId).getStatus() == StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }

            }
        }
        //套餐停售
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(setmealId);
        setmealMapper.update(setmeal);

    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}

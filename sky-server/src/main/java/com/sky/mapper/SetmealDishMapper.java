package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     *///select setmeal id from setmeal dish where dish_id in (1,2,3,4,)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 保存套餐对应菜品
     * @param setmealDishes
     */
    void saveStemealDish(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除对应菜品
     * @param ids
     */
    void delete(List<Long> ids);


    /**
     * 根据套餐id查询对应菜品
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getById(Long setmealId);

    /**
     * 根据套餐删除对应菜品
     * @param setmeal
     */
    void deleteOne(Setmeal setmeal);

    /**
     * 修改套餐对应菜品
     * @param setmeal
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);
}

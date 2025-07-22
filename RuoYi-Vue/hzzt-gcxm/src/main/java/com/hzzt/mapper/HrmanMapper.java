package com.hzzt.mapper;

import com.hzzt.domain.Hrman;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;
import java.util.List;

/**
 * @author Mr.Peng
 * @description 针对表【hrman】的数据库操作Mapper
 * 该接口定义了对员工（Hrman）表的数据库操作，包括增、删、改、查等功能。
 * @createDate 2025-02-27 15:13:42
 * @Entity com.hzzt.domain.Hrman
 */

@Mapper
public interface HrmanMapper extends BaseMapper<Hrman> {
    /**
     * 查询指定ID的员工信息
     *
     * @param id 员工表主键ID
     * @return 返回对应ID的员工信息
     */
    public Hrman selectHrmanById(Long id);

    /**
     * 查询员工列表
     *
     * @param hrman 查询条件，封装员工对象
     * @return 返回符合条件的员工集合
     */
    public List<Hrman> selectHrmanList(Hrman hrman);

    /**
     * 插入一条新的员工记录
     *
     * @param hrman 员工对象，包含新增员工的信息
     * @return 插入成功的记录数
     */
    public int insertHrman(Hrman hrman);

    /**
     * 更新员工信息
     *
     * @param hrman 员工对象，包含更新后的员工信息
     * @return 更新成功的记录数
     */
    public int updateHrman(Hrman hrman);

    /**
     * 删除指定ID的员工记录
     *
     * @param id 员工表主键ID
     * @return 删除成功的记录数
     */
    public int deleteHrmanById(Long id);

    /**
     * 批量删除员工记录
     *
     * @param ids 需要删除的员工记录的主键ID集合
     * @return 删除成功的记录数
     */
    public int deleteHrmanByIds(Long[] ids);

}

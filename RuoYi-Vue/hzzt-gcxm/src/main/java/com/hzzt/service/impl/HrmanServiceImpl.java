package com.hzzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzzt.domain.Hrman;
import com.hzzt.service.HrmanService;
import com.hzzt.mapper.HrmanMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Peng
 * @description 针对表【hrman】的数据库操作Service实现
 * 该类实现了HrmanService接口，提供了对员工（Hrman）表的增、删、改、查等功能的具体实现。
 * @createDate 2025-02-27 15:13:42
 */
@Service
public class HrmanServiceImpl extends ServiceImpl<HrmanMapper, Hrman>
        implements HrmanService {

    @Autowired
    private HrmanMapper hrmanMapper;

    @Override
    public List<Hrman> selectHrmanList(Hrman hrman) {
        LambdaQueryWrapper<Hrman> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(ObjectUtils.isNotEmpty(hrman.getName()), Hrman::getName, hrman.getName());
        queryWrapper.like(ObjectUtils.isNotEmpty(hrman.getCardNumber()), Hrman::getCardNumber, hrman.getCardNumber());
        queryWrapper.like(ObjectUtils.isNotEmpty(hrman.getEmploymentStatus()), Hrman::getEmploymentStatus, hrman.getEmploymentStatus());
        queryWrapper.like(ObjectUtils.isNotEmpty(hrman.getDepartment()), Hrman::getDepartment, hrman.getDepartment());


        // 新增：合同起始时间范围查询
        if (hrman.getContractPeriodStart() != null && hrman.getContractPeriodEnd() != null) {
            queryWrapper.between(Hrman::getContractPeriod, hrman.getContractPeriodStart(), hrman.getContractPeriodEnd());
        }

        // 新增：合同到期时间范围查询
        if (hrman.getContractEndDateStart() != null && hrman.getContractEndDateEnd() != null) {
            queryWrapper.between(Hrman::getContractEndDate, hrman.getContractEndDateStart(), hrman.getContractEndDateEnd());
        }

        // 设置排序条件：按照 createTime 降序排列
        queryWrapper.orderByDesc(Hrman::getCreateTime);
        return super.list(queryWrapper);
    }

    /**
     * 查询指定ID的员工信息
     *
     * @param id 员工表主键ID
     * @return 返回对应ID的员工信息
     */
//    @Override
////    public Hrman selectHrmanById(Long id) {
//        return hrmanMapper.selectHrmanById(id);
//    }

    /**
     * 查询员工列表
     *
     * @param hrman 查询条件，封装员工对象
     * @return 返回符合条件的员工集合
     */
//    @Override
//    public List<Hrman> selectHrmanList(Hrman hrman) {
//        return hrmanMapper.selectHrmanList(hrman);
//    }

    /**
     * 新增一名员工
     *
     * @param hrman 员工对象，包含新增员工的信息
     * @return 插入成功的记录数
     */
    @Override
    public int insertHrman(Hrman hrman) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        hrman.setCreateBy(username);
        hrman.setCreateTime(new Date());
//        return hrmanMapper.insertHrman(hrman);

        // 保存数据
        boolean isSave = super.save(hrman);
        if (isSave) {
            return 1; // 保存成功
        }
        // 保存失败，抛出异常
        throw new RuntimeException("保存失败！");
    }

    /**
     * 更新员工信息
     *
     * @param hrman 员工对象，包含更新后的员工信息
     * @return 更新成功的记录数
     */
    @Override
    public int updateHrman(Hrman hrman) {
        // 获取登录用户信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 设置更新人和更新时间
        hrman.setUpdateBy(user.getUserName());
        hrman.setUpdateTime(DateUtils.getNowDate());
        // 更新数据
        boolean isUpdate = super.updateById(hrman);
        if (isUpdate) {
            return 1; // 更新成功
        }
        // 更新失败，抛出异常
        throw new RuntimeException("编辑失败！");
    }

    /**
     * 批量删除员工记录
     *
     * @param ids 需要删除的员工记录的主键ID集合
     * @return 删除成功的记录数
     */
    @Override
    public int deleteHrmanByIds(Long[] ids) {
        return hrmanMapper.deleteHrmanByIds(ids);
    }

    @Override
    public boolean deleteHrmanById(String id) {
        return super.removeById(id);
    }

    /**
     * 删除指定ID的员工记录
     *
     * @param id 员工表主键ID
     * @return 删除成功的记录数
     */
//    @Override
//    public int deleteHrmanById(Long id) {
//        return hrmanMapper.deleteHrmanById(id);
//    }

    @Override
    public String importHrman(List<Hrman> hrmanList, boolean updateSupport) {
        if (CollectionUtils.isEmpty(hrmanList)) {
            throw new ServiceException("导入数据不能为空！");
        }

        String username = SecurityUtils.getUsername();
        int successNum = 0;
        StringBuilder successMsg = new StringBuilder();

        for (Hrman hrman : hrmanList) {
            try {
                // 使用Service的lambdaQuery方法
                boolean exists = this.lambdaQuery()
                        .eq(Hrman::getEmployeeNumber, hrman.getEmployeeNumber())
                        .exists();

                if (exists) {
                    if (updateSupport) {
                        hrman.setUpdateBy(username);
                        hrman.setUpdateTime(new Date());
                        this.updateById(hrman);
                    } else {
                        throw new ServiceException("存在重复员工编号: " + hrman.getEmployeeNumber());
                    }
                } else {
                    hrman.setCreateBy(username);
                    hrman.setCreateTime(new Date());
                    this.save(hrman);
                }
                successNum++;
            } catch (Exception e) {
                throw new ServiceException("导入失败：" + e.getMessage());
            }
        }
        successMsg.append("成功导入 ").append(successNum).append(" 条数据");
        return successMsg.toString();
    }

    @Override
    public List<Hrman> findAll(Hrman hrman) {
        LambdaQueryWrapper<Hrman> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ObjectUtils.isNotEmpty(hrman.getEmployeeNumber()), Hrman::getEmployeeNumber, hrman.getEmployeeNumber());
        queryWrapper.like(ObjectUtils.isNotEmpty(hrman.getName()), Hrman::getName, hrman.getName());
        queryWrapper.eq(ObjectUtils.isNotEmpty(hrman.getEmploymentStatus()), Hrman::getEmploymentStatus, hrman.getEmploymentStatus());
        queryWrapper.eq(ObjectUtils.isNotEmpty(hrman.getId()), Hrman::getId,hrman.getId());
        queryWrapper.orderByDesc(Hrman::getCreateTime);
        return super.list(queryWrapper);
    }

//    @Override
//    public boolean delete(List<Long> ids) {
//        if (ObjectUtils.isEmpty(ids)){
//            throw new ServiceException("主键Id不能为空", HttpStatus.BAD_REQUEST);
//        }
//        LambdaQueryWrapper<Hrman> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.eq(Hrman::getCardNumber, ids.get(0))  ;
//        List<Hrman> list = super.list(queryWrapper);
//        return super.removeBatchByIds(ids);
//    }

    @Override
    public Hrman selectbyid(String id) {
        if (ObjectUtils.isEmpty(id)){
            throw new ServiceException("主键Id不能为空",HttpStatus.BAD_REQUEST);
        }
        return super.getById(id);
    }

    @Override
    public List<Hrman> getContractExpiringEmployees() {
        LambdaQueryWrapper<Hrman> queryWrapper = new LambdaQueryWrapper<>();

        // 获取当前日期
        Date currentDate = new Date();

        // 获取30天后的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date thirtyDaysLater = calendar.getTime();

        // 查询条件：
        // 1. 合同到期时间不为空
        // 2. 合同到期时间大于等于当前日期
        // 3. 合同到期时间小于等于30天后的日期
        // 4. 员工状态为在职（"1"）
        queryWrapper.isNotNull(Hrman::getContractEndDate)
                .ge(Hrman::getContractEndDate, currentDate)
                .le(Hrman::getContractEndDate, thirtyDaysLater)
                .eq(Hrman::getEmploymentStatus, "1");

        return list(queryWrapper);
    }


}

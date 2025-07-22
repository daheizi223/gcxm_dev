package com.hzzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzzt.domain.Payroll;
import com.hzzt.mapper.PayrollMapper;
import com.hzzt.service.PayrollService;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Peng
 * @description 针对表【payroll(员工工资表)】的数据库操作Service实现
 * @createDate 2025-03-12 10:25:04
 */
@Service
public class PayrollServiceImpl extends ServiceImpl<PayrollMapper, Payroll>
        implements PayrollService {



    @Override
    public List<Payroll> selectPayrollList(Payroll payroll) {
        LambdaQueryWrapper<Payroll> queryWrapper = new LambdaQueryWrapper<>();
        // 根据员工编号、员工姓名、所属项目组进行模糊查询
        queryWrapper.like(ObjectUtils.isNotEmpty(payroll.getEmployeeNumber()), Payroll::getEmployeeNumber, payroll.getEmployeeNumber());
        queryWrapper.like(ObjectUtils.isNotEmpty(payroll.getName()), Payroll::getName, payroll.getName());
        queryWrapper.like(ObjectUtils.isNotEmpty(payroll.getProjectGroup()), Payroll::getProjectGroup, payroll.getProjectGroup());
        // 设置排序条件：按照 createTime 降序排列
        queryWrapper.orderByDesc(Payroll::getCreateTime);
        return super.list(queryWrapper);
    }

    @Override
    public int insertPayroll(Payroll payroll) {
        // 获取登录用户信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 设置创建人和创建时间
        payroll.setCreateBy(user.getUserName());
        payroll.setCreateTime(DateUtils.getNowDate());
        // 保存数据
        boolean isSave = super.save(payroll);
        if (isSave) {
            return 1; // 保存成功
        }
        // 保存失败，抛出异常
        throw new RuntimeException("保存失败！");
    }

    @Override
    public int updatePayroll(Payroll payroll) {
        // 获取登录用户信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 设置更新人和更新时间
        payroll.setUpdateBy(user.getUserName());
        payroll.setUpdateTime(DateUtils.getNowDate());
        // 更新数据
        boolean isUpdate = super.updateById(payroll);
        if (isUpdate) {
            return 1; // 更新成功
        }
        // 更新失败，抛出异常
        throw new RuntimeException("编辑失败！");
    }

    @Override
    public boolean deletePayrollById(String id) {
        // 直接调用 MyBatis-Plus 的 removeById 方法删除数据
        return super.removeById(id);
    }

    @Override
    public Payroll selectbyid(String id) {
        // 校验主键 ID 是否为空
        if (ObjectUtils.isEmpty(id)) {
            throw new ServiceException("主键Id不能为空", HttpStatus.BAD_REQUEST);
        }
        // 根据 ID 查询数据
        return super.getById(id);
    }
}
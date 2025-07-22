package com.hzzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzzt.domain.Hrman;
import com.hzzt.domain.Projectexpenditure;
import com.hzzt.service.ProjectexpenditureService;
import com.hzzt.mapper.ProjectexpenditureMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Mr.Peng
* @description 针对表【projectexpenditure】的数据库操作Service实现
* @createDate 2025-03-10 14:52:52
*/
@Service
public class ProjectexpenditureServiceImpl extends ServiceImpl<ProjectexpenditureMapper, Projectexpenditure>
    implements ProjectexpenditureService{

    @Override
    public List<Projectexpenditure> selectProjectexpenditureList(Projectexpenditure projectexpenditure) {
        LambdaQueryWrapper<Projectexpenditure> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ObjectUtils.isNotEmpty(projectexpenditure.getInvoiced()), Projectexpenditure::getInvoiced, projectexpenditure.getInvoiced());
        queryWrapper.eq(ObjectUtils.isNotEmpty(projectexpenditure.getReceivedPayment()), Projectexpenditure::getReceivedPayment, projectexpenditure.getReceivedPayment());
        queryWrapper.like(ObjectUtils.isNotEmpty(projectexpenditure.getProjectName()), Projectexpenditure::getProjectName, projectexpenditure.getProjectName());
        queryWrapper.like(ObjectUtils.isNotEmpty(projectexpenditure.getProjectYear()), Projectexpenditure::getProjectYear, projectexpenditure.getProjectYear());
            queryWrapper.like(ObjectUtils.isNotEmpty(projectexpenditure.getOwnerUnit()), Projectexpenditure::getOwnerUnit, projectexpenditure.getOwnerUnit());
        // 设置排序条件：按照 createTime 降序排列
        queryWrapper.orderByDesc(Projectexpenditure::getCreateTime);
        return super.list(queryWrapper);
    }

    @Override
    public int insertProjectexpenditure(Projectexpenditure projectexpenditure) {
        //获取登录信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        projectexpenditure.setCreateBy(user.getUserName());
        projectexpenditure.setCreateTime(DateUtils.getNowDate());
        boolean isSave=super.save(projectexpenditure);
       if (isSave) {
            return 1;
        }
        // 如果保存失败，可以抛出异常或返回 null
        throw new RuntimeException("保存失败！");
    }

    @Override
    public int updateProjectexpenditure(Projectexpenditure projectexpenditure) {
        //获取登录信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        projectexpenditure.setUpdateBy(user.getUserName());
        projectexpenditure.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate=super.updateById(projectexpenditure);
        if (isUpdate) {
            return 1;
        }
        // 如果保存失败，可以抛出异常或返回 null
        throw new RuntimeException("编辑失败！");
    }

    @Override
    public boolean deleteProjectexpenditureById(String id) {
        return super.removeById(id);
    }

    @Override
    public Projectexpenditure selectbyid(String id) {
        if (ObjectUtils.isEmpty(id)){
            throw new ServiceException("主键Id不能为空", HttpStatus.BAD_REQUEST);
        }
        return super.getById(id);
    }
}





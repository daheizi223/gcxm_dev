package com.hzzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzzt.domain.IncomePro;
import com.hzzt.domain.Projectexpenditure;
import com.hzzt.service.IncomeProService;
import com.hzzt.mapper.IncomeProMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Mr.Peng
* @description 针对表【income_pro】的数据库操作Service实现
* @createDate 2025-03-11 14:22:25
*/
@Service
public class IncomeProServiceImpl extends ServiceImpl<IncomeProMapper, IncomePro>
    implements IncomeProService{



    /**
     * 计算并设置支付比例和未支付金额
     */
    private void calculatePaymentDetails(IncomePro incomePro) {

        // 计算总开票金额
        double totalInvoiceAmount = 0.0;
        if (incomePro.getInvoiceAmount1() != null) {
            totalInvoiceAmount += incomePro.getInvoiceAmount1();
        }
        if (incomePro.getInvoiceAmount2() != null) {
            totalInvoiceAmount += incomePro.getInvoiceAmount2();
        }
        if (incomePro.getInvoiceAmount3() != null) {
            totalInvoiceAmount += incomePro.getInvoiceAmount3();
        }
        // 设置总开票金额
        incomePro.setTotalInvoiceAmount(totalInvoiceAmount);

        if (incomePro.getContractAmount() != null && incomePro.getContractAmount() > 0) {
            // 计算总到账金额
            double totalAmount = 0.0;
            if (incomePro.getAmountReceived1() != null) {
                totalAmount += incomePro.getAmountReceived1();
            }
            if (incomePro.getAmountReceived2() != null) {
                totalAmount += incomePro.getAmountReceived2();
            }
            if (incomePro.getAmountReceived3() != null) {
                totalAmount += incomePro.getAmountReceived3();
            }
            // 设置总到账金额
            incomePro.setTotalAmountReceived(totalAmount);

            // 计算支付比例 (总到账金额/合同金额*100)
            double ratio = (totalAmount / incomePro.getContractAmount()) * 100;
            incomePro.setPaymentRatio((int) Math.round(ratio)); // 四舍五入到整数

            // 计算未支付金额 (合同金额-总到账金额)
            double unpaidAmount = incomePro.getContractAmount() - totalAmount;
            incomePro.setUnpaidAmount(unpaidAmount);

        }
    }

    @Override
    public List<IncomePro> selectIncomeProList(IncomePro incomePro) {
        LambdaQueryWrapper<IncomePro> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(ObjectUtils.isNotEmpty(incomePro.getBusinessUnits()), IncomePro::getBusinessUnits, incomePro.getBusinessUnits());
        queryWrapper.like(ObjectUtils.isNotEmpty(incomePro.getNameOfContract()), IncomePro::getNameOfContract, incomePro.getNameOfContract());
        queryWrapper.like(ObjectUtils.isNotEmpty(incomePro.getCooperativeUnit()), IncomePro::getCooperativeUnit, incomePro.getCooperativeUnit());

        // 添加金额搜索条件
        if (incomePro.getSearchAmount() != null) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(IncomePro::getAmountReceived1, incomePro.getSearchAmount())
                    .or()
                    .eq(IncomePro::getAmountReceived2, incomePro.getSearchAmount())
                    .or()
                    .eq(IncomePro::getAmountReceived3, incomePro.getSearchAmount())
            );
        }

        // 添加开票时间范围查询
        // 使用 or 条件组合三个开票时间字段的查询
        if (incomePro.getBillingTimeStart() != null && incomePro.getBillingTimeEnd() != null) {
            queryWrapper.and(wrapper -> wrapper
                    .or(w -> w.between(IncomePro::getBillingTime1,
                            incomePro.getBillingTimeStart(), incomePro.getBillingTimeEnd()))
                    .or(w -> w.between(IncomePro::getBillingTime2,
                            incomePro.getBillingTimeStart(), incomePro.getBillingTimeEnd()))
                    .or(w -> w.between(IncomePro::getBillingTime3,
                            incomePro.getBillingTimeStart(), incomePro.getBillingTimeEnd()))
            );
        }

        // 设置排序条件：按照 createTime 降序排列
        queryWrapper.orderByDesc(IncomePro::getCreateTime);
        return super.list(queryWrapper);
    }

    @Override
    public int insertIncomePro(IncomePro incomePro) {
        //获取登录信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        incomePro.setCreateBy(user.getUserName());
        incomePro.setCreateTime(DateUtils.getNowDate());

        // 计算支付比例和未支付金额
        calculatePaymentDetails(incomePro);

        boolean isSave=super.save(incomePro);
        if (isSave) {
            return 1;
        }
        // 如果保存失败，可以抛出异常或返回 null
        throw new RuntimeException("保存失败！");
    }

    @Override
    public int updateIncomePro(IncomePro incomePro) {
        //获取登录信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        incomePro.setUpdateBy(user.getUserName());
        incomePro.setUpdateTime(DateUtils.getNowDate());

        // 计算支付比例和未支付金额
        calculatePaymentDetails(incomePro);

        boolean isUpdate=super.updateById(incomePro);
        if (isUpdate) {
            return 1;
        }
        // 如果保存失败，可以抛出异常或返回 null
        throw new RuntimeException("编辑失败！");
    }

    @Override
    public boolean deleteIncomeProById(String id) {
        return super.removeById(id);

    }

    @Override
    public IncomePro selectbyid(String id) {
        if (ObjectUtils.isEmpty(id)){
            throw new ServiceException("主键Id不能为空", HttpStatus.BAD_REQUEST);
        }
        return super.getById(id);
    }
}




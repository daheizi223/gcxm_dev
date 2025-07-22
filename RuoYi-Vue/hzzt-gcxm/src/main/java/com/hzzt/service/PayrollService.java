package com.hzzt.service;

import com.hzzt.domain.IncomePro;
import com.hzzt.domain.Payroll;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Mr.Peng
* @description 针对表【payroll(员工工资表)】的数据库操作Service
* @createDate 2025-03-12 10:25:04
*/
public interface PayrollService extends IService<Payroll> {

    public List<Payroll> selectPayrollList(Payroll payroll);

    public int insertPayroll(Payroll payroll);

    public int updatePayroll(Payroll payroll);

    public boolean deletePayrollById(String id);

    public Payroll selectbyid(String id);

}

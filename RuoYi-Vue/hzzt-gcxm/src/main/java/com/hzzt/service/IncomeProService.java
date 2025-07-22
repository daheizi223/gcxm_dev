package com.hzzt.service;

import com.hzzt.domain.IncomePro;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzzt.domain.Projectexpenditure;

import java.util.List;

/**
* @author Mr.Peng
* @description 针对表【income_pro】的数据库操作Service
* @createDate 2025-03-11 14:22:25
*/
public interface IncomeProService extends IService<IncomePro> {

    public List<IncomePro> selectIncomeProList(IncomePro incomePro);

    public int insertIncomePro(IncomePro incomePro);

    public int updateIncomePro(IncomePro incomePro);

    public boolean deleteIncomeProById(String id);

    public IncomePro selectbyid(String id);

}

package com.hzzt.service;

import com.hzzt.domain.Hrman;
import com.hzzt.domain.Projectexpenditure;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Mr.Peng
* @description 针对表【projectexpenditure】的数据库操作Service
* @createDate 2025-03-10 14:52:52
*/
public interface ProjectexpenditureService extends IService<Projectexpenditure> {

    public List<Projectexpenditure> selectProjectexpenditureList(Projectexpenditure projectexpenditure);

    public int insertProjectexpenditure(Projectexpenditure projectexpenditure);

    public int updateProjectexpenditure(Projectexpenditure projectexpenditure);

    public boolean deleteProjectexpenditureById(String id);

    public Projectexpenditure selectbyid(String id);


}

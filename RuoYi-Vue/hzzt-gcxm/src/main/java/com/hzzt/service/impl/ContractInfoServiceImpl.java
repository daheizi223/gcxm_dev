package com.hzzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzzt.domain.ContractAttachment;
import com.hzzt.domain.ContractInfo;
import com.hzzt.mapper.ContractInfoMapper;
import com.hzzt.service.IContractAttachmentService;
import com.hzzt.service.IContractInfoService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 合同信息Service业务层处理
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@Service
public class ContractInfoServiceImpl extends ServiceImpl<ContractInfoMapper, ContractInfo> implements IContractInfoService {
    
    @Autowired
    private ContractInfoMapper contractInfoMapper;

    @Autowired
    private IContractAttachmentService contractAttachmentService;

    /**
     * 查询合同信息
     * 
     * @param id 合同信息主键
     * @return 合同信息
     */
    @Override
    public ContractInfo selectContractInfoById(Long id) {
        return contractInfoMapper.selectById(id);
    }

    /**
     * 查询合同信息列表
     * 
     * @param contractInfo 合同信息
     * @return 合同信息
     */
    @Override
    public List<ContractInfo> selectContractInfoList(ContractInfo contractInfo) {
        return contractInfoMapper.selectContractInfoList(contractInfo);
    }

    /**
     * 查询合同信息详情（包含附件）
     * 
     * @param id 合同信息主键
     * @return 合同信息
     */
    @Override
    public ContractInfo selectContractInfoWithAttachments(Long id) {
        ContractInfo contractInfo = contractInfoMapper.selectContractInfoWithAttachments(id);
        if (contractInfo != null) {
            List<ContractAttachment> attachments = contractAttachmentService.selectAttachmentsByContractId(id);
            contractInfo.setAttachments(attachments);
        }
        return contractInfo;
    }

    /**
     * 新增合同信息
     * 
     * @param contractInfo 合同信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertContractInfo(ContractInfo contractInfo) {
        // 如果没有提供合同编号，自动生成
        if (StringUtils.isEmpty(contractInfo.getContractNo())) {
            contractInfo.setContractNo(generateContractNo());
        }
        
        // 设置默认状态
        if (StringUtils.isEmpty(contractInfo.getContractStatus())) {
            contractInfo.setContractStatus(ContractInfo.ContractStatus.DRAFT.getCode());
        }
        
        contractInfo.setCreateTime(DateUtils.getNowDate());
        return contractInfoMapper.insert(contractInfo);
    }

    /**
     * 修改合同信息
     * 
     * @param contractInfo 合同信息
     * @return 结果
     */
    @Override
    public int updateContractInfo(ContractInfo contractInfo) {
        contractInfo.setUpdateTime(DateUtils.getNowDate());
        return contractInfoMapper.updateById(contractInfo);
    }

    /**
     * 批量删除合同信息
     * 
     * @param ids 需要删除的合同信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteContractInfoByIds(Long[] ids) {
        // 删除相关附件
        for (Long id : ids) {
            contractAttachmentService.deleteAttachmentsByContractId(id);
        }
        return contractInfoMapper.deleteContractInfoByIds(ids);
    }

    /**
     * 删除合同信息信息
     * 
     * @param id 合同信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteContractInfoById(Long id) {
        // 删除相关附件
        contractAttachmentService.deleteAttachmentsByContractId(id);
        return contractInfoMapper.deleteById(id);
    }

    /**
     * 检查合同编号是否唯一
     * 
     * @param contractNo 合同编号
     * @param id 合同ID（修改时传入，新增时传null）
     * @return 结果 true-唯一 false-不唯一
     */
    @Override
    public boolean checkContractNoUnique(String contractNo, Long id) {
        LambdaQueryWrapper<ContractInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractInfo::getContractNo, contractNo);
        wrapper.eq(ContractInfo::getDelFlag, "0"); // 只查询未删除的记录
        if (id != null) {
            wrapper.ne(ContractInfo::getId, id);
        }
        return contractInfoMapper.selectCount(wrapper) == 0;
    }

    /**
     * 生成合同编号
     * 
     * @return 合同编号
     */
    @Override
    public String generateContractNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        
        // 查询当天已有的合同数量（只统计未删除的记录）
        String prefix = "CT" + dateStr;
        LambdaQueryWrapper<ContractInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(ContractInfo::getContractNo, prefix);
        wrapper.eq(ContractInfo::getDelFlag, "0"); // 只统计未删除的记录
        long count = contractInfoMapper.selectCount(wrapper);
        
        return prefix + String.format("%03d", count + 1);
    }

    /**
     * 统计合同数量按状态分组
     * 
     * @return 统计结果
     */
    @Override
    public List<ContractInfo> selectContractCountByStatus() {
        return contractInfoMapper.selectContractCountByStatus();
    }
}

package com.hzzt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hzzt.domain.ContractInfo;

import java.util.List;

/**
 * 合同信息Service接口
 * 
 * @author hzzt
 * @date 2025-06-26
 */
public interface IContractInfoService extends IService<ContractInfo> {

    /**
     * 查询合同信息
     * 
     * @param id 合同信息主键
     * @return 合同信息
     */
    public ContractInfo selectContractInfoById(Long id);

    /**
     * 查询合同信息列表
     * 
     * @param contractInfo 合同信息
     * @return 合同信息集合
     */
    public List<ContractInfo> selectContractInfoList(ContractInfo contractInfo);

    /**
     * 查询合同信息详情（包含附件）
     * 
     * @param id 合同信息主键
     * @return 合同信息
     */
    public ContractInfo selectContractInfoWithAttachments(Long id);

    /**
     * 新增合同信息
     * 
     * @param contractInfo 合同信息
     * @return 结果
     */
    public int insertContractInfo(ContractInfo contractInfo);

    /**
     * 修改合同信息
     * 
     * @param contractInfo 合同信息
     * @return 结果
     */
    public int updateContractInfo(ContractInfo contractInfo);

    /**
     * 批量删除合同信息
     * 
     * @param ids 需要删除的合同信息主键集合
     * @return 结果
     */
    public int deleteContractInfoByIds(Long[] ids);

    /**
     * 删除合同信息信息
     * 
     * @param id 合同信息主键
     * @return 结果
     */
    public int deleteContractInfoById(Long id);

    /**
     * 检查合同编号是否唯一
     * 
     * @param contractNo 合同编号
     * @param id 合同ID（修改时传入，新增时传null）
     * @return 结果 true-唯一 false-不唯一
     */
    public boolean checkContractNoUnique(String contractNo, Long id);

    /**
     * 生成合同编号
     * 
     * @return 合同编号
     */
    public String generateContractNo();

    /**
     * 统计合同数量按状态分组
     * 
     * @return 统计结果
     */
    public List<ContractInfo> selectContractCountByStatus();
}

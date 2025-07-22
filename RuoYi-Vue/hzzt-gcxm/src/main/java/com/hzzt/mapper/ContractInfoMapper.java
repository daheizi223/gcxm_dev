package com.hzzt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzzt.domain.ContractInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 合同信息Mapper接口
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@Mapper
public interface ContractInfoMapper extends BaseMapper<ContractInfo> {

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
    public ContractInfo selectContractInfoWithAttachments(@Param("id") Long id);

    /**
     * 根据合同编号查询合同信息
     * 
     * @param contractNo 合同编号
     * @return 合同信息
     */
    public ContractInfo selectContractInfoByContractNo(@Param("contractNo") String contractNo);

    /**
     * 批量删除合同信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteContractInfoByIds(Long[] ids);

    /**
     * 统计合同数量按状态分组
     * 
     * @return 统计结果
     */
    public List<ContractInfo> selectContractCountByStatus();
}

package com.hzzt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzzt.domain.ContractAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 合同附件Mapper接口
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@Mapper
public interface ContractAttachmentMapper extends BaseMapper<ContractAttachment> {

    /**
     * 查询合同附件列表
     * 
     * @param contractAttachment 合同附件
     * @return 合同附件集合
     */
    public List<ContractAttachment> selectContractAttachmentList(ContractAttachment contractAttachment);

    /**
     * 根据合同ID查询附件列表
     * 
     * @param contractId 合同ID
     * @return 附件列表
     */
    public List<ContractAttachment> selectAttachmentsByContractId(@Param("contractId") Long contractId);

    /**
     * 批量删除合同附件
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteContractAttachmentByIds(Long[] ids);

    /**
     * 根据合同ID删除附件
     * 
     * @param contractId 合同ID
     * @return 结果
     */
    public int deleteAttachmentsByContractId(@Param("contractId") Long contractId);
}

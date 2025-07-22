package com.hzzt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hzzt.domain.ContractAttachment;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 合同附件Service接口
 * 
 * @author hzzt
 * @date 2025-06-26
 */
public interface IContractAttachmentService extends IService<ContractAttachment> {

    /**
     * 查询合同附件
     * 
     * @param id 合同附件主键
     * @return 合同附件
     */
    public ContractAttachment selectContractAttachmentById(Long id);

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
    public List<ContractAttachment> selectAttachmentsByContractId(Long contractId);

    /**
     * 新增合同附件
     * 
     * @param contractAttachment 合同附件
     * @return 结果
     */
    public int insertContractAttachment(ContractAttachment contractAttachment);

    /**
     * 修改合同附件
     * 
     * @param contractAttachment 合同附件
     * @return 结果
     */
    public int updateContractAttachment(ContractAttachment contractAttachment);

    /**
     * 批量删除合同附件
     * 
     * @param ids 需要删除的合同附件主键集合
     * @return 结果
     */
    public int deleteContractAttachmentByIds(Long[] ids);

    /**
     * 删除合同附件信息
     * 
     * @param id 合同附件主键
     * @return 结果
     */
    public int deleteContractAttachmentById(Long id);

    /**
     * 上传附件
     * 
     * @param contractId 合同ID
     * @param file 上传的文件
     * @param fileCategory 文件分类
     * @param isMain 是否主要文件
     * @return 附件信息
     */
    public ContractAttachment uploadAttachment(Long contractId, MultipartFile file, String fileCategory, String isMain);

    /**
     * 下载附件
     * 
     * @param id 附件ID
     * @param response 响应对象
     */
    public void downloadAttachment(Long id, HttpServletResponse response);

    /**
     * 预览附件
     * 
     * @param id 附件ID
     * @param response 响应对象
     */
    public void previewAttachment(Long id, HttpServletResponse response);

    /**
     * 根据合同ID删除所有附件
     * 
     * @param contractId 合同ID
     * @return 结果
     */
    public int deleteAttachmentsByContractId(Long contractId);
}

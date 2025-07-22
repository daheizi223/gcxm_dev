package com.hzzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzzt.domain.ContractAttachment;
import com.hzzt.mapper.ContractAttachmentMapper;
import com.hzzt.service.IContractAttachmentService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 合同附件Service业务层处理
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@Service
public class ContractAttachmentServiceImpl extends ServiceImpl<ContractAttachmentMapper, ContractAttachment> implements IContractAttachmentService {
    
    @Autowired
    private ContractAttachmentMapper contractAttachmentMapper;

    @Value("${ruoyi.profile}")
    private String profile;

    /**
     * 查询合同附件
     * 
     * @param id 合同附件主键
     * @return 合同附件
     */
    @Override
    public ContractAttachment selectContractAttachmentById(Long id) {
        return contractAttachmentMapper.selectById(id);
    }

    /**
     * 查询合同附件列表
     * 
     * @param contractAttachment 合同附件
     * @return 合同附件
     */
    @Override
    public List<ContractAttachment> selectContractAttachmentList(ContractAttachment contractAttachment) {
        return contractAttachmentMapper.selectContractAttachmentList(contractAttachment);
    }

    /**
     * 根据合同ID查询附件列表
     * 
     * @param contractId 合同ID
     * @return 附件列表
     */
    @Override
    public List<ContractAttachment> selectAttachmentsByContractId(Long contractId) {
        return contractAttachmentMapper.selectAttachmentsByContractId(contractId);
    }

    /**
     * 新增合同附件
     * 
     * @param contractAttachment 合同附件
     * @return 结果
     */
    @Override
    public int insertContractAttachment(ContractAttachment contractAttachment) {
        contractAttachment.setCreateTime(DateUtils.getNowDate());
        contractAttachment.setUploadTime(DateUtils.getNowDate());
        contractAttachment.setUploadUser(SecurityUtils.getUsername());
        return contractAttachmentMapper.insert(contractAttachment);
    }

    /**
     * 修改合同附件
     * 
     * @param contractAttachment 合同附件
     * @return 结果
     */
    @Override
    public int updateContractAttachment(ContractAttachment contractAttachment) {
        contractAttachment.setUpdateTime(DateUtils.getNowDate());
        return contractAttachmentMapper.updateById(contractAttachment);
    }

    /**
     * 批量删除合同附件
     * 
     * @param ids 需要删除的合同附件主键
     * @return 结果
     */
    @Override
    public int deleteContractAttachmentByIds(Long[] ids) {
        // 删除物理文件
        for (Long id : ids) {
            ContractAttachment attachment = contractAttachmentMapper.selectById(id);
            if (attachment != null && StringUtils.isNotEmpty(attachment.getFilePath())) {
                File file = new File(profile + attachment.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        return contractAttachmentMapper.deleteContractAttachmentByIds(ids);
    }

    /**
     * 删除合同附件信息
     * 
     * @param id 合同附件主键
     * @return 结果
     */
    @Override
    public int deleteContractAttachmentById(Long id) {
        // 删除物理文件
        ContractAttachment attachment = contractAttachmentMapper.selectById(id);
        if (attachment != null && StringUtils.isNotEmpty(attachment.getFilePath())) {
            File file = new File(profile + attachment.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        return contractAttachmentMapper.deleteById(id);
    }

    /**
     * 上传附件
     * 
     * @param contractId 合同ID
     * @param file 上传的文件
     * @param fileCategory 文件分类
     * @param isMain 是否主要文件
     * @return 附件信息
     */
    @Override
    public ContractAttachment uploadAttachment(Long contractId, MultipartFile file, String fileCategory, String isMain) {
        try {
            // 验证输入参数
            if (file == null || file.isEmpty()) {
                throw new ServiceException("请选择要上传的文件");
            }
            
            if (contractId == null) {
                throw new ServiceException("合同ID不能为空");
            }
            
            System.out.println("开始上传文件: " + file.getOriginalFilename() + ", 合同ID: " + contractId);
            
            // 确保上传目录存在
            String uploadDir = profile + "/contract";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("创建上传目录: " + uploadDir);
            }
            
            // 上传文件路径 - 注意这里返回的是相对路径
            String filePath = FileUploadUtils.upload(uploadDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
            System.out.println("上传后的文件路径: " + filePath);
            
            // 确保存储相对路径（去掉profile前缀，只保留/contract开头）
            String relativePath = filePath;
            if (filePath.startsWith(profile)) {
                relativePath = filePath.substring(profile.length());
            }
            // 如果路径以/profile开头，移除这个前缀
            if (relativePath.startsWith("/profile")) {
                relativePath = relativePath.substring("/profile".length());
            }
            // 确保以/开头
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
            System.out.println("存储的相对路径: " + relativePath);
            
            // 创建附件记录
            ContractAttachment attachment = new ContractAttachment();
            attachment.setContractId(contractId);
            attachment.setFileName(FileUtils.getName(filePath));
            attachment.setFileOriginalName(file.getOriginalFilename());
            // 存储相对路径，用于后续访问
            attachment.setFilePath(relativePath);
            attachment.setFileSize(file.getSize());
            attachment.setFileType(file.getContentType());
            attachment.setFileExtension(FileUtils.getExtension(file));
            attachment.setFileCategory(fileCategory);
            attachment.setIsMain(isMain);
            attachment.setUploadUser(SecurityUtils.getUsername());
            attachment.setUploadTime(DateUtils.getNowDate());
            attachment.setCreateBy(SecurityUtils.getUsername());
            attachment.setCreateTime(DateUtils.getNowDate());
            
            contractAttachmentMapper.insert(attachment);
            System.out.println("附件记录插入成功, ID: " + attachment.getId());
            return attachment;
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 下载附件
     * 
     * @param id 附件ID
     * @param response 响应对象
     */
    @Override
    public void downloadAttachment(Long id, HttpServletResponse response) {
        ContractAttachment attachment = contractAttachmentMapper.selectById(id);
        if (attachment == null) {
            throw new ServiceException("附件不存在");
        }
        
        try {
            String filePath = profile + attachment.getFilePath();
            File file = new File(filePath);
            if (!file.exists()) {
                throw new ServiceException("文件不存在：" + filePath);
            }
            
            // 设置正确的Content-Type
            String contentType = getContentTypeByExtension(attachment.getFileExtension());
            if (StringUtils.isNotEmpty(attachment.getFileType())) {
                contentType = attachment.getFileType();
            }
            response.setContentType(contentType);
            
            // 设置文件长度
            response.setContentLengthLong(file.length());
            
            // 使用改进的文件下载方法
            FileUtils.downloadFile(filePath, attachment.getFileOriginalName(), response);
        } catch (IOException e) {
            throw new ServiceException("文件下载失败：" + e.getMessage());
        }
    }

    /**
     * 预览附件
     * 
     * @param id 附件ID
     * @param response 响应对象
     */
    @Override
    public void previewAttachment(Long id, HttpServletResponse response) {
        ContractAttachment attachment = contractAttachmentMapper.selectById(id);
        if (attachment == null) {
            throw new ServiceException("附件不存在");
        }
        
        try {
            String filePath = profile + attachment.getFilePath();
            System.out.println("预览文件路径: " + filePath);
            System.out.println("profile: " + profile);
            System.out.println("attachment.getFilePath(): " + attachment.getFilePath());
            
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("文件不存在: " + filePath);
                throw new ServiceException("文件不存在：" + filePath);
            }
            
            // 确保使用正确的Content-Type
            String contentType = getContentTypeByExtension(attachment.getFileExtension());
            if (StringUtils.isNotEmpty(attachment.getFileType())) {
                contentType = attachment.getFileType();
            }
            System.out.println("设置ContentType: " + contentType);
            
            // 设置缓存控制头
            response.setHeader("Cache-Control", "max-age=3600");
            response.setContentLengthLong(file.length());
            
            FileUtils.previewFile(filePath, contentType, response);
            System.out.println("文件预览完成");
        } catch (IOException e) {
            System.err.println("文件预览失败: " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("文件预览失败：" + e.getMessage());
        }
    }

    /**
     * 根据文件扩展名获取Content-Type
     * 
     * @param extension 文件扩展名
     * @return Content-Type
     */
    private String getContentTypeByExtension(String extension) {
        if (StringUtils.isEmpty(extension)) {
            return "application/octet-stream";
        }
        
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 根据合同ID删除所有附件
     * 
     * @param contractId 合同ID
     * @return 结果
     */
    @Override
    public int deleteAttachmentsByContractId(Long contractId) {
        // 先删除物理文件
        List<ContractAttachment> attachments = selectAttachmentsByContractId(contractId);
        for (ContractAttachment attachment : attachments) {
            if (StringUtils.isNotEmpty(attachment.getFilePath())) {
                File file = new File(profile + attachment.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        
        // 删除数据库记录
        return contractAttachmentMapper.deleteAttachmentsByContractId(contractId);
    }
}

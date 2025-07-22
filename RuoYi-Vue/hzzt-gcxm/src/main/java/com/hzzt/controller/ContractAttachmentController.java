package com.hzzt.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.hzzt.domain.ContractAttachment;
import com.hzzt.service.IContractAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 合同附件Controller
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@RestController
@RequestMapping("/contract/attachment")
public class ContractAttachmentController extends BaseController {
    
    @Autowired
    private IContractAttachmentService contractAttachmentService;

    /**
     * 查询合同附件列表
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractAttachment contractAttachment) {
        startPage();
        List<ContractAttachment> list = contractAttachmentService.selectContractAttachmentList(contractAttachment);
        return getDataTable(list);
    }

    /**
     * 根据合同ID查询附件列表
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:query')")
    @GetMapping("/listByContractId/{contractId}")
    public AjaxResult listByContractId(@PathVariable("contractId") Long contractId) {
        List<ContractAttachment> list = contractAttachmentService.selectAttachmentsByContractId(contractId);
        return success(list);
    }

    /**
     * 获取合同附件详细信息
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(contractAttachmentService.selectContractAttachmentById(id));
    }

    /**
     * 上传附件
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:upload')")
    @Log(title = "合同附件", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("contractId") Long contractId,
                           @RequestParam("file") MultipartFile file,
                           @RequestParam(value = "fileCategory", defaultValue = "ATTACHMENT") String fileCategory,
                           @RequestParam(value = "isMain", defaultValue = "0") String isMain) {
        try {
            // 验证文件是否为空
            if (file == null || file.isEmpty()) {
                return error("请选择要上传的文件");
            }
            
            // 验证合同ID
            if (contractId == null) {
                return error("合同ID不能为空");
            }
            
            ContractAttachment attachment = contractAttachmentService.uploadAttachment(contractId, file, fileCategory, isMain);
            return success(attachment);
        } catch (Exception e) {
            return error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 修改合同附件
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:edit')")
    @Log(title = "合同附件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContractAttachment contractAttachment) {
        contractAttachment.setUpdateBy(getUsername());
        return toAjax(contractAttachmentService.updateContractAttachment(contractAttachment));
    }

    /**
     * 删除合同附件
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:remove')")
    @Log(title = "合同附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(contractAttachmentService.deleteContractAttachmentByIds(ids));
    }

    /**
     * 下载附件
     */
    @Log(title = "合同附件", businessType = BusinessType.OTHER)
    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            contractAttachmentService.downloadAttachment(id, response);
        } catch (Exception e) {
            logger.error("附件下载失败", e);
        }
    }

    /**
     * 预览附件
     */
    @Log(title = "合同附件", businessType = BusinessType.OTHER)
    @GetMapping("/preview/{id}")
    public void preview(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            contractAttachmentService.previewAttachment(id, response);
        } catch (Exception e) {
            logger.error("附件预览失败", e);
        }
    }
}

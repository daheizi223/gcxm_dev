package com.hzzt.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.hzzt.domain.ContractInfo;
import com.hzzt.service.IContractInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 合同信息Controller
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@RestController
@RequestMapping("/contract")
public class ContractInfoController extends BaseController {
    
    @Autowired
    private IContractInfoService contractInfoService;

    /**
     * 查询合同信息列表
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractInfo contractInfo) {
        startPage();
        List<ContractInfo> list = contractInfoService.selectContractInfoList(contractInfo);
        return getDataTable(list);
    }

    /**
     * 导出合同信息列表
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:export')")
    @Log(title = "合同信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ContractInfo contractInfo) {
        List<ContractInfo> list = contractInfoService.selectContractInfoList(contractInfo);
        ExcelUtil<ContractInfo> util = new ExcelUtil<ContractInfo>(ContractInfo.class);
        util.exportExcel(response, list, "合同信息数据");
    }

    /**
     * 获取合同信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(contractInfoService.selectContractInfoWithAttachments(id));
    }

    /**
     * 新增合同信息
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:add')")
    @Log(title = "合同信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ContractInfo contractInfo) {
        // 检查合同编号唯一性
        if (!contractInfoService.checkContractNoUnique(contractInfo.getContractNo(), null)) {
            return error("新增合同'" + contractInfo.getContractName() + "'失败，合同编号已存在");
        }
        contractInfo.setCreateBy(getUsername());
        return toAjax(contractInfoService.insertContractInfo(contractInfo));
    }

    /**
     * 修改合同信息
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:edit')")
    @Log(title = "合同信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContractInfo contractInfo) {
        // 检查合同编号唯一性
        if (!contractInfoService.checkContractNoUnique(contractInfo.getContractNo(), contractInfo.getId())) {
            return error("修改合同'" + contractInfo.getContractName() + "'失败，合同编号已存在");
        }
        contractInfo.setUpdateBy(getUsername());
        return toAjax(contractInfoService.updateContractInfo(contractInfo));
    }

    /**
     * 删除合同信息
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:remove')")
    @Log(title = "合同信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(contractInfoService.deleteContractInfoByIds(ids));
    }

    /**
     * 生成合同编号
     */
    @PreAuthorize("@ss.hasPermi('gcxm:contract:add')")
    @GetMapping("/generateContractNo")
    public AjaxResult generateContractNo() {
        return success(contractInfoService.generateContractNo());
    }

    /**
     * 检查合同编号唯一性
     */
    @GetMapping("/checkContractNoUnique")
    public AjaxResult checkContractNoUnique(@RequestParam String contractNo, @RequestParam(required = false) Long id) {
        return success(contractInfoService.checkContractNoUnique(contractNo, id));
    }

    /**
     * 统计合同数量按状态分组
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(contractInfoService.selectContractCountByStatus());
    }
}

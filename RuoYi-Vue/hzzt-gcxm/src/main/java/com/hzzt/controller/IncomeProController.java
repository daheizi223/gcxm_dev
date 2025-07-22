package com.hzzt.controller;


import com.hzzt.domain.IncomePro;
import com.hzzt.domain.Projectexpenditure;
import com.hzzt.service.IncomeProService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static com.ruoyi.common.utils.PageUtils.startPage;

@RestController
@RequestMapping("/income")
@CrossOrigin
public class IncomeProController extends BaseController {

    @Autowired
    private IncomeProService incomeProService;


    @PreAuthorize("@ss.hasPermi('income:list')")
    @GetMapping("/list")
    public TableDataInfo list(IncomePro incomePro) {
        startPage();
        List<IncomePro> list = incomeProService.selectIncomeProList(incomePro);
        return getDataTable(list);
    }

//    @PreAuthorize("@ss.hasPermi('income:add')")
    @Log(title = "新增收入信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IncomePro incomePro) {
        return toAjax(incomeProService.insertIncomePro(incomePro));
    }

//    @PreAuthorize("@ss.hasPermi('income:edit')")
    @Log(title = "修改收入信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IncomePro incomePro) {
        return toAjax(incomeProService.updateIncomePro(incomePro));
    }


//    @PreAuthorize("@ss.hasPermi('income:remove')")
    @Log(title = "删除收入信息", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult delete(@RequestBody String id)throws ServiceException {
        return toAjax(incomeProService.deleteIncomeProById(id));
    }

//    @PreAuthorize("@ss.hasPermi('income:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(incomeProService.selectbyid(id));
    }


//    @PreAuthorize("@ss.hasPermi('income:export')")
    @Log(title = "收入数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IncomePro incomePro) {
        List<IncomePro> list = incomeProService.selectIncomeProList(incomePro);
        ExcelUtil<IncomePro> util = new ExcelUtil<IncomePro>(IncomePro.class);
        util.exportExcel(response, list, "收入数据");
    }

    @GetMapping("/template")  // 修改路径避免冲突
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<IncomePro> util = new ExcelUtil<>(IncomePro.class);
        util.importTemplateExcel(response, "收入数据模板");
    }


    @PostMapping("/importData")
//    @PreAuthorize("@ss.hasPermi('income:import')")
    @Log(title = "收入信息导入", businessType = BusinessType.IMPORT)
    @ResponseBody
    public AjaxResult importData(MultipartFile file) throws Exception
    {
//        ExcelUtil<IncomePro> util = new ExcelUtil<>(IncomePro.class);
//        List<IncomePro> list = util.importExcel(file.getInputStream());
//        String username = getUsername();
//        Date now = new Date();
//        for (IncomePro item : list) {
//            item.setCreateBy(username);
//            item.setCreateTime(now);
//        }
//        boolean result = incomeProService.saveBatch(list);
//        return result ? AjaxResult.success("成功导入" + list.size() + "条数据")
//                : AjaxResult.error("导入失败");



        ExcelUtil<IncomePro> util = new ExcelUtil<>(IncomePro.class);
        List<IncomePro> list = util.importExcel(file.getInputStream());
        String username = getUsername();
        Date now = new Date();

        int successCount = 0;
        StringBuilder errorMsg = new StringBuilder();

        // 逐条处理数据
        for (IncomePro item : list) {
            try {
                item.setCreateBy(username);
                item.setCreateTime(now);
                // 使用现有的插入方法，该方法中已包含计算逻辑
                if (incomeProService.insertIncomePro(item) > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录失败的数据
                errorMsg.append("行数据处理失败：").append(e.getMessage()).append("\n");
            }
        }

        if (successCount == list.size()) {
            return AjaxResult.success("成功导入 " + successCount + " 条数据");
        } else {
            return AjaxResult.success("成功导入 " + successCount + " 条数据，失败 " +
                    (list.size() - successCount) + " 条\n" + errorMsg.toString());
        }
    }

}

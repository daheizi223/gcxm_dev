package com.hzzt.controller;

import com.hzzt.domain.Payroll;
import com.hzzt.service.PayrollService;
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
@RequestMapping("/payroll")
@CrossOrigin
public class PayrollController extends BaseController {

    @Autowired
    private PayrollService payrollService;

    @PreAuthorize("@ss.hasPermi('payroll:list')")
    @GetMapping("/list")
    public TableDataInfo list(Payroll payroll) {
        startPage();
        List<Payroll> list = payrollService.selectPayrollList(payroll);
        return getDataTable(list);
    }

//    @PreAuthorize("@ss.hasPermi('payroll:add')")
    @Log(title = "新增员工工资信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Payroll payroll) throws ServiceException{
        return toAjax(payrollService.insertPayroll(payroll));
    }

//    @PreAuthorize("@ss.hasPermi('payroll:edit')")
    @Log(title = "修改员工工资信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Payroll payroll) throws ServiceException{
        return toAjax(payrollService.updatePayroll(payroll));
    }

//    @PreAuthorize("@ss.hasPermi('payroll:remove')")
    @Log(title = "删除员工工资信息", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult delete(@RequestBody String id) throws ServiceException {
        return toAjax(payrollService.deletePayrollById(id));
    }

//    @PreAuthorize("@ss.hasPermi('payroll:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) throws ServiceException{
        return success(payrollService.selectbyid(id));
    }

//    @PreAuthorize("@ss.hasPermi('payroll:export')")
    @Log(title = "员工工资数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Payroll payroll) {
        List<Payroll> list = payrollService.selectPayrollList(payroll);
        ExcelUtil<Payroll> util = new ExcelUtil<>(Payroll.class);
        util.exportExcel(response, list, "员工工资数据");
    }

    @GetMapping("/template")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Payroll> util = new ExcelUtil<>(Payroll.class);
        util.importTemplateExcel(response, "员工工资数据模板");
    }

    @PostMapping("/importData")
//    @PreAuthorize("@ss.hasPermi('payroll:import')")
    @Log(title = "员工工资信息导入", businessType = BusinessType.IMPORT)
    @ResponseBody
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<Payroll> util = new ExcelUtil<>(Payroll.class);
        List<Payroll> list = util.importExcel(file.getInputStream());
        String username = getUsername();
        Date now = new Date();
        for (Payroll item : list) {
            item.setCreateBy(username);
            item.setCreateTime(now);
        }
        boolean result = payrollService.saveBatch(list);
        return result ? AjaxResult.success("成功导入" + list.size() + "条数据")
                : AjaxResult.error("导入失败");
    }
}
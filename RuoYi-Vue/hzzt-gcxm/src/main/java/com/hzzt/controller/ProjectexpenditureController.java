package com.hzzt.controller;


import com.hzzt.domain.Hrman;
import com.hzzt.domain.Projectexpenditure;
import com.hzzt.service.ProjectexpenditureService;
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

@RestController
@RequestMapping("/Projectexpenditure")
@CrossOrigin
public class ProjectexpenditureController extends BaseController {

    @Autowired
    private ProjectexpenditureService projectexpenditureService;


    @PreAuthorize("@ss.hasPermi('Projectexpenditure:list')")
    @GetMapping("/list")
    public TableDataInfo list(Projectexpenditure projectexpenditure) {
        startPage();
        List<Projectexpenditure> list = projectexpenditureService.selectProjectexpenditureList(projectexpenditure);
        return getDataTable(list);
    }

//    @PreAuthorize("@ss.hasPermi('Projectexpenditure:add')")
    @Log(title = "新增支出信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Projectexpenditure projectexpenditure) {
        return toAjax(projectexpenditureService.insertProjectexpenditure(projectexpenditure));
    }


//    @PreAuthorize("@ss.hasPermi('Projectexpenditure:edit')")
    @Log(title = "修改支出信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Projectexpenditure projectexpenditure) {
        return toAjax(projectexpenditureService.updateProjectexpenditure(projectexpenditure));
    }


//    @PreAuthorize("@ss.hasPermi('Projectexpenditure:remove')")
    @Log(title = "删除支出信息", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult delete(@RequestBody String id)throws ServiceException {
        return toAjax(projectexpenditureService.deleteProjectexpenditureById(id));
    }

//    @PreAuthorize("@ss.hasPermi('Projectexpenditure:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(projectexpenditureService.selectbyid(id));
    }


//    @PreAuthorize("@ss.hasPermi('Projectexpenditure:export')")
    @Log(title = "支出数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Projectexpenditure projectexpenditure) {
        List<Projectexpenditure> list = projectexpenditureService.selectProjectexpenditureList(projectexpenditure);
        ExcelUtil<Projectexpenditure> util = new ExcelUtil<Projectexpenditure>(Projectexpenditure.class);
        util.exportExcel(response, list, "支出数据");
    }


    @GetMapping("/template")  // 修改路径避免冲突
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Projectexpenditure> util = new ExcelUtil<>(Projectexpenditure.class);
        util.importTemplateExcel(response, "支出数据模板");
    }

    @PostMapping("/importData")
//    @PreAuthorize("@ss.hasPermi('Projectexpenditure:import')")
    @Log(title = "支出信息导入", businessType = BusinessType.IMPORT)
    @ResponseBody
    public AjaxResult importData(MultipartFile file) throws Exception
    {
        ExcelUtil<Projectexpenditure> util = new ExcelUtil<>(Projectexpenditure.class);
        List<Projectexpenditure> list = util.importExcel(file.getInputStream());
        String username = getUsername();
        Date now = new Date();
        for (Projectexpenditure item : list) {
            item.setCreateBy(username);
            item.setCreateTime(now);
        }
        boolean result = projectexpenditureService.saveBatch(list);
        return result ? AjaxResult.success("成功导入" + list.size() + "条数据")
                : AjaxResult.error("导入失败");
    }
}

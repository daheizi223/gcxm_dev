package com.hzzt.controller;

import com.hzzt.domain.Hrman;
import com.hzzt.filedomain.EmployeeFile;
import com.hzzt.service.EmployeeFileService;
import com.hzzt.service.HrmanService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;


/**
 * @author Mr.Peng
 * @description 员工信息管理接口
 * 该控制器提供了员工信息（Hrman）相关的增、删、改、查等功能接口。
 * @createDate 2025-02-27 15:13:42
 */
@RestController
@RequestMapping("/hrman")
@CrossOrigin
public class HrmanController extends BaseController {

    @Autowired
    private HrmanService hrmanService;

    @Autowired
    private EmployeeFileService employeeFileService;

    private static final Logger log = LoggerFactory.getLogger(HrmanController.class);


    /** 允许的最大文件大小（10MB） */
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024;



    /**
     * 查询员工列表
     *
     * @param hrman 查询条件，封装员工对象
     * @return 返回符合条件的员工列表
     */
    @PreAuthorize("@ss.hasPermi('hrman:list')")
    @GetMapping("/list")
    public TableDataInfo list(Hrman hrman) {
        startPage();
        List<Hrman> list = hrmanService.selectHrmanList(hrman);
        return getDataTable(list);
    }

    /**
     * 导出员工列表数据
     *
     * @param response HTTP响应，用于返回导出的文件
     * @param hrman 查询条件，封装员工对象
     */
//    @PreAuthorize("@ss.hasPermi('hrman:export')")
    @Log(title = "员工数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Hrman hrman) {
        List<Hrman> list = hrmanService.selectHrmanList(hrman);
        ExcelUtil<Hrman> util = new ExcelUtil<Hrman>(Hrman.class);
        util.exportExcel(response, list, "员工数据");
    }

    /**
     * 获取指定ID的员工详细信息
     *
     * @param id 员工ID
     * @return 返回员工的详细信息
     */
//    @PreAuthorize("@ss.hasPermi('hrman:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(hrmanService.selectbyid(id));
    }

    /**
     * 新增员工
     *
     * @param hrman 员工信息对象
     * @return 插入结果
     */
//    @PreAuthorize("@ss.hasPermi('hrman:add')")
    @Log(title = "新增员工", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Hrman hrman) {
        return toAjax(hrmanService.insertHrman(hrman));
    }

    /**
     * 修改员工信息
     *
     * @param hrman 员工信息对象
     * @return 更新结果
     */
//    @PreAuthorize("@ss.hasPermi('hrman:edit')")
    @Log(title = "修改员工", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Hrman hrman) {
        return toAjax(hrmanService.updateHrman(hrman));
    }

    /**
     * 删除员工信息
     *
     * @param id 需要删除的员工ID
     * @return 删除结果
     */
//    @PreAuthorize("@ss.hasPermi('hrman:remove')")
    @Log(title = "删除员工", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult delete(@RequestBody String id)throws ServiceException{
        return toAjax(hrmanService.deleteHrmanById(id));
    }




    // 添加模板下载接口
    @GetMapping("/template")  // 修改路径避免冲突
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Hrman> util = new ExcelUtil<>(Hrman.class);
        util.importTemplateExcel(response, "员工数据模板");
    }


    @PostMapping("/importData")
//    @PreAuthorize("@ss.hasPermi('hrman:import')")
    @Log(title = "员工导入", businessType = BusinessType.IMPORT)
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<Hrman> util = new ExcelUtil<>(Hrman.class);
        List<Hrman> hrmanList = util.importExcel(file.getInputStream());
        String message = hrmanService.importHrman(hrmanList, updateSupport);
        return AjaxResult.success(message);
    }


    @GetMapping("/expiring-contracts")
    public TableDataInfo getExpiringContracts() {
        startPage();
        List<Hrman> list = hrmanService.getContractExpiringEmployees();
        return getDataTable(list);
    }


    /**
     * 上传文件
     */
    @PostMapping("/{employeeId}/files")
    public AjaxResult uploadFile(@PathVariable Long employeeId,
                                 @RequestParam String fileType,
                                 @RequestParam MultipartFile file) {
        try {
            // 文件大小验证
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                return AjaxResult.error("文件大小不能超过10MB");
            }

            // 文件类型验证
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                return AjaxResult.error("不支持的文件类型");
            }

            boolean success = employeeFileService.uploadFile(employeeId, fileType, file);
            return success ? AjaxResult.success("文件上传成功") : AjaxResult.error("文件上传失败");
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return AjaxResult.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取员工文件列表
     */
    @GetMapping("/{employeeId}/files")
    public AjaxResult getEmployeeFiles(@PathVariable Long employeeId) {
        List<EmployeeFile> files = employeeFileService.getFilesByEmployeeId(employeeId);
        return AjaxResult.success(files);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/files/{fileId}")
    public AjaxResult deleteFile(@PathVariable Long fileId) {
        boolean success = employeeFileService.deleteFile(fileId);
        return success ? AjaxResult.success("文件删除成功") : AjaxResult.error("文件删除失败");
    }

    /**
     * 获取文件(预览或下载)
     */
    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable Long fileId,
                                            @RequestParam(defaultValue = "false") boolean download) {
        try {
            Resource file =  employeeFileService.getFileContent(fileId);
            if (file == null || !file.exists()) {
                return ResponseEntity.notFound().build();
            }

            EmployeeFile employeeFile = employeeFileService.getFileInfo(fileId);
            String disposition = download ? "attachment" : "inline";
            String filename = URLEncoder.encode(employeeFile.getFileName(), StandardCharsets.UTF_8.toString());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(employeeFile.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            disposition + "; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(file);
        } catch (Exception e) {
            log.error("获取文件失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    private boolean isValidFileType(String contentType) {
        Set<String> allowedTypes = new HashSet<>(Arrays.asList(
                "image/jpeg",
                "image/png",
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ));
        return allowedTypes.contains(contentType);
    }
}

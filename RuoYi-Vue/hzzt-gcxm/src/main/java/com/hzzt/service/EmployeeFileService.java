package com.hzzt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hzzt.filedomain.EmployeeFile;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.List;

public interface EmployeeFileService extends IService<EmployeeFile> {

        /**
         * 上传员工文件
         * @param employeeId 员工ID
         * @param fileType 文件类型
         * @param file 上传的文件
         * @return 是否上传成功
         */
        boolean uploadFile(Long employeeId, String fileType, MultipartFile file) throws IOException;

        /**
         * 获取员工的所有文件
         * @param employeeId 员工ID
         * @return 文件列表
         */
        List<EmployeeFile> getFilesByEmployeeId(Long employeeId);

        /**
         * 删除文件
         * @param fileId 文件ID
         * @return 是否删除成功
         */
        boolean deleteFile(Long fileId);

        /**
         * 获取文件内容(用于预览或下载)
         * @param fileId 文件ID
         * @return 文件资源
         */
        Resource getFileContent(Long fileId);

        EmployeeFile getFileInfo(Long fileId);
}


package com.hzzt.filedomain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 员工文件实体类
 *
 * @author koolman121e
 * @date 2025-03-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employee_file")
public class EmployeeFile extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    private String fileType;

    private String fileName;

    private String filePath;

    private Date uploadTime;

    // Add new fields
    private Long fileSize;

    private String mimeType;

    // For preview/download URL
    @TableField(exist = false)
    private String url;
}
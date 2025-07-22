package com.hzzt.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 合同附件实体类
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("contract_attachment")
public class ContractAttachment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 合同ID */
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /** 文件原始名称 */
    @Excel(name = "原始文件名")
    @NotBlank(message = "文件原始名称不能为空")
    private String fileOriginalName;

    /** 文件路径 */
    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    /** 文件大小(字节) */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private String fileType;

    /** 文件扩展名 */
    @Excel(name = "文件扩展名")
    private String fileExtension;

    /** 文件分类 */
    @Excel(name = "文件分类", readConverterExp = "CONTRACT=合同文件,ATTACHMENT=附件,IMAGE=图片")
    private String fileCategory;

    /** 上传用户 */
    @Excel(name = "上传用户")
    private String uploadUser;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 是否主要文件 */
    @Excel(name = "是否主要文件", readConverterExp = "0=否,1=是")
    private String isMain;

    /** 排序 */
    private Integer sortOrder;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 文件分类枚举 */
    public enum FileCategory {
        CONTRACT("CONTRACT", "合同文件"),
        ATTACHMENT("ATTACHMENT", "附件"),
        IMAGE("IMAGE", "图片");

        private final String code;
        private final String desc;

        FileCategory(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}

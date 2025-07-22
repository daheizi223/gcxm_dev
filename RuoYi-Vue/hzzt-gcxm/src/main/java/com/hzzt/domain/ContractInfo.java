package com.hzzt.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 合同信息实体类
 * 
 * @author hzzt
 * @date 2025-06-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("contract_info")
public class ContractInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 合同编号 */
    @Excel(name = "合同编号")
    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    /** 合同名称 */
    @Excel(name = "合同名称")
    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    /** 主要内容 */
    @Excel(name = "主要内容")
    private String mainContent;

    /** 合同金额(元) */
    @Excel(name = "合同金额")
    private BigDecimal contractAmount;

    /** 签订时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签订时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date signingDate;

    /** 甲方 */
    @Excel(name = "甲方")
    private String partyA;

    /** 乙方 */
    @Excel(name = "乙方")
    private String partyB;

    /** 合同状态 */
    @Excel(name = "合同状态", readConverterExp = "DRAFT=草稿,SIGNED=已签订,EXECUTING=执行中,COMPLETED=已完成,TERMINATED=已终止")
    private String contractStatus;

    /** 合同开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "合同开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 合同结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "合同结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 甲方联系人 */
    @Excel(name = "甲方联系人")
    private String contactPersonA;

    /** 甲方联系电话 */
    @Excel(name = "甲方联系电话")
    private String contactPhoneA;

    /** 乙方联系人 */
    @Excel(name = "乙方联系人")
    private String contactPersonB;

    /** 乙方联系电话 */
    @Excel(name = "乙方联系电话")
    private String contactPhoneB;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 附件列表 */
    @TableField(exist = false)
    private List<ContractAttachment> attachments;

    /** 合同状态枚举 */
    public enum ContractStatus {
        DRAFT("DRAFT", "草稿"),
        SIGNED("SIGNED", "已签订"),
        EXECUTING("EXECUTING", "执行中"),
        COMPLETED("COMPLETED", "已完成"),
        TERMINATED("TERMINATED", "已终止");

        private final String code;
        private final String desc;

        ContractStatus(String code, String desc) {
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

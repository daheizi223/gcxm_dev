package com.hzzt.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import com.ruoyi.common.annotation.Excel;

/**
 *
 * @TableName projectexpenditure
 */
@Data
@TableName("projectexpenditure")
public class Projectexpenditure  extends BaseEntity {

    /**
     * 主键id
     */
    @NotNull(message="[主键id]不能为空")
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 项目年份
     */
    @ApiModelProperty("项目年份")
    @Excel(name = "项目年份")
    private Integer projectYear;

    /**
     * 项目组
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("项目组")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "项目组")
    private String projectGroup;

    /**
     * 项目名称
     */
    @NotBlank(message="[项目名称]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("项目名称")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "项目名称")
    private String projectName;

    /**
     * 业主单位
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("业主单位")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "业主单位")
    private String ownerUnit;

    /**
     * 人工成本
     */
    @ApiModelProperty("人工成本")
    @Excel(name = "人工成本")
    private BigDecimal laborCost;

    /**
     * 人员数量
     */
    @ApiModelProperty("人员数量")
    @Excel(name = "人员数量")
    private Integer personnelCount;

    /**
     * 人均成本
     */
    @ApiModelProperty("人均成本")
    @Excel(name = "人均成本")
    private BigDecimal perCapitaCost;

    /**
     * 房租
     */
    @ApiModelProperty("房租")
    @Excel(name = "房租")
    private BigDecimal rent;

    /**
     * 房租起始日
     */
    @ApiModelProperty("房租起始日")
    @Excel(name = "房租起始日", dateFormat = "yyyy-MM-dd")
    private Date rentStartDate;

    /**
     * 房租结束日
     */
    @ApiModelProperty("房租结束日")
    @Excel(name = "房租结束日", dateFormat = "yyyy-MM-dd")
    private Date rentEndDate;

    /**
     * 租车报销
     */
    @ApiModelProperty("租车报销")
    @Excel(name = "租车报销")
    private BigDecimal carReimbursement;

    /**
     * 其他报销
     */
    @ApiModelProperty("其他报销")
    @Excel(name = "其他报销")
    private BigDecimal otherReimbursements;

    /**
     * 设备采购额
     */
    @ApiModelProperty("设备采购额")
    @Excel(name = "设备采购额")
    private BigDecimal equipmentPurchaseAmount;

    /**
     * 外协成本
     */
    @ApiModelProperty("外协成本")
    @Excel(name = "外协成本")
    private BigDecimal outsourcingCost;

    /**
     * 综合均摊成本
     */
    @ApiModelProperty("综合均摊成本")
    @Excel(name = "综合均摊成本")
    private BigDecimal averageCost;

    /**
     * 资金成本
     */
    @ApiModelProperty("资金成本")
    @Excel(name = "资金成本")
    private BigDecimal capitalCost;

    /**
     * 成本汇总
     */
    @ApiModelProperty("成本汇总")
    @Excel(name = "成本汇总")
    private BigDecimal summaryCost;

    /**
     * 产值
     */
    @ApiModelProperty("产值")
    @Excel(name = "产值")
    private BigDecimal productionValue;

    /**
     * 利润
     */
    @ApiModelProperty("利润")
    @Excel(name = "利润")
    private BigDecimal profit;

    /**
     * 已开票
     */
    @ApiModelProperty("已开票")
    @Excel(name = "开票状态")
    private Integer invoiced;

    /**
     * 已回款
     */
    @ApiModelProperty("已回款")
    @Excel(name = "回款状态")
    private Integer receivedPayment;

    /**
     * 利润率
     */
    @ApiModelProperty("利润率")
    @Excel(name = "利润率")
    private BigDecimal profitRate;

    /**
     * 创建人
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("创建人")
    @Length(max= 255,message="编码长度不能超过255")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改人
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("修改人")
    @Length(max= 255,message="编码长度不能超过255")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;


    @ApiModelProperty("备注")
    @Excel(name = "备注")
    private String remark;

}
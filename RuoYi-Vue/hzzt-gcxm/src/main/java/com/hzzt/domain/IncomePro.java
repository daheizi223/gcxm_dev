package com.hzzt.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @TableName income_pro
 */

@Data
@TableName("income_pro")
public class IncomePro extends BaseEntity {

    /**
     * 主键id
     */
    @NotBlank(message="[主键id]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("主键id")
    @Length(max= 255,message="编码长度不能超过255")
    private String id;

    /**
     * 省份
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("省份")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "省份")
    private String provinces;

    /**
     * 城市
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("城市")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "城市")
    private String cities;

    /**
     * 合同名称
     */
    @NotBlank(message="[合同名称]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("合同名称")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "合同名称")
    private String nameOfContract;

    /**
     * 业务部门
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("业务部门")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "业务部门")
    private String businessUnits;

    /**
     * 专业
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("专业")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "专业")
    private String profession;

    /**
     * 归属年份
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("归属年份")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "归属年份")
    private String year;

    /**
     * 启动时间
     */
    @ApiModelProperty("启动时间")
    @Excel(name = "启动时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date stopTime;

    /**
     * 取费标准（合同）
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("取费标准（合同）")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "取费标准（合同）")
    private String collectionStandard;

    /**
     * 项目负责人
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("项目负责人")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "项目负责人")
    private String leader;

    /**
     * 合作单位
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("合作单位")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "合作单位")
    private String cooperativeUnit;

    /**
     * 合作单位联系人
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("合作单位联系人")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "合作单位联系人")
    private String cooperativeUnitPerson;

    /**
     * 合同签订情况
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("合同签订情况")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "合同签订情况")
    private int contractSigningSituation;

    /**
     * 合同金额（元）
     */
    @ApiModelProperty("合同金额（元）")
    @Excel(name = "合同金额（元）")
    private Double contractAmount;

    /**
     * 是否有外协
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("是否有外协")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "是否有外协")
    private int outsource;

    /**
     * 取费标准(外协)
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("取费标准(外协)")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "取费标准(外协)")
    private String collectionStandardOut;

    /**
     * 订单号
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("订单号")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "订单号")
    private String orderNumber;

    /**
     * 签订日期
     */
    @ApiModelProperty("签订日期")
    @Excel(name = "签订日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateOfSigning;

    /**
     * 确认订单号
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("确认订单号")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "确认订单号")
    private String orderNumberReal;

    /**
     * 确认签订日期
     */
    @ApiModelProperty("确认签订日期")
    @Excel(name = "确认签订日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateOfSigningReal;

    /**
     * 项目应收金额（元）
     */
    @ApiModelProperty("项目应收金额（元）")
    @Excel(name = "项目应收金额（元）")
    private Double projectReceivables;

    /**
     * 订单备注
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("订单备注")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "订单备注")
    private String orderNotes;

    /**
     * 发票编号1
     */
    @ApiModelProperty("发票编号1")
    @Excel(name = "发票编号1")
    private String invoiceNumber1;

    /**
     * 发票金额1（元）
     */
    @ApiModelProperty("发票金额1（元）")
    @Excel(name = "发票金额1（元）")
    private Double invoiceAmount1;

    /**
     * 开票时间1
     */
    @ApiModelProperty("开票时间1")
    @Excel(name = "开票时间1", width = 30, dateFormat = "yyyy-MM-dd")
    private Date billingTime1;

    /**
     * 到账金额1（元）
     */
    @ApiModelProperty("到账金额1（元）")
    @Excel(name = "到账金额1（元）")
    private Double amountReceived1;

    /**
     * 到账时间1
     */
    @ApiModelProperty("到账时间1")
    @Excel(name = "到账时间1", width = 30, dateFormat = "yyyy-MM-dd")
    private Date arrivaTime1;

    /**
     * 发票编号2
     */
    @ApiModelProperty("发票编号2")
    @Excel(name = "发票编号2")
    private String invoiceNumber2;

    /**
     * 发票金额2（元）
     */
    @ApiModelProperty("发票金额2（元）")
    @Excel(name = "发票金额2（元）")
    private Double invoiceAmount2;

    /**
     * 开票时间2
     */
    @ApiModelProperty("开票时间2")
    @Excel(name = "开票时间2", width = 30, dateFormat = "yyyy-MM-dd")
    private Date billingTime2;

    /**
     * 到账金额2（元）
     */
    @ApiModelProperty("到账金额2（元）")
    @Excel(name = "到账金额2（元）")
    private Double amountReceived2;

    /**
     * 到账时间2
     */
    @ApiModelProperty("到账时间2")
    @Excel(name = "到账时间2", width = 30, dateFormat = "yyyy-MM-dd")
    private Date arrivaTime2;

    /**
     * 发票编号3
     */
    @ApiModelProperty("发票编号3")
    @Excel(name = "发票编号3")
    private String invoiceNumber3;

    /**
     * 发票金额3（元）
     */
    @ApiModelProperty("发票金额3（元）")
    @Excel(name = "发票金额3（元）")
    private Double invoiceAmount3;

    /**
     * 开票时间3
     */
    @ApiModelProperty("开票时间3")
    @Excel(name = "开票时间3", width = 30, dateFormat = "yyyy-MM-dd")
    private Date billingTime3;

    /**
     * 到账金额3（元）
     */
    @ApiModelProperty("到账金额3（元）")
    @Excel(name = "到账金额3（元）")
    private Double amountReceived3;

    /**
     * 到账时间3
     */
    @ApiModelProperty("到账时间3")
    @Excel(name = "到账时间3", width = 30, dateFormat = "yyyy-MM-dd")
    private Date arrivaTime3;

    /**
     * 总到账金额（元）
     */
    @ApiModelProperty("总到账金额（元）")
    @Excel(name = "总到账金额（元）")
    private Double totalAmountReceived;

    /**
     * 支付比例
     */
    @ApiModelProperty("支付比例")
    @Excel(name = "支付比例")
    private Integer paymentRatio;

    /**
     * 未支付金额
     */
    @ApiModelProperty("未支付金额")
    @Excel(name = "未支付金额")
    private Double unpaidAmount;

    /**
     * 到账信息备注
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("到账信息备注")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "到账信息备注")
    private String notes;

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
     * 更新人
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("更新人")
    @Length(max= 255,message="编码长度不能超过255")
    private String updateBy;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("备注")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "备注")
    private String remark;


    @TableField(exist = false)
    private Date billingTimeStart;

    @TableField(exist = false)
    private Date billingTimeEnd;

    @TableField(exist = false)
    private Double searchAmount;  // 用于搜索金额的字段

    /**
     * 总开票金额（元）
     */
    @ApiModelProperty("总开票金额（元）")
    @Excel(name = "总开票金额（元）")
    private Double totalInvoiceAmount;

}
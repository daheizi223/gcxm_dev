package com.hzzt.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 员工工资表
 * @TableName payroll
 */

@Data
@TableName("payroll")
public class Payroll extends BaseEntity {

    /**
     * 主键ID
     */
    @NotBlank(message="[主键ID]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("主键ID")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "主键ID")
    private String id;

    /**
     * 员工编号
     */
    @NotBlank(message="[员工编号]不能为空")
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("员工编号")
    @Length(max= 20,message="编码长度不能超过20")
    @Excel(name = "员工编号")
    private String employeeNumber;

    /**
     * 员工姓名
     */
    @NotBlank(message="[员工姓名]不能为空")
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("员工姓名")
    @Length(max= 50,message="编码长度不能超过50")
    @Excel(name = "员工姓名")
    private String name;

    /**
     * 所属项目组
     */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("所属项目组")
    @Length(max= 100,message="编码长度不能超过100")
    @Excel(name = "所属项目组")
    private String projectGroup;

    /**
     * 应出勤天数
     */
    @ApiModelProperty("应出勤天数")
    @Excel(name = "应出勤天数")
    private Integer attendanceDays;

    /**
     * 实际考勤天数
     */
    @ApiModelProperty("实际考勤天数")
    @Excel(name = "实际考勤天数")
    private Integer actualAttendanceDays;

    /**
     * 绩效考核分数
     */
    @ApiModelProperty("绩效考核分数")
    @Excel(name = "绩效考核分数")
    private BigDecimal performanceEvaluation;

    /**
     * 请假天数
     */
    @ApiModelProperty("请假天数")
    @Excel(name = "请假天数")
    private Integer leaveDays;

    /**
     * 本月扣款金额
     */
    @ApiModelProperty("本月扣款金额")
    @Excel(name = "本月扣款金额")
    private BigDecimal monthlyDeduction;

    /**
     * 职称工资
     */
    @ApiModelProperty("职称工资")
    @Excel(name = "职称工资")
    private BigDecimal titleSalary;

    /**
     * 工龄工资
     */
    @ApiModelProperty("工龄工资")
    @Excel(name = "工龄工资")
    private BigDecimal workYearsSalary;

    /**
     * 技能工资
     */
    @ApiModelProperty("技能工资")
    @Excel(name = "技能工资")
    private BigDecimal skillSalary;

    /**
     * 岗位绩效
     */
    @ApiModelProperty("岗位绩效")
    @Excel(name = "岗位绩效")
    private BigDecimal positionPerformance;

    /**
     * 业务绩效
     */
    @ApiModelProperty("业务绩效")
    @Excel(name = "业务绩效")
    private BigDecimal businessPerformance;

    /**
     * 话费补助
     */
    @ApiModelProperty("话费补助")
    @Excel(name = "话费补助")
    private BigDecimal phoneAllowance;

    /**
     * 电脑补助
     */
    @ApiModelProperty("电脑补助")
    @Excel(name = "电脑补助")
    private BigDecimal computerAllowance;

    /**
     * 住房补助
     */
    @ApiModelProperty("住房补助")
    @Excel(name = "住房补助")
    private BigDecimal housingAllowance;

    /**
     * 高温补助
     */
    @ApiModelProperty("高温补助")
    @Excel(name = "高温补助")
    private BigDecimal highTemperatureAllowance;

    /**
     * 出差补助
     */
    @ApiModelProperty("出差补助")
    @Excel(name = "出差补助")
    private BigDecimal travelAllowance;

    /**
     * 其他奖金及补贴
     */
    @ApiModelProperty("其他奖金及补贴")
    @Excel(name = "其他奖金及补贴")
    private BigDecimal otherBonusAndSubsidy;

    /**
     * 应发工资合计
     */
    @ApiModelProperty("应发工资合计")
    @Excel(name = "应发工资合计")
    private BigDecimal totalPayable;

    /**
     * 个人养老保险
     */
    @ApiModelProperty("个人养老保险")
    @Excel(name = "个人养老保险")
    private BigDecimal personalPension;

    /**
     * 个人失业保险
     */
    @ApiModelProperty("个人失业保险")
    @Excel(name = "个人失业保险")
    private BigDecimal personalUnemploymentInsurance;

    /**
     * 个人医疗保险
     */
    @ApiModelProperty("个人医疗保险")
    @Excel(name = "个人医疗保险")
    private BigDecimal personalMedicalInsurance;

    /**
     * 个人公积金
     */
    @ApiModelProperty("个人公积金")
    @Excel(name = "个人公积金")
    private BigDecimal personalProvidentFund;

    /**
     * 个人所得税
     */
    @ApiModelProperty("个人所得税")
    @Excel(name = "个人所得税")
    private BigDecimal incomeTax;

    /**
     * 实发工资金额
     */
    @ApiModelProperty("实发工资金额")
    @Excel(name = "实发工资金额")
    private BigDecimal salaryAmount;

    /**
     * 备注
     */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("备注")
    @Length(max= 255,message="编码长度不能超过255")
    @Excel(name = "备注")
    private String remark;

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

}
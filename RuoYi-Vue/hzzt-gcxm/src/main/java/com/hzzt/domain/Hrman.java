package com.hzzt.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("hrman")
public class Hrman extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private String id;

    /** 员工编号 */
    @Excel(name = "员工编号")
    private String employeeNumber;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthDate;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long age;


    /** 部门 */
    @Excel(name = "部门")
    private String department;

    /** 当前所在项目中心/部门 */
    @Excel(name = "当前所在项目中心/部门")
    private String currentProjectDepartment;

    /** 职位 */
    @Excel(name = "职位")
    private String position;

    /** 从事专业 */
    @Excel(name = "从事专业")
    private String professionalField;

    /** 合同签订单位 */
    @Excel(name = "合同签订单位")
    private String contractCompany;

    /** 社保代理机构 */
    @Excel(name = "社保代理机构")
    private String socialSecurityAgency;

    /** 合同起始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "合同起始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date contractPeriod;

    /** 合同到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "合同到期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date contractEndDate;

    // 新增临时查询字段（不持久化到数据库）
    /** 合同起始时间查询范围-开始 */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractPeriodStart;

    /** 合同起始时间查询范围-结束 */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractPeriodEnd;

    /** 合同到期时间查询范围-开始 */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractEndDateStart;

    /** 合同到期时间查询范围-结束 */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractEndDateEnd;

    /** 性别（0男1女） */
    @Excel(name = "性别", readConverterExp = "0=男,1=女")
    private Integer gender;

    /** 民族 */
    @Excel(name = "民族")
    private String ethnicity;

    /** 政治面貌 */
    @Excel(name = "政治面貌")
    private String politicalStatus;

    /** 籍贯 */
    @Excel(name = "籍贯")
    private String birthplace;

    /** 身份证号码 */
    @Excel(name = "身份证号码")
    private String idCardNumber;

    /** 试用期时间 */
    @Excel(name = "试用期时间")
    private String probationPeriod;

    /** 试用期工资（元） */
    @Excel(name = "试用期工资（元）")
    private BigDecimal probationSalary;

    /** 转正后工资（元） */
    @Excel(name = "转正后工资（元）")
    private BigDecimal postProbationSalary;

    /** 毕业时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "毕业时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date graduationTime;

    /** 毕业院校 */
    @Excel(name = "毕业院校")
    private String graduationSchool;

    /** 所学专业 */
    @Excel(name = "所学专业")
    private String major;

    /** 学历 */
    @Excel(name = "学历")
    private String educationLevel;

    /** 学位 */
    @Excel(name = "学位")
    private String degree;

    /** 参加工作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "参加工作时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startWorkTime;

    /** 入职本公司时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职本公司时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinCompanyTime;

    /** 转正时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "转正时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date regularTime;

    /** 岗位级别 */
    @Excel(name = "岗位级别")
    private String positionLevel;

    /** 职称名称 */
    @Excel(name = "职称名称")
    private String titleName;

    /** 职称等级 */
    @Excel(name = "职称等级")
    private String titleLevel;

    /** 职称评定年份 */
    @Excel(name = "职称评定年份")
    private Long titleEvaluationYear;

    /** 档案所在地 */
    @Excel(name = "档案所在地")
    private String archiveLocation;

    /** 户口所在地 */
    @Excel(name = "户口所在地")
    private String hukouLocation;

    /** QQ */
    @Excel(name = "QQ")
    private String qq;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phoneNumber;

    /** 邮箱  */
    @Excel(name = "邮箱 ")
    private String email;

    /** 紧急联系人 */
    @Excel(name = "紧急联系人")
    private String emergencyContact;

    /** 紧急联系电话 */
    @Excel(name = "紧急联系电话")
    private String emergencyPhoneNumber;

    /** 爱好 */
    @Excel(name = "爱好")
    private String hobby;

    /** 曾任职工作单位及工作职位 */
    @Excel(name = "曾任职工作单位及工作职位")
    private String previousWorkPlaceAndPosition;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bank;

    /** 卡号 */
    @Excel(name = "卡号")
    private String cardNumber;

    /** 转正工资 */
    @Excel(name = "转正工资")
    private BigDecimal regularSalary;

    /** 就职状态 */
    @Excel(name = "就职状态:0为离职 1为在职")
    private String employmentStatus;

    //新增字段


}

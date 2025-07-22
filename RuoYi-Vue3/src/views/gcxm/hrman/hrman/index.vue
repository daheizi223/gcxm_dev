<template>
    <div class="app-container">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="员工姓名" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入员工姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <!-- <el-form-item label="员工编号" prop="employeeNumber">
          <el-input
            v-model="queryParams.employeeNumber"
            placeholder="请输入员工编号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item> -->
        <el-form-item label="部门" prop="department">
    <el-input
      v-model="queryParams.department"
      placeholder="请输入部门"
      clearable
      @keyup.enter="handleQuery"
    />
  </el-form-item>
  <el-form-item label="合同开始时间" prop="contractPeriod">
    <el-date-picker
      v-model="dateRange1"
      type="daterange"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      value-format="YYYY-MM-DD"
      @change="handleContractPeriodChange"
    />
  </el-form-item>
  <el-form-item label="合同到期时间" prop="contractEndDate">
    <el-date-picker
      v-model="dateRange2"
      type="daterange"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      value-format="YYYY-MM-DD"
      @change="handleContractEndDateChange"
    />
  </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
  
      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" icon="Download" @click="handleInput">导出</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" icon="Upload" @click="handleImport">导入</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="info" icon="DocumentAdd" @click="handleImportTemplate">下载模板</el-button>
        </el-col>
        <el-col :span="1.5">
        <el-button 
          type="warning" 
          icon="Timer" 
          @click="handleExpiringContracts"
        >合同即将到期</el-button>
      </el-col>
        <el-col :span="1.5">
      <el-button 
        type="warning" 
        icon="User" 
        @click="handleActiveEmployees"
      >在职员工</el-button>
    </el-col>
      </el-row>
  
      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="hrmanList" @selection-change="handleSelectionChange"
      :summary-method="getSummaries"
      show-summary>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="员工编号" prop="employeeNumber" width="120" />
        <el-table-column label="姓名" prop="name" width="100" />
        <el-table-column label="年龄" prop="age" width="80" />
        <el-table-column label="性别" prop="gender" width="80">
          <template #default="scope">
           {{ scope.row.gender === 0 ? '男' : '女' }}
              </template>
        </el-table-column>

        <el-table-column label="手机号" prop="phoneNumber" width="120" />
        <el-table-column label="入职时间" prop="joinCompanyTime" width="120" />
        <el-table-column label="状态" prop="employmentStatus" width="120">
         <template #default="scope">
          {{ scope.row.employmentStatus === "1" ? '在职' : '离职' }}
         </template>
        </el-table-column>  
        <el-table-column 
        label="试用期工资" 
        prop="probationSalary" 
        width="120"
        align="right"
      >
        <template #default="scope">
          {{ scope.row.probationSalary ? '¥' + scope.row.probationSalary.toFixed(2) : '-' }}
        </template>
      </el-table-column>
      
      <el-table-column 
        label="转正工资" 
        prop="regularSalary" 
        width="120"
        align="right"
      >
    <template #default="scope">
      {{ scope.row.regularSalary ? '¥' + scope.row.regularSalary.toFixed(2) : '-' }}
    </template>
  </el-table-column>    
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
  
  
      
      <!-- 新增/修改弹窗 -->
<!-- 新增/修改弹窗 -->
<el-dialog :title="title" v-model="open" width="90%">
  <el-form ref="formRef" :model="form" :rules="rules" label-width="150px">
    <el-tabs v-model="activeTab">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工编号" prop="employeeNumber">
              <el-input v-model="form.employeeNumber" placeholder="请输入员工编号" />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="0">男</el-radio>
                <el-radio :label="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="身份证号码" prop="idCardNumber">
              <el-input v-model="form.idCardNumber" placeholder="请输入身份证号码" />
            </el-form-item>
            <el-form-item label="民族" prop="ethnicity">
              <el-input v-model="form.ethnicity" placeholder="请输入民族" />
            </el-form-item>
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择出生日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="18" :max="100" />
            </el-form-item>
            <el-form-item label="政治面貌" prop="politicalStatus">
              <el-select v-model="form.politicalStatus" placeholder="请选择政治面貌">
                <el-option label="群众" value="群众" />
                <el-option label="党员" value="党员" />
                <el-option label="团员" value="团员" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item label="籍贯" prop="birthplace">
              <el-input v-model="form.birthplace" placeholder="请输入籍贯" />
            </el-form-item>
            <el-form-item label="就职状态" prop="employmentStatus">
              <el-select v-model="form.employmentStatus" placeholder="请选择就职状态">
                <el-option label="在职" value="1" />
                <el-option label="离职" value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 联系方式 -->
      <el-tab-pane label="联系方式" name="contact">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phoneNumber">
              <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" />
            </el-form-item>
            <el-form-item label="QQ" prop="qq">
              <el-input v-model="form.qq" placeholder="请输入QQ号码" />
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="form.email" type="email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
            </el-form-item>
            <el-form-item label="紧急联系电话" prop="emergencyPhoneNumber">
              <el-input v-model="form.emergencyPhoneNumber" placeholder="请输入紧急联系电话" />
            </el-form-item>
            <el-form-item label="兴趣爱好" prop="hobby">
              <el-input v-model="form.hobby" placeholder="请输入兴趣爱好" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 工作信息 -->
      <el-tab-pane label="工作信息" name="work">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
            <el-form-item label="当前项目/部门" prop="currentProjectDepartment">
              <el-input v-model="form.currentProjectDepartment" placeholder="请输入当前项目/部门" />
            </el-form-item>
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
            <el-form-item label="从事专业" prop="professionalField">
              <el-input v-model="form.professionalField" placeholder="请输入从事专业" />
            </el-form-item>
            <el-form-item label="岗位级别" prop="positionLevel">
              <el-input v-model="form.positionLevel" placeholder="请输入岗位级别" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始工作时间" prop="startWorkTime">
              <el-date-picker
                v-model="form.startWorkTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择开始工作时间"
              />
            </el-form-item>
            <el-form-item label="入职时间" prop="joinCompanyTime">
              <el-date-picker
                v-model="form.joinCompanyTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择入职时间"
              />
            </el-form-item>
            <el-form-item label="转正时间" prop="regularTime">
              <el-date-picker
                v-model="form.regularTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择转正时间"
              />
            </el-form-item>
            <el-form-item label="职称名称" prop="titleName">
              <el-input v-model="form.titleName" placeholder="请输入职称名称" />
            </el-form-item>
            <el-form-item label="职称等级" prop="titleLevel">
              <el-input v-model="form.titleLevel" placeholder="请输入职称等级" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 合同信息 -->
      <el-tab-pane label="合同信息" name="contract">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同签订单位" prop="contractCompany">
              <el-input v-model="form.contractCompany" placeholder="请输入合同签订单位" />
            </el-form-item>
            <el-form-item label="社保代理机构" prop="socialSecurityAgency">
              <el-input v-model="form.socialSecurityAgency" placeholder="请输入社保代理机构" />
            </el-form-item>
            <el-form-item label="合同开始时间" prop="contractPeriod">
              <el-date-picker
                v-model="form.contractPeriod"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择合同开始时间"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同到期时间" prop="contractEndDate">
              <el-date-picker
                v-model="form.contractEndDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择合同到期时间"
              />
            </el-form-item>
            <el-form-item label="试用期时间" prop="probationPeriod">
              <el-input v-model="form.probationPeriod" placeholder="请输入试用期时间" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 薪资信息 -->
      <el-tab-pane label="薪资信息" name="salary">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="试用期工资" prop="probationSalary">
              <el-input-number 
                v-model="form.probationSalary" 
                :min="0" 
                :precision="2" 
                :step="1000"
                placeholder="请输入试用期工资"
              />
            </el-form-item>
            <el-form-item label="转正工资" prop="regularSalary">
              <el-input-number 
                v-model="form.regularSalary" 
                :min="0" 
                :precision="2" 
                :step="1000"
                placeholder="请输入转正工资"
              />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="12">
            <el-form-item label="转正后工资" prop="postProbationSalary">
              <el-input-number 
                v-model="form.postProbationSalary" 
                :min="0" 
                :precision="2" 
                :step="1000"
                placeholder="请输入转正后工资"
              />
            </el-form-item>
          </el-col> -->
        </el-row>
      </el-tab-pane>

      <!-- 教育背景 -->
      <el-tab-pane label="教育背景" name="education">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="毕业院校" prop="graduationSchool">
              <el-input v-model="form.graduationSchool" placeholder="请输入毕业院校" />
            </el-form-item>
            <el-form-item label="学历" prop="educationLevel">
              <el-select v-model="form.educationLevel" placeholder="请选择学历">
                <el-option label="专科" value="专科" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
            <el-form-item label="学位" prop="degree">
              <el-select v-model="form.degree" placeholder="请选择学位">
                <el-option label="学士" value="学士" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所学专业" prop="major">
              <el-input v-model="form.major" placeholder="请输入所学专业" />
            </el-form-item>
            <el-form-item label="毕业时间" prop="graduationTime">
              <el-date-picker
                v-model="form.graduationTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择毕业时间"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 其他信息 -->
      <el-tab-pane label="其他信息" name="other">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="档案所在地" prop="archiveLocation">
              <el-input v-model="form.archiveLocation" placeholder="请输入档案所在地" />
            </el-form-item>
            <el-form-item label="户口所在地" prop="hukouLocation">
              <el-input v-model="form.hukouLocation" placeholder="请输入户口所在地" />
            </el-form-item>
            <el-form-item label="开户银行" prop="bank">
              <el-input v-model="form.bank" placeholder="请输入开户银行" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行卡号" prop="cardNumber">
              <el-input v-model="form.cardNumber" placeholder="请输入银行卡号" />
            </el-form-item>
            <el-form-item label="工作经历" prop="previousWorkPlaceAndPosition">
              <el-input 
                type="textarea" 
                v-model="form.previousWorkPlaceAndPosition" 
                :rows="3"
                placeholder="请输入工作经历"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

    </el-tabs>
  </el-form>
  <template #footer>
    <div class="dialog-footer">
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </template>
</el-dialog>
  
  <!-- 新增详情弹窗 -->
<el-dialog title="员工详情" v-model="detailVisible" width="90%">
  <el-descriptions :column="2" border>

    <!-- 基本信息 -->
    <el-descriptions-item label="员工编号">{{ detailInfo.employeeNumber }}</el-descriptions-item>
    <el-descriptions-item label="姓名">{{ detailInfo.name }}</el-descriptions-item>
    <el-descriptions-item label="性别">{{ detailInfo.gender === 0 ? '男' : '女' }}</el-descriptions-item>
    <el-descriptions-item label="出生日期">{{ detailInfo.birthDate }}</el-descriptions-item>
    <el-descriptions-item label="年龄">{{ detailInfo.age }}</el-descriptions-item>
    <el-descriptions-item label="身份证号码">{{ detailInfo.idCardNumber }}</el-descriptions-item>
    <el-descriptions-item label="民族">{{ detailInfo.ethnicity }}</el-descriptions-item>
    <el-descriptions-item label="政治面貌">{{ detailInfo.politicalStatus }}</el-descriptions-item>
    <el-descriptions-item label="籍贯">{{ detailInfo.birthplace }}</el-descriptions-item>
    <el-descriptions-item label="手机号">{{ detailInfo.phoneNumber }}</el-descriptions-item>
    <el-descriptions-item label="QQ">{{ detailInfo.qq }}</el-descriptions-item>
    <el-descriptions-item label="电子邮箱">{{ detailInfo.email }}</el-descriptions-item>
    
    <!-- 教育背景 -->
    <el-descriptions-item label="毕业院校">{{ detailInfo.graduationSchool }}</el-descriptions-item>
    <el-descriptions-item label="所学专业">{{ detailInfo.major }}</el-descriptions-item>
    <el-descriptions-item label="学历">{{ detailInfo.educationLevel }}</el-descriptions-item>
    <el-descriptions-item label="学位">{{ detailInfo.degree }}</el-descriptions-item>
    <el-descriptions-item label="毕业时间">{{ detailInfo.graduationTime }}</el-descriptions-item>
    
    <!-- 工作信息 -->
    <el-descriptions-item label="部门">{{ detailInfo.department }}</el-descriptions-item>
    <el-descriptions-item label="当前项目/部门">{{ detailInfo.currentProjectDepartment }}</el-descriptions-item>
    <el-descriptions-item label="职位">{{ detailInfo.position }}</el-descriptions-item>
    <el-descriptions-item label="从事专业">{{ detailInfo.professionalField }}</el-descriptions-item>
    <el-descriptions-item label="开始工作时间">{{ detailInfo.startWorkTime }}</el-descriptions-item>
    <el-descriptions-item label="入职时间">{{ detailInfo.joinCompanyTime }}</el-descriptions-item>
    <el-descriptions-item label="转正时间">{{ detailInfo.regularTime }}</el-descriptions-item>
    <el-descriptions-item label="职称名称">{{ detailInfo.titleName }}</el-descriptions-item>
    <el-descriptions-item label="职称等级">{{ detailInfo.titleLevel }}</el-descriptions-item>
    <el-descriptions-item label="职称评定年份">{{ detailInfo.titleEvaluationYear }}</el-descriptions-item>
    <el-descriptions-item label="岗位级别">{{ detailInfo.positionLevel }}</el-descriptions-item>
    
    <!-- 合同信息 -->
    <el-descriptions-item label="合同签订单位">{{ detailInfo.contractCompany }}</el-descriptions-item>
    <el-descriptions-item label="社保代理机构">{{ detailInfo.socialSecurityAgency }}</el-descriptions-item>
    <el-descriptions-item label="合同开始时间">{{ detailInfo.contractPeriod }}</el-descriptions-item>
    <el-descriptions-item label="合同到期时间">{{ detailInfo.contractEndDate }}</el-descriptions-item>
    <el-descriptions-item label="试用期时间">{{ detailInfo.probationPeriod }}</el-descriptions-item>
    
    <!-- 薪资信息 -->
    <el-descriptions-item label="试用期工资">{{ detailInfo.probationSalary }}</el-descriptions-item>
    <el-descriptions-item label="转正工资">{{ detailInfo.regularSalary }}</el-descriptions-item>
    <!-- <el-descriptions-item label="转正后工资">{{ detailInfo.postProbationSalary }}</el-descriptions-item> -->
    
    <!-- 其他信息 -->
    <el-descriptions-item label="档案所在地">{{ detailInfo.archiveLocation }}</el-descriptions-item>
    <el-descriptions-item label="户口所在地">{{ detailInfo.hukouLocation }}</el-descriptions-item>
    <el-descriptions-item label="紧急联系人">{{ detailInfo.emergencyContact }}</el-descriptions-item>
    <el-descriptions-item label="紧急联系电话">{{ detailInfo.emergencyPhoneNumber }}</el-descriptions-item>
    <el-descriptions-item label="兴趣爱好">{{ detailInfo.hobby }}</el-descriptions-item>
    <el-descriptions-item label="银行卡号">{{ detailInfo.cardNumber }}</el-descriptions-item>
    <el-descriptions-item label="开户银行">{{ detailInfo.bank }}</el-descriptions-item>
    <el-descriptions-item label="就职状态">{{ detailInfo.employmentStatus === "1" ? '在职' : '离职' }}</el-descriptions-item>
    <el-descriptions-item label="工作经历" :span="2">{{ detailInfo.previousWorkPlaceAndPosition }}</el-descriptions-item>

 
  
</el-descriptions>

<div class="file-section">
  <el-divider content-position="left">图片管理</el-divider>
  
  <!-- 文件上传 -->
  <el-upload
    class="upload-demo"
    :action="uploadUrl"
    :headers="headers"
    :data="{ fileType: currentFileType }"
    :on-success="handleUploadSuccess"
    :on-error="handleUploadError"
    :before-upload="beforeUpload"
    :show-file-list="false"
  >
    <div class="upload-content">
      <el-select v-model="currentFileType" placeholder="选择文件类型" style="width: 150px">
        <el-option label="身份证" value="ID_CARD" />
        <el-option label="学历证书" value="DEGREE" />
        <el-option label="职业证书" value="CERTIFICATE" />
        <el-option label="其他文件" value="Otherfile" />
      </el-select>
      <el-button type="primary" style="margin-left: 10px">上传文件</el-button>
    </div>
  </el-upload>

  <!-- 文件列表 -->
  <el-table :data="fileList" style="margin-top: 20px">
    <el-table-column prop="fileType" label="文件类型" width="120">
      <template #default="{ row }">
        {{ fileTypeMap[row.fileType] }}
      </template>
    </el-table-column>
    <el-table-column prop="fileName" label="文件名称" />
    <el-table-column prop="uploadTime" label="上传时间" width="180">
      <template #default="{ row }">
        {{ parseTime(row.uploadTime) }}
      </template>
    </el-table-column>
    <el-table-column label="操作" width="220" fixed="right">
      <template #default="{ row }">
    <el-button 
      v-if="isPreviewable(row.mimeType)"
      link 
      type="primary" 
      @click="previewFile(row)"
    >预览</el-button>
    <el-button link type="primary" @click="downloadFile(row)">下载</el-button>
    <el-button link type="danger" @click="deleteFiles(row)">删除</el-button>
  </template>
    </el-table-column>
  </el-table>
</div>

</el-dialog>
  
      
    </div>
  </template>
  
  <script setup>
  import { listHrman, addHrman, updateHrman, delHrman, getHrman , importData , importTemplate,  input ,getExpiringContracts,uploadFile,getEmployeeFiles,deleteFile   } from "@/api/gcxm/hrman";
// 在 import 部分添加 computed
import { ref, reactive, getCurrentInstance, onMounted, computed } from 'vue';
import { parseTime } from '@/utils/ruoyi';
import { getToken } from '@/utils/auth';
import axios from 'axios';


  const { proxy } = getCurrentInstance();
  
  const hrmanList = ref([]);
  const open = ref(false);
  const loading = ref(true);
  const ids = ref([]);
  const single = ref(true);
  const multiple = ref(true);
  const total = ref(0);
  const title = ref("");
  const activeTab = ref("basic");
  const detailVisible = ref(false);
  const detailInfo = ref({});


  // 在已有的变量声明后添加
const fileList = ref([]);
const currentFileType = ref('ID_CARD');
const fileTypeMap = {
  ID_CARD: '身份证',
  DEGREE: '学历证书',
  CERTIFICATE: '职业证书',
  Otherfile:'其他文件'
};



// const uploadUrl = computed(() => `D:/uploads/employee-files/hrman/${detailInfo.value.id}/files`);
const uploadUrl = computed(() => `${import.meta.env.VITE_APP_BASE_API}/hrman/${detailInfo.value.id}/files`);
const headers = {
  Authorization: 'Bearer ' + getToken()
};
const uploadData = computed(() => ({
  fileType: currentFileType.value
}));

  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
  pageSize: 10,
  name: null,
  employeeNumber: null,
  department: null,
  contractPeriodStart: null,
  contractPeriodEnd: null,
  contractEndDateStart: null,
  contractEndDateEnd: null,
  employmentStatus: null
  });


  const getSummaries = (param) => {
  const { columns, data } = param;
  const sums = [];
  
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计';
      return;
    }
    
    // 需要计算合计的列
    if (column.property === 'probationSalary' || column.property === 'regularSalary') {
      const values = data.map(item => Number(item[column.property]) || 0);
      const sum = values.reduce((prev, curr) => {
        return prev + curr;
      }, 0);
      sums[index] = '¥' + sum.toFixed(2);
    } else {
      sums[index] = '';
    }
  });
  
  return sums;
};

  // 日期范围选择器的值
const dateRange1 = ref([]);
const dateRange2 = ref([]);
  
  const form = ref({
  id: null,
  employeeNumber: null,
  name: null,
  gender: 0,
  birthDate: null,
  age: null,
  idCardNumber: null,
  ethnicity: null,
  politicalStatus: null,
  birthplace: null,
  phoneNumber: null,
  qq: null,
  email: null,
  emergencyContact: null,
  emergencyPhoneNumber: null,
  hobby: null,
  department: null,
  currentProjectDepartment: null,
  position: null,
  professionalField: null,
  startWorkTime: null,
  joinCompanyTime: null,
  regularTime: null,
  employmentStatus: "1",
  contractCompany: null,
  socialSecurityAgency: null,
  contractPeriod: null,
  contractEndDate: null,
  probationPeriod: null,
  probationSalary: null,
  regularSalary: null,
  postProbationSalary: null,
  graduationSchool: null,
  educationLevel: null,
  degree: null,
  major: null,
  graduationTime: null,
  archiveLocation: null,
  hukouLocation: null,
  bank: null,
  cardNumber: null,
  previousWorkPlaceAndPosition: null
});
  
  // 验证规则
const rules = reactive({
  employeeNumber: [
    { required: true, message: "员工编号不能为空", trigger: "blur" }
  ],
  name: [
    { required: true, message: "姓名不能为空", trigger: "blur" }
  ],
  gender: [
    { required: true, message: "请选择性别", trigger: "change" }
  ],
  idCardNumber: [
    { required: true, message: "身份证号码不能为空", trigger: "blur" },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: "身份证号码格式不正确" }
  ],
  phoneNumber: [
    { required: true, message: "手机号码不能为空", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "手机号码格式不正确" }
  ],
  email: [
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }
  ],
  employmentStatus: [
    { required: true, message: "请选择就职状态", trigger: "change" }
  ],
  probationSalary: [
    { type: "number", message: "必须为数字值" }
  ],
  regularSalary: [
    { type: "number", message: "必须为数字值" }
  ],
  postProbationSalary: [
    { type: "number", message: "必须为数字值" }
  ],
  cardNumber: [
    { pattern: /^\d{16,19}$/, message: "银行卡号格式不正确" }
  ],
  emergencyPhoneNumber: [
    { pattern: /^1[3-9]\d{9}$/, message: "手机号码格式不正确" }
  ]
});
  
  
  /** 查询列表 */
  function getList() {
    loading.value = true;
    listHrman(queryParams).then(response => {
      hrmanList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  }
  
  /** 新增按钮 */
  function handleAdd() {
    reset();
    open.value = true;
    title.value = "新增员工";
  }
  
  /** 修改按钮 */
  function handleUpdate(row) {
    reset();
    const id = row.id || ids.value[0];
    getHrman(id).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改员工信息";
    });
  }
  
  /** 删除按钮 */
  function handleDelete(row) {
    const ids = row.id || ids.value;
    proxy.$modal.confirm('确认删除选中数据？').then(() => {
      return delHrman(ids);
    }).then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    });
  }
  
  function handleImport() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.xlsx, .xls';
    input.onchange = e => {
      const files = e.target.files;
      if (files.length === 0) return;
      const formData = new FormData();
      formData.append('file', files[0]);
      
      proxy.$modal.loading("正在导入数据，请稍后...");
      importData(formData).then(() => {
        proxy.$modal.msgSuccess("导入成功");
        getList();
      }).finally(() => {
        proxy.$modal.closeLoading();
        input.value = '';
      });
    };
    input.click();
  }
  
  
  function handleImportTemplate() {
    importTemplate().then(res => {
      const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = '员工信息模板.xlsx';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
  
  
  function handleInput() {
    input().then(res => {
      const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = '.xlsx';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
  
  const queryForm = ref(null); // 添加查询表单的ref
  
  // 添加搜索相关方法
  function handleQuery() {
    queryParams.pageNum = 1;
    getList();
  }
  
  function resetQuery() {
    dateRange1.value = [];
  dateRange2.value = [];
  queryForm.value.resetFields();
  queryParams.contractPeriodStart = null;
  queryParams.contractPeriodEnd = null;
  queryParams.contractEndDateStart = null;
  queryParams.contractEndDateEnd = null;
  queryParams.employmentStatus = null;
  handleQuery();
  }
  
/** 取消按钮 */
function cancel() {
  proxy.$modal.confirm('确认关闭吗？未保存的数据将会丢失。').then(() => {
    open.value = false;
    reset();
  }).catch(() => {});
}
  
  // 添加重置方法
  function reset() {
    form.value = {
      employeeNumber: null,
      name: null,
      birthDate: null,
      age: null,
      // ...保持所有字段初始化...
      employmentStatus: "1"
    };
    proxy.resetForm("formRef");
  }
  
// 详情处理方法
function handleDetail(row) {
  getHrman(row.id).then(response => {
    detailInfo.value = response.data;
    detailVisible.value = true;
    // 加载文件列表
    loadFileList();
  });
}
  
  // 添加选择处理方法
  function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.id);
    single.value = selection.length !== 1;
    multiple.value = !selection.length;
  }
  

  // 处理合同开始时间范围变化
function handleContractPeriodChange(val) {
  if (val) {
    queryParams.contractPeriodStart = val[0];
    queryParams.contractPeriodEnd = val[1];
  } else {
    queryParams.contractPeriodStart = null;
    queryParams.contractPeriodEnd = null;
  }
}

// 处理合同到期时间范围变化
function handleContractEndDateChange(val) {
  if (val) {
    queryParams.contractEndDateStart = val[0];
    queryParams.contractEndDateEnd = val[1];
  } else {
    queryParams.contractEndDateStart = null;
    queryParams.contractEndDateEnd = null;
  }
}

// 处理合同即将到期按钮点击事件
function handleExpiringContracts() {
  loading.value = true;
  getExpiringContracts().then(response => {
    hrmanList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    
    // 如果没有查到数据，显示提示信息
    if (response.total === 0) {
      proxy.$modal.msgInfo("没有找到合同即将到期的员工");
    }
  });
}

// 添加在职员工筛选方法
function handleActiveEmployees() {
  // 重置分页
  queryParams.pageNum = 1;
  // 设置就职状态为"在职"
  queryParams.employmentStatus = "1";
  // 重新获取列表
  getList();
}
  
  
  /** 提交表单 */
  function submitForm() {
    proxy.$refs.formRef.validate(valid => {
      if (valid) {
        if (form.value.id) {
          updateHrman(form.value).then(() => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          });
        } else {
          addHrman(form.value).then(() => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          });
        }
      }
    });
  }
  
  // 初始化加载
  onMounted(() => {
    getList();
  });

  // 添加文件相关方法
function handleUploadSuccess(response) {
  if (response.code === 200) {
    proxy.$modal.msgSuccess("文件上传成功");
    loadFileList();
  } else {
    proxy.$modal.msgError(response.msg || "上传失败");
  }
}

function handleUploadError(error) {
  proxy.$modal.msgError("文件上传失败");
  console.error('上传失败:', error);
}

function beforeUpload(file) {
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    proxy.$modal.msgError('文件大小不能超过 10MB!');
    return false;
  }
  return true;
}



async function previewFile(file) {
  try {
    const baseUrl = import.meta.env.VITE_APP_BASE_API.replace(/\/$/, '');
    const response = await axios({
      method: 'get',
      url: `${baseUrl}/hrman/files/${file.id}`,
      params: { download: false },
      responseType: 'blob',
      headers: {
        'Authorization': 'Bearer ' + getToken()
      }
    });

    // 处理响应
    const blob = new Blob([response.data], { type: file.mimeType });
    const fileUrl = window.URL.createObjectURL(blob);

    // 创建预览窗口
    const newWindow = window.open('');
    if (newWindow) {
      newWindow.document.write(`
        <html>
          <head>
            <title>图片预览 - ${file.fileName}</title>
            <style>
              body {
                margin: 0;
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
                background: #f0f0f0;
              }
              img {
                max-width: 100%;
                max-height: 100vh;
                object-fit: contain;
              }
              .preview-container {
                padding: 20px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
                background: white;
                border-radius: 4px;
              }
              .preview-title {
                text-align: center;
                margin-bottom: 20px;
                font-size: 18px;
                color: #333;
              }
            </style>
          </head>
          <body>
            <div class="preview-container">
              <div class="preview-title">${file.fileName}</div>
              <img src="${fileUrl}" alt="${file.fileName}"/>
            </div>
          </body>
        </html>
      `);
    }

    // 清理资源
    setTimeout(() => {
      window.URL.revokeObjectURL(fileUrl);
    }, 100);

  } catch (error) {
    console.error('预览文件失败:', error);
    proxy.$modal.msgError(`预览文件失败: ${error.message || '未知错误'}`);
  }
}



async function downloadFile(file) {
  try {
    const baseUrl = import.meta.env.VITE_APP_BASE_API.replace(/\/$/, '');
    const response = await axios({
      method: 'get',
      url: `${baseUrl}/hrman/files/${file.id}`,
      params: { download: true },
      responseType: 'blob',
      headers: {
        'Authorization': 'Bearer ' + getToken()
      }
    });

    const blob = new Blob([response.data], { type: file.mimeType });
    const fileUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = fileUrl;
    link.download = file.fileName || '未命名文件';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    setTimeout(() => {
      window.URL.revokeObjectURL(fileUrl);
    }, 100);
  } catch (error) {
    console.error('下载文件失败:', error);
    proxy.$modal.msgError(`下载文件失败: ${error.message || '未知错误'}`);
  }
}



async function deleteFiles(file) {
  try {
    await proxy.$modal.confirm('确认删除该文件吗？');
    
    // 调用API删除文件
    await deleteFile(file.id); // 使用已定义的API函数
    
    proxy.$modal.msgSuccess("删除成功");
    loadFileList(); // 重新加载文件列表
    
  } catch (error) {
    if (error !== 'cancel') { // 忽略用户取消的情况
      console.error('删除文件失败:', error);
      proxy.$modal.msgError(`删除失败: ${error.message || '未知错误'}`);
    }
  }
}

function isPreviewable(mimeType) {
  const previewableTypes = [
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/bmp',
    'image/webp',
    'application/pdf'
  ];
  return previewableTypes.includes(mimeType);
}






function loadFileList() {
  if (detailInfo.value.id) {
    getEmployeeFiles(detailInfo.value.id).then(response => {
      if (response.code === 200) {
        fileList.value = response.data;
      } else {
        proxy.$modal.msgError(response.msg || "获取文件列表失败");
      }
    }).catch(error => { 
      console.error('获取文件列表失败:', error);
      proxy.$modal.msgError("获取文件列表失败");
    });
  }
}
  </script>
  
  <style scoped>
  /* 详情弹窗样式优化 */
  :deep(.el-descriptions) {
    --el-descriptions-item-bordered-label-background: #f5f7fa;  /* 标签背景色 */
    --el-descriptions-item-bordered-label-border-color: #e4e7ed; /* 标签边框色 */
    --el-descriptions-item-bordered-content-background: #ffffff; /* 内容背景色 */
    --el-descriptions-item-bordered-padding: 12px 15px; /* 单元格内边距 */
  }
  
  /* 标题样式 */
  :deep(.el-dialog__title) {
    color: #303133;
    font-size: 18px;
    font-weight: 600;
  }
  
  /* 分隔线样式 */
  :deep(.el-descriptions__body .el-descriptions__table) {
    border-color: #ebeef5;
  }
  
  /* 标签文字样式 */
  :deep(.el-descriptions__label) {
    color: #606266;
    font-weight: 500;
  }
  
  /* 内容区域样式 */
  :deep(.el-descriptions__content) {
    color: #303133;
    font-weight: 400;
  }
  
  /* 添加整体阴影效果 */
  .el-dialog {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  .file-section {
  margin-top: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.upload-content {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.el-upload__tip {
  color: #909399;
  font-size: 13px;
  margin-top: 7px;
}

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: 500;
}
  </style>
  
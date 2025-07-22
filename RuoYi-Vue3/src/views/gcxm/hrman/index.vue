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
      <el-form-item label="员工编号" prop="employeeNumber">
        <el-input
          v-model="queryParams.employeeNumber"
          placeholder="请输入员工编号"
          clearable
          @keyup.enter="handleQuery"
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
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="hrmanList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="员工编号" prop="employeeNumber" width="120" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="年龄" prop="age" width="80" />
      <el-table-column label="手机号" prop="phoneNumber" width="120" />
      <el-table-column label="入职时间" prop="joinCompanyTime" width="120" />
      <el-table-column label="职称" prop="titleName" width="120" />
      <el-table-column label="状态" prop="employmentStatus" width="120">
       <template #default="scope">
        {{ scope.row.employmentStatus === "1" ? '在职' : '离职' }}
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
    <el-dialog :title="title" v-model="open" width="90%">
  <el-form ref="formRef" :model="form" :rules="rules" label-width="150px">
    <el-tabs v-model="activeTab">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工编号" prop="employeeNumber">
              <el-input v-model="form.employeeNumber" placeholder="E+数字编号" />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="年龄" prop="age">
              <el-input v-model="form.age" />
            </el-form-item>
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="开始工作时间" prop="startWorkTime">
              <el-date-picker
                v-model="form.startWorkTime"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phoneNumber">
              <el-input v-model="form.phoneNumber" />
            </el-form-item>
            <el-form-item label="QQ" prop="qq">
              <el-input v-model="form.qq" />
            </el-form-item>
            <el-form-item label="入职时间" prop="joinCompanyTime">
              <el-date-picker
                v-model="form.joinCompanyTime"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="转正时间" prop="regularTime">
              <el-date-picker
                v-model="form.regularTime"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 教育背景 -->
      <el-tab-pane label="教育背景" name="education">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="毕业院校" prop="graduationSchool">
              <el-input v-model="form.graduationSchool" />
            </el-form-item>
            <el-form-item label="学历" prop="educationLevel">
              <el-input v-model="form.educationLevel" />
            </el-form-item>
            <el-form-item label="学位" prop="degree">
              <el-select v-model="form.degree">
                <el-option label="学士" value="学士" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所学专业" prop="major">
              <el-input v-model="form.major" />
            </el-form-item>
            <el-form-item label="毕业时间" prop="graduationTime">
              <el-date-picker
                v-model="form.graduationTime"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 工作信息 -->
      <el-tab-pane label="工作信息" name="work">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职称名称" prop="titleName">
              <el-input v-model="form.titleName" />
            </el-form-item>
            <el-form-item label="职称等级" prop="titleLevel">
              <el-input v-model="form.titleLevel" />
            </el-form-item>
            <el-form-item label="职称评定年份" prop="titleEvaluationYear">
              <el-input-number 
                v-model="form.titleEvaluationYear" 
                :min="2000" 
                :max="2100"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位级别" prop="positionLevel">
              <el-input v-model="form.positionLevel" />
            </el-form-item>
            <el-form-item label="转正工资" prop="regularSalary">
              <el-input-number 
                v-model="form.regularSalary" 
                :min="0" 
                :precision="2" 
                :step="1000"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 联系信息 -->
      <el-tab-pane label="联系信息" name="contact">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" />
            </el-form-item>
            <el-form-item label="紧急联系电话" prop="emergencyPhoneNumber">
              <el-input v-model="form.emergencyPhoneNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="form.email" type="email" />
            </el-form-item>
            <el-form-item label="兴趣爱好" prop="hobby">
              <el-input v-model="form.hobby" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="工作经历" prop="previousWorkPlaceAndPosition">
              <el-input 
                v-model="form.previousWorkPlaceAndPosition" 
                type="textarea" 
                :rows="3"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 银行信息 -->
      <el-tab-pane label="银行信息" name="bank">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开户银行" prop="bank">
              <el-input v-model="form.bank" />
            </el-form-item>
            <el-form-item label="银行卡号" prop="cardNumber">
              <el-input v-model="form.cardNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="档案所在地" prop="archiveLocation">
              <el-input v-model="form.archiveLocation" />
            </el-form-item>
            <el-form-item label="户口所在地" prop="hukouLocation">
              <el-input v-model="form.hukouLocation" />
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
    <el-descriptions-item label="出生日期">{{ detailInfo.birthDate }}</el-descriptions-item>
    <el-descriptions-item label="年龄">{{ detailInfo.age }}</el-descriptions-item>
    <el-descriptions-item label="手机号">{{ detailInfo.phoneNumber }}</el-descriptions-item>
    <el-descriptions-item label="QQ">{{ detailInfo.qq }}</el-descriptions-item>
    
    <!-- 教育背景 -->
    <el-descriptions-item label="毕业院校">{{ detailInfo.graduationSchool }}</el-descriptions-item>
    <el-descriptions-item label="所学专业">{{ detailInfo.major }}</el-descriptions-item>
    <el-descriptions-item label="学历">{{ detailInfo.educationLevel }}</el-descriptions-item>
    <el-descriptions-item label="学位">{{ detailInfo.degree }}</el-descriptions-item>
    <el-descriptions-item label="毕业时间">{{ detailInfo.graduationTime }}</el-descriptions-item>
    
    <!-- 工作信息 -->
    <el-descriptions-item label="入职时间">{{ detailInfo.joinCompanyTime }}</el-descriptions-item>
    <el-descriptions-item label="转正时间">{{ detailInfo.regularTime }}</el-descriptions-item>
    <el-descriptions-item label="职称名称">{{ detailInfo.titleName }}</el-descriptions-item>
    <el-descriptions-item label="职称等级">{{ detailInfo.titleLevel }}</el-descriptions-item>
    <el-descriptions-item label="职称评定年份">{{ detailInfo.titleEvaluationYear }}</el-descriptions-item>
    <el-descriptions-item label="岗位级别">{{ detailInfo.positionLevel }}</el-descriptions-item>
    
    <!-- 联系信息 -->
    <el-descriptions-item label="电子邮箱">{{ detailInfo.email }}</el-descriptions-item>
    <el-descriptions-item label="紧急联系人">{{ detailInfo.emergencyContact }}</el-descriptions-item>
    <el-descriptions-item label="紧急联系电话">{{ detailInfo.emergencyPhoneNumber }}</el-descriptions-item>
    <el-descriptions-item label="兴趣爱好">{{ detailInfo.hobby }}</el-descriptions-item>
    
    <!-- 银行信息 -->
    <el-descriptions-item label="开户银行">{{ detailInfo.bank }}</el-descriptions-item>
    <el-descriptions-item label="银行卡号">{{ detailInfo.cardNumber }}</el-descriptions-item>
    
    <!-- 其他信息 -->
    <el-descriptions-item label="档案所在地">{{ detailInfo.archiveLocation }}</el-descriptions-item>
    <el-descriptions-item label="户口所在地">{{ detailInfo.hukouLocation }}</el-descriptions-item>
    <el-descriptions-item label="开始工作时间">{{ detailInfo.startWorkTime }}</el-descriptions-item>
    <el-descriptions-item label="工作经历" :span="2">
      {{ detailInfo.previousWorkPlaceAndPosition }}
    </el-descriptions-item>
    
    <!-- 薪资信息 -->
    <el-descriptions-item label="转正工资">{{ detailInfo.regularSalary }}</el-descriptions-item>
    <el-descriptions-item label="入职状态">
  {{ detailInfo.employmentStatus === "1" ? '在职' : '离职' }}
</el-descriptions-item>  </el-descriptions>
</el-dialog>

    
  </div>
</template>

<script setup>
import { listHrman, addHrman, updateHrman, delHrman, getHrman , importData , importTemplate,  input } from "@/api/gcxm/hrman";

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

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null,
  employeeNumber: null
});

// 表单数据
const form = ref({
  employeeNumber: null,
  name: null,
  birthDate: null,
  educationLevel: null,
  age: null,
  startWorkTime: null,
  regularTime: null,
  degree: null,
  titleName: null,
  titleEvaluationYear: null,
  positionLevel: null,
  emergencyContact: null,
  emergencyPhoneNumber: null,
  hobby: null,
  bank: null,
  // 其他字段初始化...
  employmentStatus: "1"
});

// 验证规则
const rules = reactive({
  name: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
  phoneNumber: [
    { required: true, message: "联系电话不能为空", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确" }
  ],
  cardNumber: [
    { required: true, message: "银行卡号不能为空", trigger: "blur" },
    { pattern: /^\d{16,19}$/, message: "银行卡号格式不正确" }
  ],
  previousWorkPlaceAndPosition: [
    { required: true, message: "工作经历不能为空", trigger: "blur" }
  ],
  regularSalary: [
    { required: true, message: "转正工资不能为空", trigger: "blur" },
    { type: 'number', message: '必须为数字值' }
  ],
  bank: [{ required: true, message: "开户银行不能为空", trigger: "blur" }],
  emergencyPhoneNumber: [
    { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确" }
  ],
  titleEvaluationYear: [
    { type: 'number', min: 2000, max: 2100, message: '请输入有效年份' }
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
  queryForm.value.resetFields();
  handleQuery();
}

// 添加取消方法
function cancel() {
  open.value = false;
  reset();
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
  });
}

// 添加选择处理方法
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
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
</style>

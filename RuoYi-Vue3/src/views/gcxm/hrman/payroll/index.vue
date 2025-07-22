<template>
    <div class="app-container">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="员工编号" prop="employeeNumber">
          <el-input
            v-model="queryParams.employeeNumber"
            placeholder="请输入员工编号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="员工姓名" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入员工姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="项目组" prop="projectGroup">
          <el-input
            v-model="queryParams.projectGroup"
            placeholder="请输入所属项目组"
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
      <el-table 
        v-loading="loading" 
        :data="payrollList" 
        @selection-change="handleSelectionChange"
        :summary-method="getSummaries"
        show-summary
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="员工编号" prop="employeeNumber" width="120" />
        <el-table-column label="员工姓名" prop="name" width="120" />
        <el-table-column label="所属项目组" prop="projectGroup" width="150" />
        <el-table-column label="应出勤天数" prop="attendanceDays" width="100" />
        <el-table-column label="实际考勤" prop="actualAttendanceDays" width="100" />
        <el-table-column label="职称工资" prop="titleSalary" width="150" />
        <el-table-column label="工龄工资" prop="workYearsSalary" width="150" />
        <el-table-column label="岗位绩效" prop="positionPerformance" width="150" />
        <el-table-column label="业务绩效" prop="businessPerformance" width="150" />
        <el-table-column label="话费补助" prop="phoneAllowance" width="150" />
        <el-table-column label="电脑补助" prop="computerAllowance" width="150" />
        <el-table-column label="住房补助" prop="housingAllowance" width="150" />
        <el-table-column label="高温补助" prop="highTemperatureAllowance" width="150" />
        <el-table-column label="出差补助" prop="travelAllowance" width="150" />
        <el-table-column label="其他奖金及补贴" prop="otherBonusAndSubsidy" width="150" />
        <el-table-column label="个人养老保险" prop="personalPension" width="150" />
        <el-table-column label="个人失业保险" prop="personalUnemploymentInsurance" width="150" />
        <el-table-column label="个人医疗保险" prop="personalMedicalInsurance" width="150" />
        <el-table-column label="个人公积金" prop="personalProvidentFund" width="150" />
        <el-table-column label="个人所得税" prop="incomeTax" width="150" />
        <el-table-column label="备注" prop="remark" width="150" />
        <!-- <el-table-column label="绩效分数" prop="performanceEvaluation" width="100">
          <template #default="{row}">{{ row.performanceEvaluation?.toFixed(2) }}</template>
        </el-table-column> -->
        <el-table-column label="应发合计" prop="totalPayable" width="120">
          <template #default="{row}">{{ row.totalPayable?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column label="实发工资额" prop="salaryAmount" width="120">
          <template #default="{row}">{{ row.salaryAmount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
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
<el-dialog :title="title" v-model="open" width="1000px">
  <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
    <el-row :gutter="20">
      <!-- 基础信息 -->
      <el-col :span="12">
        <el-divider content-position="left">基础信息</el-divider>
        <el-form-item label="员工编号" prop="employeeNumber">
          <el-input v-model="form.employeeNumber" />
        </el-form-item>
        <el-form-item label="员工姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属项目组" prop="projectGroup">
          <el-input v-model="form.projectGroup" />
        </el-form-item>
        <el-form-item label="应出勤天数" prop="attendanceDays">
          <el-input-number v-model="form.attendanceDays" :min="0" />
        </el-form-item>
        <el-form-item label="实际考勤" prop="actualAttendanceDays">
          <el-input-number v-model="form.actualAttendanceDays" :min="0" />
        </el-form-item>
        <el-form-item label="请假天数" prop="leaveDays">
          <el-input-number v-model="form.leaveDays" :min="0" />
        </el-form-item>
      </el-col>

      <!-- 工资明细 -->
      <el-col :span="12">
        <el-divider content-position="left">工资明细</el-divider>
        <el-form-item label="职称工资" prop="titleSalary">
          <el-input-number v-model="form.titleSalary" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="工龄工资" prop="workYearsSalary">
          <el-input-number v-model="form.workYearsSalary" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="岗位绩效" prop="positionPerformance">
          <el-input-number v-model="form.positionPerformance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="业务绩效" prop="businessPerformance">
          <el-input-number v-model="form.businessPerformance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-col>


    </el-row>

    <!-- 扣款信息 -->
    <el-divider content-position="left">扣款信息</el-divider>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="个人养老保险" prop="personalPension">
          <el-input-number v-model="form.personalPension" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="个人失业保险" prop="personalUnemploymentInsurance">
          <el-input-number v-model="form.personalUnemploymentInsurance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="个人医疗保险" prop="personalMedicalInsurance">
          <el-input-number v-model="form.personalMedicalInsurance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="个人公积金" prop="personalProvidentFund">
          <el-input-number v-model="form.personalProvidentFund" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="个人所得税" prop="incomeTax">
          <el-input-number v-model="form.incomeTax" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-divider content-position="left">补助明细</el-divider>
        <el-form-item label="话费补助" prop="phoneAllowance">
          <el-input-number v-model="form.phoneAllowance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="电脑补助" prop="computerAllowance">
          <el-input-number v-model="form.computerAllowance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="住房补助" prop="housingAllowance">
          <el-input-number v-model="form.housingAllowance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="高温补助" prop="highTemperatureAllowance">
          <el-input-number v-model="form.highTemperatureAllowance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="出差补助" prop="travelAllowance">
          <el-input-number v-model="form.travelAllowance" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="其他奖金及补贴补助" prop="otherBonusAndSubsidy">
          <el-input-number v-model="form.otherBonusAndSubsidy" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-col>
    </el-row>

        <!-- 信息 -->
      <el-divider content-position="left">实发工资</el-divider>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="应发工资" prop="totalPayable">
          <el-input-number v-model="form.totalPayable" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实发工资额" prop="salaryAmount">
          <el-input-number v-model="form.salaryAmount" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-col>
    </el-row>


  </el-form>
  <template #footer>
    <div class="dialog-footer">
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </template>
</el-dialog>
  
      <!-- 详情弹窗 -->
      <el-dialog title="工资详情" v-model="detailVisible" width="900px">
  <el-descriptions :column="2" border>
    <!-- 基本信息 -->
    <el-descriptions-item label="员工编号">{{ detailInfo.employeeNumber }}</el-descriptions-item>
    <el-descriptions-item label="员工姓名">{{ detailInfo.name }}</el-descriptions-item>
    <el-descriptions-item label="项目组">{{ detailInfo.projectGroup }}</el-descriptions-item>
    <el-descriptions-item label="应出勤天数">{{ detailInfo.attendanceDays }} 天</el-descriptions-item>
    <el-descriptions-item label="实际考勤">{{ detailInfo.actualAttendanceDays }} 天</el-descriptions-item>

    <!-- 工资明细 -->
    <el-descriptions-item label="职称工资">{{ detailInfo.titleSalary }} 元</el-descriptions-item>
    <el-descriptions-item label="工龄工资">{{ detailInfo.workYearsSalary }} 元</el-descriptions-item>
    <el-descriptions-item label="岗位绩效">{{ detailInfo.positionPerformance }} 元</el-descriptions-item>
    <el-descriptions-item label="业务绩效">{{ detailInfo.businessPerformance }} 元</el-descriptions-item>

    <!-- 补助明细 -->
    <el-descriptions-item label="话费补助">{{ detailInfo.phoneAllowance }} 元</el-descriptions-item>
    <el-descriptions-item label="电脑补助">{{ detailInfo.computerAllowance }} 元</el-descriptions-item>
    <el-descriptions-item label="住房补助">{{ detailInfo.housingAllowance }} 元</el-descriptions-item>
    <el-descriptions-item label="高温补助">{{ detailInfo.highTemperatureAllowance }} 元</el-descriptions-item>
    <el-descriptions-item label="出差补助">{{ detailInfo.travelAllowance }} 元</el-descriptions-item>
    <el-descriptions-item label="其他奖金及补贴">{{ detailInfo.otherBonusAndSubsidy }} 元</el-descriptions-item>

    <!-- 扣款信息 -->
    <el-descriptions-item label="个人养老保险">{{ detailInfo.personalPension }} 元</el-descriptions-item>
    <el-descriptions-item label="个人失业保险">{{ detailInfo.personalUnemploymentInsurance }} 元</el-descriptions-item>
    <el-descriptions-item label="个人医疗保险">{{ detailInfo.personalMedicalInsurance }} 元</el-descriptions-item>
    <el-descriptions-item label="个人公积金">{{ detailInfo.personalProvidentFund }} 元</el-descriptions-item>
    <el-descriptions-item label="个人所得税">{{ detailInfo.incomeTax }} 元</el-descriptions-item>

    <!-- 总计 -->
    <el-descriptions-item label="应发工资合计">{{ detailInfo.totalPayable }} 元</el-descriptions-item>
    <el-descriptions-item label="实发工资金额">{{ detailInfo.salaryAmount }} 元</el-descriptions-item>

    <!-- 其他信息 -->
    <el-descriptions-item label="备注">{{ detailInfo.remark }}</el-descriptions-item>
    <el-descriptions-item label="创建人">{{ detailInfo.createBy }}</el-descriptions-item>
    <el-descriptions-item label="创建时间">{{ dayjs(detailInfo.createTime).format('YYYY-MM-DD HH:mm') }}</el-descriptions-item>
    <el-descriptions-item label="更新时间">{{ dayjs(detailInfo.updateTime).format('YYYY-MM-DD HH:mm') }}</el-descriptions-item>
  </el-descriptions>
</el-dialog>

    </div>
  </template>
  
  <script setup>
  import { list, addpro, updatepro, delpro, input, importTemplate, importData,getpro } from "@/api/gcxm/payroll";
  import dayjs from 'dayjs';
  import { getCurrentInstance, onMounted, reactive, ref } from 'vue';
  
  const { proxy } = getCurrentInstance();
  
  const payrollList = ref([]);
  const open = ref(false);
  const loading = ref(true);
  const ids = ref([]);
  const single = ref(true);
  const multiple = ref(true);
  const total = ref(0);
  const title = ref("");
  const detailVisible = ref(false);
  const detailInfo = ref({});
  
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    employeeNumber: null,
    name: null,
    projectGroup: null
  });
  
  const form = ref({
  employeeNumber: '',                     // 员工编号
  name: '',                               // 员工姓名
  projectGroup: '',                       // 所属项目组
  attendanceDays: 0,                      // 应出勤天数
  actualAttendanceDays: 0,                // 实际考勤天数
  performanceEvaluation: 0,               // 绩效考核分数
  leaveDays: 0,                           // 请假天数
  titleSalary: 0,                         // 职称工资
  workYearsSalary: 0,                     // 工龄工资
  positionPerformance: 0,                 // 岗位绩效
  businessPerformance: 0,                 // 业务绩效
  phoneAllowance: 0,                      // 话费补助
  computerAllowance: 0,                   // 电脑补助
  housingAllowance: 0,                    // 住房补助
  highTemperatureAllowance: 0,            // 高温补助
  travelAllowance: 0,                     // 出差补助
  otherBonusAndSubsidy: 0,                // 其他奖金及补贴
  totalPayable: 0,                        // 应发工资合计
  personalPension: 0,                     // 个人养老保险
  personalUnemploymentInsurance: 0,      // 个人失业保险
  personalMedicalInsurance: 0,            // 个人医疗保险
  personalProvidentFund: 0,               // 个人公积金
  incomeTax: 0,                           // 个人所得税
  salaryAmount: 0,                        // 实发工资金额
  remark: ''                              // 备注
});

  
  const rules = {
    employeeNumber: [{ required: true, message: "员工编号不能为空", trigger: "blur" }],
    name: [{ required: true, message: "员工姓名不能为空", trigger: "blur" }],
    attendanceDays: [{ required: true, message: "应出勤天数不能为空", trigger: "blur" }]
  };
  
  // 查询列表
  function getList() {
    loading.value = true;
    list(queryParams).then(response => {
      payrollList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  }
  
  // 重置表单
  function reset() {
  form.value = {
    employeeNumber: '',
    name: '',
    projectGroup: '',
    attendanceDays: 0,
    actualAttendanceDays: 0,
    performanceEvaluation: 0,
    leaveDays: 0,
    titleSalary: 0,
    workYearsSalary: 0,
    positionPerformance: 0,
    businessPerformance: 0,
    phoneAllowance: 0,
    computerAllowance: 0,
    housingAllowance: 0,
    highTemperatureAllowance: 0,
    travelAllowance: 0,
    otherBonusAndSubsidy: 0,
    totalPayable: 0,
    personalPension: 0,
    personalUnemploymentInsurance: 0,
    personalMedicalInsurance: 0,
    personalProvidentFund: 0,
    incomeTax: 0,
    salaryAmount: 0,
    remark: ''
  };
  proxy.resetForm("formRef");
}

  
  // 表单提交
  function submitForm() {
    proxy.$refs.formRef.validate(valid => {
      if (valid) {
        if (form.value.id) {
            updatepro(form.value).then(() => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          });
        } else {
            addpro(form.value).then(() => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          });
        }
      }
    });
  }
  
  // 其他方法（handleAdd, handleUpdate, handleDelete等）与示例代码逻辑类似
  // 此处需根据实际API调整方法实现...

      /** 新增按钮 */
      function handleAdd() {
      reset();
      open.value = true;
      title.value = "新增支出信息";
    }

    
  function handleUpdate(row) {
      reset();
      const id = row.id || ids.value[0];
      getpro(id).then(response => {
        form.value = response.data;
        open.value = true;
        title.value = "修改支出信息";
      });
    }
  
      /** 删除按钮 */
      function handleDelete(row) {
      const ids = row.id || ids.value;
      proxy.$modal.confirm('确认删除选中数据？').then(() => {
        return delpro(ids);
      }).then(() => {
        getList();
        proxy.$modal.msgSuccess("删除成功");
      });
    }

    function handleQuery() {
         queryParams.pageNum = 1;
          getList();
    }

    function resetQuery() {
        proxy.resetForm("queryForm");
         handleQuery();
    }

    /** 取消按钮 */
function cancel() {
  proxy.$modal.confirm('确认关闭吗？未保存的数据将会丢失。').then(() => {
    open.value = false;
    reset();
  }).catch(() => {});
}

    function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
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

    // 表格合计计算方法
    function getSummaries(param) {
  const { columns, data } = param;
  const sums = [];
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计';
      return;
    }
    
    // 只统计所需的字段
    const columnsToSummarize = [
      'titleSalary', 'workYearsSalary', 'positionPerformance', 
      'businessPerformance', 'phoneAllowance', 'computerAllowance', 
      'housingAllowance', 'highTemperatureAllowance', 'travelAllowance', 
      'otherBonusAndSubsidy', 'totalPayable', 'personalPension', 
      'personalUnemploymentInsurance', 'personalMedicalInsurance', 
      'personalProvidentFund', 'incomeTax', 'salaryAmount'
    ];

    if (columnsToSummarize.includes(column.property)) {
      const values = data.map(item => Number(item[column.property]));
      if (!values.every(value => isNaN(value))) {
        sums[index] = values.reduce((prev, curr) => {
          const value = Number(curr);
          if (!isNaN(value)) {
            return prev + curr;
          }
          return prev;
        }, 0).toLocaleString();
      }
    }
  });
  return sums;
}

    function handleDetail(row) {
    getpro(row.id).then(response => {
    const data = response.data;
    // 格式化日期
    data.createTime = dayjs(data.createTime).format('YYYY-MM-DD HH:mm');
    data.updateTime = dayjs(data.updateTime).format('YYYY-MM-DD HH:mm');
    
    // 格式化金额
    data.titleSalary = data.titleSalary?.toLocaleString();
    data.workYearsSalary = data.workYearsSalary?.toLocaleString();
    data.positionPerformance = data.positionPerformance?.toLocaleString();
    data.businessPerformance = data.businessPerformance?.toLocaleString();
    data.phoneAllowance = data.phoneAllowance?.toLocaleString();
    data.computerAllowance = data.computerAllowance?.toLocaleString();
    data.housingAllowance = data.housingAllowance?.toLocaleString();
    data.highTemperatureAllowance = data.highTemperatureAllowance?.toLocaleString();
    data.travelAllowance = data.travelAllowance?.toLocaleString();
    data.otherBonusAndSubsidy = data.otherBonusAndSubsidy?.toLocaleString();
    data.totalPayable = data.totalPayable?.toLocaleString();
    data.personalPension = data.personalPension?.toLocaleString();
    data.personalUnemploymentInsurance = data.personalUnemploymentInsurance?.toLocaleString();
    data.personalMedicalInsurance = data.personalMedicalInsurance?.toLocaleString();
    data.personalProvidentFund = data.personalProvidentFund?.toLocaleString();
    data.incomeTax = data.incomeTax?.toLocaleString();
    data.salaryAmount = data.salaryAmount?.toLocaleString();
    
    detailInfo.value = data;
    detailVisible.value = true;
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
        a.download = '工资信息模板.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      });
    }
  

  // 初始化加载
  onMounted(() => {
    getList();
  });
  </script>
  
  <style scoped>
  /* 样式参考原有示例 */
  </style>
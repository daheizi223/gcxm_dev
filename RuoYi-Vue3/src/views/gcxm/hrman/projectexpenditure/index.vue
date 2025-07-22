<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="项目名称" prop="projectName">
        <el-input
          v-model="queryParams.projectName"
          placeholder="请输入项目名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业主单位" prop="ownerUnit">
        <el-input
          v-model="queryParams.ownerUnit"
          placeholder="请输入业主单位"
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
    <el-table v-loading="loading" :data="projectList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="项目年份" prop="projectYear" width="100" />
      <el-table-column label="项目组" prop="projectGroup" width="120" />
      <el-table-column label="项目名称" prop="projectName" width="200" />
      <el-table-column label="业主单位" prop="ownerUnit" width="200" />
      <el-table-column label="总成本" prop="summaryCost" width="120">
        <template #default="{row}">{{ row.summaryCost?.toLocaleString() }}</template>
      </el-table-column>
      <el-table-column label="产值" prop="productionValue" width="120">
        <template #default="{row}">{{ row.productionValue?.toLocaleString() }}</template>
      </el-table-column>
      <el-table-column label="利润率" prop="profitRate" width="100">
        <template #default="{row}">{{ (row.profitRate * 100).toFixed(0) }}%</template>
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

    <el-dialog :title="title" v-model="open" width="800px">
  <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
    <el-row :gutter="20">
      <el-col :span="12">
        <!-- 项目年份 -->
        <el-form-item label="项目年份" prop="projectYear">
          <el-input-number 
            v-model="form.projectYear"
            :min="2000"
            :max="2100"
            controls-position="right"
          />
        </el-form-item>
        <!-- 项目组 -->
        <el-form-item label="项目组" prop="projectGroup">
          <el-input v-model="form.projectGroup" />
        </el-form-item>
        <!-- 项目名称 -->
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" />
        </el-form-item>
        <!-- 业主单位 -->
        <el-form-item label="业主单位" prop="ownerUnit">
          <el-input v-model="form.ownerUnit" />
        </el-form-item>
        <!-- 人工成本 -->
        <el-form-item label="人工成本" prop="laborCost">
          <el-input-number 
            v-model="form.laborCost"
            :min="0"
            :precision="2"
            :step="1000"
          />
        </el-form-item>
        <!-- 人员数量 -->
        <el-form-item label="人员数量" prop="personnelCount">
          <el-input-number 
            v-model="form.personnelCount"
            :min="0"
            :step="1"
          />
        </el-form-item>
                <!-- 人均成本 -->
                <el-form-item label="人均成本" prop="perCapitaCost">
          <el-input-number 
            v-model="form.perCapitaCost"
            :min="0"
            :precision="2"
            :step="1000"
          />
        </el-form-item>
        <!-- 是否开票 -->
        <el-form-item label="是否开票" prop="invoiced">
          <el-select v-model="form.invoiced" placeholder="请选择">
            <el-option label="已开票" :value="1" />
            <el-option label="未开票" :value="0" />
          </el-select>
        </el-form-item>
        <!-- 是否回款 -->
        <el-form-item label="是否回款" prop="receivedPayment">
          <el-select v-model="form.receivedPayment" placeholder="请选择">
            <el-option label="已回款" :value="1" />
            <el-option label="未回款" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <!-- 租赁费用 -->
        <el-form-item label="房租费用" prop="rent">
          <el-input-number 
            v-model="form.rent"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 租赁期间 -->
        <el-form-item label="租房时间">
          <el-date-picker
            v-model="form.rentStartDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 48%; margin-right: 2%"
          />
          <el-date-picker
            v-model="form.rentEndDate"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 48%"
          />
        </el-form-item>
        <!-- 设备采购 -->
        <el-form-item label="设备采购额" prop="equipmentPurchaseAmount">
          <el-input-number 
            v-model="form.equipmentPurchaseAmount"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 外包成本 -->
        <el-form-item label="外协成本" prop="outsourcingCost">
          <el-input-number 
            v-model="form.outsourcingCost"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 资本成本 -->
        <el-form-item label="资金成本" prop="capitalCost">
          <el-input-number 
            v-model="form.capitalCost"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 平均成本 -->
        <el-form-item label="综合均摊成本" prop="averageCost">
          <el-input-number 
            v-model="form.averageCost"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 总成本 -->
        <el-form-item label="总成本" prop="summaryCost">
          <el-input-number 
            v-model="form.summaryCost"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 产值 -->
        <el-form-item label="产值" prop="productionValue">
          <el-input-number 
            v-model="form.productionValue"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 利润 -->
        <el-form-item label="利润" prop="profit">
          <el-input-number 
            v-model="form.profit"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <!-- 利润率 -->
        <el-form-item label="利润率" prop="profitRate">
          <el-input-number 
            v-model="form.profitRate"
            :min="0"
            :precision="2"
            :step="0.01"
          />
        </el-form-item>

        <el-form-item label="租车报销" prop="carReimbursement">
          <el-input-number 
            v-model="form.carReimbursement"
            :min="0"
            :precision="2"
          />
        </el-form-item>

        <el-form-item label="其他报销" prop="otherReimbursements">
          <el-input-number 
            v-model="form.otherReimbursements"
            :min="0"
            :precision="2"
          />
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
<!-- 详情弹窗 -->
<el-dialog title="项目详情" v-model="detailVisible" width="800px">
  <el-descriptions :column="2" border>
    <!-- 基础信息 -->
    <el-descriptions-item label="项目年份">{{ detailInfo.projectYear }}</el-descriptions-item>
    <el-descriptions-item label="项目组">{{ detailInfo.projectGroup }}</el-descriptions-item>
    <el-descriptions-item label="项目名称">{{ detailInfo.projectName }}</el-descriptions-item>
    <el-descriptions-item label="业主单位">{{ detailInfo.ownerUnit }}</el-descriptions-item>
    <el-descriptions-item label="备注">{{ detailInfo.remark || '-' }}</el-descriptions-item>

    <!-- 成本信息 -->
    <el-descriptions-item label="人工成本">{{ detailInfo.laborCost?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="人员数量">{{ detailInfo.personnelCount }}</el-descriptions-item>
    <el-descriptions-item label="人均成本">{{ detailInfo.perCapitaCost?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="租房费用">{{ detailInfo.rent?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="租房时间">
      {{ detailInfo.rentStartDate }} 至 {{ detailInfo.rentEndDate }}
    </el-descriptions-item>
    <el-descriptions-item label="租车报销">{{ detailInfo.carReimbursement?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="其他报销">{{ detailInfo.otherReimbursements?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="设备采购费用">{{ detailInfo.equipmentPurchaseAmount?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="外协成本">{{ detailInfo.outsourcingCost?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="综合均摊成本">{{ detailInfo.averageCost?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="资本成本">{{ detailInfo.capitalCost?.toLocaleString() }}</el-descriptions-item>

    <!-- 财务信息 -->
    <el-descriptions-item label="总成本汇总">{{ detailInfo.summaryCost?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="产值">{{ detailInfo.productionValue?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="利润">{{ detailInfo.profit?.toLocaleString() }}</el-descriptions-item>
    <el-descriptions-item label="利润率">{{ (detailInfo.profitRate * 100).toFixed(0) }}%</el-descriptions-item>
    <el-descriptions-item label="是否开票">
      {{ detailInfo.invoiced === 1 ? '已开票' : '未开票' }}
    </el-descriptions-item>
    <el-descriptions-item label="是否收款">
      {{ detailInfo.receivedPayment === 1 ? '已收款' : '未收款' }}
    </el-descriptions-item>

    <!-- 系统信息 -->
    <el-descriptions-item label="创建人">{{ detailInfo.createBy }}</el-descriptions-item>
    <el-descriptions-item label="创建时间">{{ detailInfo.createTime }}</el-descriptions-item>
    <el-descriptions-item label="更新人">{{ detailInfo.updateBy || '-' }}</el-descriptions-item>
    <el-descriptions-item label="更新时间">{{ detailInfo.updateTime || '-' }}</el-descriptions-item>
  </el-descriptions>
</el-dialog>
  </div>
</template>

<script setup>
import { listProjectexpenditure, getProjectexpenditure, addProjectexpenditure, updateProjectexpenditure, delProjectexpenditure,input,importTemplate,importData } from "@/api/gcxm/projectexpenditure";
import dayjs from 'dayjs';

const { proxy } = getCurrentInstance();

const projectList = ref([]);
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
  projectName: null,
  ownerUnit: null
});

const form = ref({
  projectYear: null,
  projectGroup: null,
  projectName: null,
  ownerUnit: null,
  laborCost: 0,
  personnelCount: 0,
  rent: 0,
  rentStartDate: null,
  rentEndDate: null,
  equipmentPurchaseAmount: 0,
  outsourcingCost: 0,
  capitalCost: 0
});
const rules = {
  projectYear: [{ required: true, message: "请选择项目年份", trigger: "blur" }],
  projectGroup: [{ required: true, message: "请输入项目组", trigger: "blur" }],
  projectName: [{ required: true, message: "请输入项目名称", trigger: "blur" }],
  ownerUnit: [{ required: true, message: "请输入业主单位", trigger: "blur" }],
  laborCost: [{ type: 'number', min: 0, message: '请输入有效金额' }],
  personnelCount: [{ type: 'number', min: 0, message: '请输入有效人数' }],
  perCapitaCost: [{ type: 'number', min: 0, message: '请输入人均成本' }],
  rent: [{ type: 'number', min: 0, message: '请输入房租费用' }],
  rentStartDate: [{ required: true, message: "请选择租房开始日期", trigger: "change" }],
  rentEndDate: [{ required: true, message: "请选择租房结束日期", trigger: "change" }],
  equipmentPurchaseAmount: [{ type: 'number', min: 0, message: '请输入设备采购额' }],
  outsourcingCost: [{ type: 'number', min: 0, message: '请输入外协成本' }],
  capitalCost: [{ type: 'number', min: 0, message: '请输入资金成本' }],
  averageCost: [{ type: 'number', min: 0, message: '请输入综合均摊成本' }],
  summaryCost: [{ type: 'number', min: 0, message: '请输入总成本' }],
  productionValue: [{ type: 'number', min: 0, message: '请输入产值' }],
  profit: [{ type: 'number', min: 0, message: '请输入利润' }],
  profitRate: [{ type: 'number', min: 0, max: 100, message: '请输入利润率（0-100）' }],
  invoiced: [{ required: true, message: "请选择是否开票", trigger: "change" }],
  receivedPayment: [{ required: true, message: "请选择是否回款", trigger: "change" }],
  carReimbursement: [{ type: 'number', min: 0, message: '请输入租车报销' }],
  otherReimbursements: [{ type: 'number', min: 0, message: '请输入其他报销' }],
};

/** 查询列表 */
function getList() {
  loading.value = true;
  listProjectexpenditure(queryParams).then(response => {
    projectList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function reset() {
  form.value = {
    projectYear: null,               // 项目年份
    projectGroup: '',                // 项目组
    projectName: '',                 // 项目名称
    ownerUnit: '',                   // 业主单位
    laborCost: 0,                    // 人工成本
    personnelCount: 0,               // 人员数量
    perCapitaCost: 0,                // 人均成本
    rent: 0,                         // 租赁费用
    rentStartDate: null,             // 租赁开始日期
    rentEndDate: null,               // 租赁结束日期
    equipmentPurchaseAmount: 0,      // 设备采购额
    outsourcingCost: 0,              // 外包成本
    capitalCost: 0,                  // 资本成本
    averageCost: 0,                  // 综合均摊成本
    summaryCost: 0,                  // 总成本
    productionValue: 0,              // 产值
    profit: 0,                       // 利润
    profitRate: 0,                   // 利润率
    invoiced: 0,                     // 是否开票，默认为未开票
    receivedPayment: 0,              // 是否回款，默认为未回款
    carReimbursement: 0,             // 租车报销
    otherReimbursements: 0,          // 其他报销
    remark: '',                      // 备注
  };
  proxy.resetForm("formRef");
}

  
  /** 新增按钮 */
  function handleAdd() {
    reset();
    open.value = true;
    title.value = "新增支出信息";
  }

  function handleUpdate(row) {
    reset();
    const id = row.id || ids.value[0];
    getProjectexpenditure(id).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改支出信息";
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

function handleQuery() {
    queryParams.pageNum = 1;
    getList();
  }

    /** 提交表单 */
    function submitForm() {
    proxy.$refs.formRef.validate(valid => {
      if (valid) {
        if (form.value.id) {
          updateProjectexpenditure(form.value).then(() => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          });
        } else {
          addProjectexpenditure(form.value).then(() => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          });
        }
      }
    });
  }

  /** 取消按钮 */
function cancel() {
  proxy.$modal.confirm('确认关闭吗？未保存的数据将会丢失。').then(() => {
    open.value = false;
    reset();
  }).catch(() => {});
}

  function resetQuery() {
    proxy.$refs.queryForm.resetFields();
    handleQuery(); 
  }

  /** 删除按钮 */
  function handleDelete(row) {
    const ids = row.id || ids.value;
    proxy.$modal.confirm('确认删除选中数据？').then(() => {
      return delProjectexpenditure(ids);
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
      a.download = '支出信息模板.xlsx';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

function handleDetail(row) {
  getProjectexpenditure(row.id).then(response => {
    const data = response.data;

    // 格式化日期
    data.rentStartDate = dayjs(data.rentStartDate).format('YYYY-MM-DD');
    data.rentEndDate = dayjs(data.rentEndDate).format('YYYY-MM-DD');
    data.createTime = dayjs(data.createTime).format('YYYY-MM-DD');
    data.updateTime = dayjs(data.updateTime).format('YYYY-MM-DD');

    // 赋值并显示
    detailInfo.value = data;
    detailVisible.value = true;
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
  --el-descriptions-item-bordered-label-background: #f5f7fa; /* 标签背景色 */
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
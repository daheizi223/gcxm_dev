<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="合同编号" prop="contractNo">
        <el-input
          v-model="queryParams.contractNo"
          placeholder="请输入合同编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同名称" prop="contractName">
        <el-input
          v-model="queryParams.contractName"
          placeholder="请输入合同名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="甲方" prop="partyA">
        <el-input
          v-model="queryParams.partyA"
          placeholder="请输入甲方"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="乙方" prop="partyB">
        <el-input
          v-model="queryParams.partyB"
          placeholder="请输入乙方"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同状态" prop="contractStatus">
        <el-select v-model="queryParams.contractStatus" placeholder="请选择合同状态" clearable>
          <el-option
            v-for="dict in contract_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="签订时间" style="width: 308px">
        <el-date-picker
          v-model="daterangeSigningDate"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['gcxm:contract:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['gcxm:contract:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gcxm:contract:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['gcxm:contract:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="contractList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="合同编号" align="center" prop="contractNo" />
      <el-table-column label="合同名称" align="center" prop="contractName" show-overflow-tooltip />
      <el-table-column label="合同金额" align="center" prop="contractAmount">
        <template #default="scope">
          <span v-if="scope.row.contractAmount">{{ formatMoney(scope.row.contractAmount) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="甲方" align="center" prop="partyA" show-overflow-tooltip />
      <el-table-column label="乙方" align="center" prop="partyB" show-overflow-tooltip />
      <el-table-column label="合同状态" align="center" prop="contractStatus">
        <template #default="scope">
          <dict-tag :options="contract_status" :value="scope.row.contractStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="签订时间" align="center" prop="signingDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.signingDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['gcxm:contract:query']">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gcxm:contract:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gcxm:contract:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改合同信息对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form ref="contractRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同状态" prop="contractStatus">
              <el-select v-model="form.contractStatus" placeholder="请选择合同状态">
                <el-option
                  v-for="dict in contract_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="合同名称" prop="contractName">
              <el-input v-model="form.contractName" placeholder="请输入合同名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同金额" prop="contractAmount">
              <el-input v-model="form.contractAmount" placeholder="请输入合同金额" type="number" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="签订时间" prop="signingDate">
              <el-date-picker clearable
                v-model="form.signingDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择签订时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同开始日期" prop="startDate">
              <el-date-picker clearable
                v-model="form.startDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择合同开始日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同结束日期" prop="endDate">
              <el-date-picker clearable
                v-model="form.endDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择合同结束日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="甲方" prop="partyA">
              <el-input v-model="form.partyA" placeholder="请输入甲方" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="乙方" prop="partyB">
              <el-input v-model="form.partyB" placeholder="请输入乙方" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="甲方联系人" prop="contactPersonA">
              <el-input v-model="form.contactPersonA" placeholder="请输入甲方联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="甲方联系电话" prop="contactPhoneA">
              <el-input v-model="form.contactPhoneA" placeholder="请输入甲方联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="乙方联系人" prop="contactPersonB">
              <el-input v-model="form.contactPersonB" placeholder="请输入乙方联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="乙方联系电话" prop="contactPhoneB">
              <el-input v-model="form.contactPhoneB" placeholder="请输入乙方联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="主要内容">
              <el-input v-model="form.mainContent" type="textarea" :rows="3" placeholder="请输入主要内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注"></el-input>
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

    <!-- 合同详情对话框 -->
    <el-dialog title="合同详情" v-model="detailOpen" width="1400px" append-to-body>
      <ContractDetail 
        v-if="detailOpen && currentContractId" 
        :contractId="currentContractId" 
        @close="handleCloseDetail" 
        @edit="handleEditFromDetail"
      />
    </el-dialog>
  </div>
</template>

<script setup name="Contract">
import { listContract, getContract, delContract, addContract, updateContract, generateContractNo, checkContractNoUnique } from "@/api/gcxm/contract";
import ContractDetail from "./ContractDetail.vue";

const { proxy } = getCurrentInstance();
const { contract_status } = proxy.useDict('contract_status');

const contractList = ref([]);
const open = ref(false);
const detailOpen = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const daterangeSigningDate = ref([]);
const generateLoading = ref(false);
const currentContractId = ref(null);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    contractNo: null,
    contractName: null,
    partyA: null,
    partyB: null,
    contractStatus: null,
    signingDate: null,
  },
  rules: {}
});

const { queryParams, form } = toRefs(data);

// 定义验证规则
const rules = ref({
  contractNo: [
    { required: true, message: "合同编号不能为空", trigger: "blur" },
    { 
      validator: async (rule, value, callback) => {
        if (!value) return callback();
        try {
          const currentId = form.value?.id || null;
          const res = await checkContractNoUnique(value, currentId);
          // 后端返回 true 表示唯一，false 表示不唯一
          if (res && res.data === false) {
            callback(new Error('合同编号已存在'));
          } else {
            callback();
          }
        } catch (error) {
          console.error('检查合同编号唯一性失败:', error);
          callback();
        }
      }, 
      trigger: "blur" 
    }
  ],
  contractName: [
    { required: true, message: "合同名称不能为空", trigger: "blur" }
  ],
});

/** 查询合同信息列表 */
function getList() {
  loading.value = true;
  queryParams.value.params = {};
  if (null != daterangeSigningDate && '' != daterangeSigningDate) {
    queryParams.value.params["beginSigningDate"] = daterangeSigningDate.value[0];
    queryParams.value.params["endSigningDate"] = daterangeSigningDate.value[1];
  }
  listContract(queryParams.value).then(response => {
    contractList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  }).catch(error => {
    console.error('获取合同列表失败:', error);
    contractList.value = [];
    total.value = 0;
    loading.value = false;
    proxy.$modal.msgError("获取合同列表失败: " + (error.msg || error.message || '网络错误'));
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    contractNo: null,
    contractName: null,
    mainContent: null,
    contractAmount: null,
    signingDate: null,
    partyA: null,
    partyB: null,
    contractStatus: "DRAFT",
    startDate: null,
    endDate: null,
    contactPersonA: null,
    contactPhoneA: null,
    contactPersonB: null,
    contactPhoneB: null,
    remark: null
  };
  proxy.resetForm("contractRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  daterangeSigningDate.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加合同信息";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getContract(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改合同信息";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["contractRef"].validate(valid => {
    if (valid) {
      console.log('表单验证通过，提交数据:', form.value);
      if (form.value.id != null) {
        updateContract(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
          console.error('修改合同失败:', error);
          proxy.$modal.msgError("修改失败: " + (error.msg || '系统异常'));
        });
      } else {
        addContract(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch(error => {
          console.error('新增合同失败:', error);
          proxy.$modal.msgError("新增失败: " + (error.msg || '系统异常'));
        });
      }
    } else {
      console.log('表单验证失败');
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除合同信息编号为"' + _ids + '"的数据项？').then(function() {
    return delContract(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('/prod-api/contract/export', {
    ...queryParams.value
  }, `contract_${new Date().getTime()}.xlsx`)
}

/** 生成合同编号 */
function handleGenerateContractNo() {
  generateLoading.value = true;
  generateContractNo().then(response => {
    form.value.contractNo = response.data;
    generateLoading.value = false;
  }).catch(() => {
    generateLoading.value = false;
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  currentContractId.value = row.id;
  detailOpen.value = true;
}

/** 关闭详情对话框 */
function handleCloseDetail() {
  detailOpen.value = false;
  currentContractId.value = null;
}

/** 从详情页面编辑 */
function handleEditFromDetail(contractData) {
  // 关闭详情页面
  detailOpen.value = false;
  // 打开编辑页面
  form.value = { ...contractData };
  open.value = true;
  title.value = "修改合同信息";
}

/** 格式化金额 */
function formatMoney(money) {
  if (!money) return '';
  return '¥' + Number(money).toLocaleString();
}

onMounted(() => {
  getList();
});
</script>

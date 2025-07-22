<template>
    <div class="app-container">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="合同名称" prop="nameOfContract">
        <el-input
          v-model="queryParams.nameOfContract"
          placeholder="请输入合同名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合作单位" prop="cooperativeUnit">
        <el-input
          v-model="queryParams.cooperativeUnit"
          placeholder="请输入合作单位"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务部门" prop="businessUnits">
        <el-input
          v-model="queryParams.businessUnits"
          placeholder="请输入业务部门"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="到账金额" prop="searchAmount">
  <el-input-number
    v-model="queryParams.searchAmount"
    :min="0"
    :precision="2"
    :controls="false"
    style="width: 200px"
    placeholder="请输入查询金额"
    clearable
    @keyup.enter="handleQuery"
  />
</el-form-item>
      <el-form-item label="开票时间范围" prop="billingTimeRange">

  <el-date-picker
    v-model="queryParams.billingTimeRange"
    type="daterange"
    range-separator="至"
    start-placeholder="开始日期"
    end-placeholder="结束日期"
    value-format="YYYY-MM-DD"
    @change="handleQuery"
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
        :data="projectList" 
        @selection-change="handleSelectionChange"
        :summary-method="getSummaries"
        show-summary
      >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="省份" prop="provinces" width="100" />
      <el-table-column label="城市" prop="cities" width="100" />
      <el-table-column label="合同名称" prop="nameOfContract" width="200" show-overflow-tooltip />
      <el-table-column label="业务部门" prop="businessUnits" width="120" />
      <el-table-column label="合作单位" prop="cooperativeUnit" width="120" />
      <el-table-column label="合同金额（元）" prop="contractAmount" width="120">
        <template #default="{row}">{{ row.contractAmount?.toLocaleString() }}</template>
      </el-table-column>
      <el-table-column label="总到账金额（元）" prop="totalAmountReceived" width="120">
        <template #default="{row}">{{ row.totalAmountReceived?.toLocaleString() }}</template>
      </el-table-column>
      <el-table-column label="总开票金额（元）" prop="totalInvoiceAmount" width="120">
  <template #default="{row}">{{ row.totalInvoiceAmount?.toLocaleString() }}</template>
</el-table-column>
      <el-table-column label="支付比例" prop="paymentRatio" width="100">
        <template #default="{row}">{{ row.paymentRatio }}%</template>
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
  
    <el-dialog :title="title" v-model="open" width="1200px">
  <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
    <el-row :gutter="20">
      <el-col :span="12">
        <!-- 基础信息 -->
        <el-divider content-position="left">基础信息</el-divider>
        <el-form-item label="省份" prop="provinces" required>
          <el-input v-model="form.provinces" />
        </el-form-item>
        <el-form-item label="城市" prop="cities" required>
          <el-input v-model="form.cities" />
        </el-form-item>
        <el-form-item label="合同名称" prop="nameOfContract" required>
          <el-input v-model="form.nameOfContract" />
        </el-form-item>
        <el-form-item label="业务部门" prop="businessUnits" required>
  <el-select
    v-model="form.businessUnits"
    placeholder="请选择业务部门"
    style="width: 100%"
  >
    <el-option
      v-for="item in businessUnitOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</el-form-item>
        <el-form-item label="专业" prop="profession">
          <el-input v-model="form.profession" />
        </el-form-item>
        <el-form-item label="归属年份" prop="year" required>
          <el-input v-model="form.year" />
        </el-form-item>
        <el-form-item label="启动时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="stopTime">
          <el-date-picker
            v-model="form.stopTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="取费标准（合同）" prop="collectionStandard">
          <el-input v-model="form.collectionStandard" />
        </el-form-item>
        <el-form-item label="项目负责人" prop="leader">
          <el-input v-model="form.leader" />
        </el-form-item>
        <el-form-item label="合作单位" prop="cooperativeUnit" required>
          <el-input v-model="form.cooperativeUnit" />
        </el-form-item>
        <el-form-item label="合作单位联系人" prop="cooperativeUnitPerson">
          <el-input v-model="form.cooperativeUnitPerson" />
        </el-form-item>
        <el-form-item label="合同签订情况" prop="contractSigningSituation">
          <el-select v-model="form.contractSigningSituation" placeholder="请选择">
            <el-option label="已签订" :value="1" />
            <el-option label="未签订" :value="0" />
          </el-select>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <!-- 合同信息 -->
        <el-divider content-position="left">合同信息</el-divider>
        <el-form-item label="合同金额（元）" prop="contractAmount">
          <el-input-number 
            v-model="form.contractAmount"
            :min="0"
            :precision="2"
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否有外协" prop="outsource">
  <el-select v-model="form.outsource" placeholder="请选择">
    <el-option label="有外协" :value="1" />
    <el-option label="无外协" :value="0" />
  </el-select>
</el-form-item>
        <el-form-item label="取费标准(外协)" prop="collectionStandardOut">
          <el-input v-model="form.collectionStandardOut" />
        </el-form-item>
        <el-form-item label="订单号" prop="orderNumber">
          <el-input v-model="form.orderNumber" />
        </el-form-item>
        <el-form-item label="签订日期" prop="dateOfSigning">
          <el-date-picker
            v-model="form.dateOfSigning"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="确认订单号" prop="orderNumberReal">
          <el-input v-model="form.orderNumberReal" />
        </el-form-item>
        <el-form-item label="确认签订日期" prop="dateOfSigningReal">
          <el-date-picker
            v-model="form.dateOfSigningReal"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="项目应收金额（元）" prop="projectReceivables">
          <el-input-number 
            v-model="form.projectReceivables"
            :min="0"
            :precision="2"
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="订单备注" prop="orderNotes">
          <el-input v-model="form.orderNotes" type="textarea" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 发票信息 -->
    <el-divider content-position="left">发票信息</el-divider>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-form-item label="发票编号1" prop="invoiceNumber1" required>
          <el-input v-model="form.invoiceNumber1" />
        </el-form-item>
        <el-form-item label="发票金额1（元）" prop="invoiceAmount1" required>
          <el-input-number 
            v-model="form.invoiceAmount1"
            :min="0"
            :precision="2"
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="开票时间1" prop="billingTime1" required>
          <el-date-picker
            v-model="form.billingTime1"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="到账金额1（元）" prop="amountReceived1" required>
          <el-input-number 
            v-model="form.amountReceived1"
            :min="0"
            :precision="2"
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="到账时间1" prop="arrivaTime1" required>
          <el-date-picker
            v-model="form.arrivaTime1"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
      </el-col>
        <!-- 发票2信息 -->
  <el-col :span="8">
    <el-form-item label="发票编号2" prop="invoiceNumber2">
      <el-input v-model="form.invoiceNumber2" />
    </el-form-item>
    <el-form-item label="发票金额2（元）" prop="invoiceAmount2">
      <el-input-number 
        v-model="form.invoiceAmount2"
        :min="0"
        :precision="2"
        :controls="false"
        style="width: 100%"
      />
    </el-form-item>
    <el-form-item label="开票时间2" prop="billingTime2">
      <el-date-picker
        v-model="form.billingTime2"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="选择日期"
      />
    </el-form-item>
    <el-form-item label="到账金额2（元）" prop="amountReceived2">
      <el-input-number 
        v-model="form.amountReceived2"
        :min="0"
        :precision="2"
        :controls="false"
        style="width: 100%"
      />
    </el-form-item>
    <el-form-item label="到账时间2" prop="arrivaTime2">
      <el-date-picker
        v-model="form.arrivaTime2"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="选择日期"
      />
    </el-form-item>
  </el-col>

  <!-- 发票3信息 -->
  <el-col :span="8">
    <el-form-item label="发票编号3" prop="invoiceNumber3">
      <el-input v-model="form.invoiceNumber3" />
    </el-form-item>
    <el-form-item label="发票金额3（元）" prop="invoiceAmount3">
      <el-input-number 
        v-model="form.invoiceAmount3"
        :min="0"
        :precision="2"
        :controls="false"
        style="width: 100%"
      />
    </el-form-item>
    <el-form-item label="开票时间3" prop="billingTime3">
      <el-date-picker
        v-model="form.billingTime3"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="选择日期"
      />
    </el-form-item>
    <el-form-item label="到账金额3（元）" prop="amountReceived3">
      <el-input-number 
        v-model="form.amountReceived3"
        :min="0"
        :precision="2"
        :controls="false"
        style="width: 100%"
      />
    </el-form-item>
    <el-form-item label="到账时间3" prop="arrivaTime3">
      <el-date-picker
        v-model="form.arrivaTime3"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="选择日期"
      />
    </el-form-item>

  </el-col>

    </el-row>

    <!-- 其他信息 -->
    <el-divider content-position="left">其他信息</el-divider>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="总到账金额（元）" prop="totalAmountReceived">
  <el-input-number 
    v-model="form.totalAmountReceived"
    :min="0"
    :precision="2"
    :controls="false"
    style="width: 100%"
    :disabled="true"
    readonly
  />
</el-form-item>

<el-form-item label="总开票金额（元）" prop="totalInvoiceAmount">
  <el-input-number 
    v-model="form.totalInvoiceAmount"
    :min="0"
    :precision="2"
    :controls="false"
    style="width: 100%"
    :disabled="true"
    readonly
  />
</el-form-item>

        <el-form-item label="支付比例" prop="paymentRatio">
          <el-input-number 
            v-model="form.paymentRatio"
            :min="0"
            :max="100"
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="未支付金额（元）" prop="unpaidAmount">
          <el-input-number 
            v-model="form.unpaidAmount"
            :min="0"
            :precision="2"
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="到账信息备注" prop="notes">
          <el-input v-model="form.notes" type="textarea" />
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
<!-- 详情弹窗 -->
<el-dialog title="项目详情" v-model="detailVisible" width="1200px">
  <el-descriptions :column="2" border>
    <!-- 基础信息 -->
    <el-descriptions-item label="省份">{{ detailInfo.provinces || '' }}</el-descriptions-item>
    <el-descriptions-item label="城市">{{ detailInfo.cities || '' }}</el-descriptions-item>
    <el-descriptions-item label="合同名称">{{ detailInfo.nameOfContract || '' }}</el-descriptions-item>
    <el-descriptions-item label="业务部门">{{ detailInfo.businessUnits || '' }}</el-descriptions-item>
    <el-descriptions-item label="专业">{{ detailInfo.profession || '' }}</el-descriptions-item>
    <el-descriptions-item label="归属年份">{{ detailInfo.year || '' }}</el-descriptions-item>
    <el-descriptions-item label="启动时间">{{ detailInfo.startTime ? dayjs(detailInfo.startTime).format('YYYY-MM-DD') : '' }}</el-descriptions-item>
    <el-descriptions-item label="结束时间">{{ detailInfo.stopTime ? dayjs(detailInfo.stopTime).format('YYYY-MM-DD') : '' }}</el-descriptions-item>
    <el-descriptions-item label="取费标准（合同）">{{ detailInfo.collectionStandard || '' }}</el-descriptions-item>
    <el-descriptions-item label="项目负责人">{{ detailInfo.leader || '' }}</el-descriptions-item>
    <el-descriptions-item label="合作单位">{{ detailInfo.cooperativeUnit || '' }}</el-descriptions-item>
    <el-descriptions-item label="合作单位联系人">{{ detailInfo.cooperativeUnitPerson || '' }}</el-descriptions-item>
    <el-descriptions-item label="合同签订情况">{{ detailInfo.contractSigningSituation === 1 ? '已签订' : '未签订' }}</el-descriptions-item>
    <el-descriptions-item label="合同金额">{{ detailInfo.contractAmount ? `${detailInfo.contractAmount.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="是否有外协">{{ detailInfo.outsource === 1 ? '有外协' : '无外协' }}</el-descriptions-item>
    <el-descriptions-item label="取费标准(外协)">{{ detailInfo.collectionStandardOut || '' }}</el-descriptions-item>
    <el-descriptions-item label="订单号">{{ detailInfo.orderNumber || '' }}</el-descriptions-item>
    <el-descriptions-item label="签订日期">{{ detailInfo.dateOfSigning ? dayjs(detailInfo.dateOfSigning).format('YYYY-MM-DD') : '' }}</el-descriptions-item>
    <el-descriptions-item label="确认订单号">{{ detailInfo.orderNumberReal || '' }}</el-descriptions-item>
    <el-descriptions-item label="确认签订日期">{{ detailInfo.dateOfSigningReal ? dayjs(detailInfo.dateOfSigningReal).format('YYYY-MM-DD') : '' }}</el-descriptions-item>
    <el-descriptions-item label="项目应收金额">{{ detailInfo.projectReceivables ? `${detailInfo.projectReceivables.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="订单备注">{{ detailInfo.orderNotes || '' }}</el-descriptions-item>

    <!-- 发票信息 -->
    <el-descriptions-item label="发票编号1">{{ detailInfo.invoiceNumber1 || '' }}</el-descriptions-item>
    <el-descriptions-item label="发票金额1">{{ detailInfo.invoiceAmount1 ? `${detailInfo.invoiceAmount1.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="开票时间1">{{ detailInfo.billingTime1 ? dayjs(detailInfo.billingTime1).format('YYYY-MM-DD') : '' }}</el-descriptions-item>
    <el-descriptions-item label="到账金额1">{{ detailInfo.amountReceived1 ? `${detailInfo.amountReceived1.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="到账时间1">{{ detailInfo.arrivaTime1 ? dayjs(detailInfo.arrivaTime1).format('YYYY-MM-DD') : '' }}</el-descriptions-item>

    <el-descriptions-item label="发票编号2">{{ detailInfo.invoiceNumber2 || '' }}</el-descriptions-item>
    <el-descriptions-item label="发票金额2">{{ detailInfo.invoiceAmount2 ? `${detailInfo.invoiceAmount2.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="开票时间2">{{ detailInfo.billingTime2 ? dayjs(detailInfo.billingTime2).format('YYYY-MM-DD') : '' }}</el-descriptions-item>
    <el-descriptions-item label="到账金额2">{{ detailInfo.amountReceived2 ? `${detailInfo.amountReceived2.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="到账时间2">{{ detailInfo.arrivaTime2 ? dayjs(detailInfo.arrivaTime2).format('YYYY-MM-DD') : '' }}</el-descriptions-item>

    <el-descriptions-item label="发票编号3">{{ detailInfo.invoiceNumber3 || '' }}</el-descriptions-item>
    <el-descriptions-item label="发票金额3">{{ detailInfo.invoiceAmount3 ? `${detailInfo.invoiceAmount3.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="开票时间3">{{ detailInfo.billingTime3 ? dayjs(detailInfo.billingTime3).format('YYYY-MM-DD') : '' }}</el-descriptions-item>
    <el-descriptions-item label="到账金额3">{{ detailInfo.amountReceived3 ? `${detailInfo.amountReceived3.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="到账时间3">{{ detailInfo.arrivaTime3 ? dayjs(detailInfo.arrivaTime3).format('YYYY-MM-DD') : '' }}</el-descriptions-item>

    <!-- 财务信息 -->
    <el-descriptions-item label="总到账金额">{{ detailInfo.totalAmountReceived ? `${detailInfo.totalAmountReceived.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="总开票金额">
  {{ detailInfo.totalInvoiceAmount ? `${detailInfo.totalInvoiceAmount.toLocaleString()} 元` : '' }}
</el-descriptions-item>
    <el-descriptions-item label="支付比例">{{ detailInfo.paymentRatio ? `${detailInfo.paymentRatio}%` : '' }}</el-descriptions-item>
    <el-descriptions-item label="未支付金额">{{ detailInfo.unpaidAmount ? `${detailInfo.unpaidAmount.toLocaleString()} 元` : '' }}</el-descriptions-item>
    <el-descriptions-item label="到账信息备注">{{ detailInfo.notes || '' }}</el-descriptions-item>

    <!-- 系统信息 -->
    <el-descriptions-item label="创建人">{{ detailInfo.createBy || '' }}</el-descriptions-item>
    <el-descriptions-item label="创建时间">{{ detailInfo.createTime ? dayjs(detailInfo.createTime).format('YYYY-MM-DD HH:mm') : '' }}</el-descriptions-item>
    <el-descriptions-item label="更新人">{{ detailInfo.updateBy || '' }}</el-descriptions-item>
    <el-descriptions-item label="更新时间">{{ detailInfo.updateTime ? dayjs(detailInfo.updateTime).format('YYYY-MM-DD HH:mm') : '' }}</el-descriptions-item>
    <el-descriptions-item label="备注">{{ detailInfo.remark || '' }}</el-descriptions-item>
  </el-descriptions>
</el-dialog>
    </div>
  </template>
  
  <script setup>
  import { list, getpro, addpro, updatepro, delpro,input,importTemplate,importData } from "@/api/gcxm/income";
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

  const businessUnitOptions = [
  { label: '设计部', value: '设计部' },
  { label: '信息化部', value: '信息化部' },
  { label: '电力设计部', value: '电力设计部' },
  { label: '智能制造事业部', value: '智能制造事业部' }
];
  
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    projectName: null,
    ownerUnit: null,
    billingTimeRange: [], // 添加日期范围数组
  billingTimeStart: null, // 开始时间
  billingTimeEnd: null,   // 结束时间
  searchAmount: null 
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
  provinces: [{ required: true, message: "请输入省份", trigger: "blur" }],
  cities: [{ required: true, message: "请输入城市", trigger: "blur" }],
  nameOfContract: [{ required: true, message: "请输入合同名称", trigger: "blur" }],
  businessUnits: [{ required: true, message: "请输入业务部门", trigger: "blur" }],
  year: [{ required: true, message: "请输入归属年份", trigger: "blur" }],
  cooperativeUnit: [{ required: true, message: "请输入合作单位", trigger: "blur" }],
  invoiceNumber1: [{ required: true, message: "请输入发票编号1", trigger: "blur" }],
  invoiceAmount1: [{ required: true, message: "请输入发票金额1", trigger: "blur" }],
  billingTime1: [{ required: true, message: "请选择开票时间1", trigger: "change" }],
  contractAmount: [{ required: true, message: "请输入合同金额", trigger: "blur" }],
  projectReceivables: [{ required: true, message: "请输入项目应收金额", trigger: "blur" }],
  
  // 保留其他字段的验证规则
  // startTime: [{ required: true, message: "请选择启动时间", trigger: "change" }],
  // stopTime: [{ required: true, message: "请选择结束时间", trigger: "change" }],
};
  
  /** 查询列表 */
  function getList() {
    loading.value = true;
    list(queryParams).then(response => {
      projectList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  }
  
  function reset() {
  // 重新初始化表单字段
  form.value = {
    provinces: '',
    cities: '',
    nameOfContract: '',
    totalInvoiceAmount: 0,
    businessUnits: '',
    profession: '',
    year: '',
    startTime: null,
    stopTime: null,
    collectionStandard: '',
    leader: '',
    cooperativeUnit: '',
    cooperativeUnitPerson: '',
    contractSigningSituation:1, // 默认值
    contractAmount: 0,
    outsource: 0, // 默认值
    collectionStandardOut: '',
    orderNumber: '',
    dateOfSigning: null,
    orderNumberReal: '',
    dateOfSigningReal: null,
    projectReceivables: 0,
    orderNotes: '',
    invoiceNumber1: '',
    invoiceAmount1: 0,
    billingTime1: null,
    amountReceived1: 0,
    arrivaTime1: null,
    invoiceNumber2: '', // 发票2
    invoiceAmount2: 0,
    billingTime2: null,
    amountReceived2: 0,
    arrivaTime2: null,
    invoiceNumber3: '', // 发票3
    invoiceAmount3: 0,
    billingTime3: null,
    amountReceived3: 0,
    arrivaTime3: null,
    totalAmountReceived: 0,
    paymentRatio: 0,
    unpaidAmount: 0,
    notes: '',
  };

  // 重置表单
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
      getpro(id).then(response => {
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
       // 处理日期范围
  if (queryParams.billingTimeRange && queryParams.billingTimeRange.length === 2) {
    queryParams.billingTimeStart = queryParams.billingTimeRange[0];
    queryParams.billingTimeEnd = queryParams.billingTimeRange[1];
  } else {
    queryParams.billingTimeStart = null;
    queryParams.billingTimeEnd = null;
  }
  
  queryParams.pageNum = 1;
  getList();
    }
  
      /** 提交表单 */
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
  
    function resetQuery() {
      queryParams.billingTimeRange = []; // 重置日期范围
  queryParams.billingTimeStart = null;
  queryParams.billingTimeEnd = null;
  queryParams.searchAmount = null; // 重置搜索金额
      proxy.$refs.queryForm.resetFields();
      handleQuery(); 
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

    // 添加统计方法：
const getSummaries = (param) => {
  const { columns, data } = param;
  const sums = [];
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '总计';
      return;
    }
    
    const values = data.map(item => {
      if (column.property === 'contractAmount') return Number(item.contractAmount) || 0;
      if (column.property === 'totalAmountReceived') return Number(item.totalAmountReceived) || 0;
      if (column.property === 'totalInvoiceAmount') return Number(item.totalInvoiceAmount) || 0;
      return 0;
    });

    if (column.property === 'contractAmount' || 
        column.property === 'totalAmountReceived' || 
        column.property === 'totalInvoiceAmount') {
      sums[index] = values.reduce((a, b) => a + b, 0).toLocaleString();
    } else {
      sums[index] = '--';
    }
  });

  return sums;
};
  
  
    function handleImportTemplate() {
      importTemplate().then(res => {
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = '收入信息模板.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      });
    }

    /** 取消按钮 */
function cancel() {
  proxy.$modal.confirm('确认关闭吗？未保存的数据将会丢失。').then(() => {
    open.value = false;
    reset();
  }).catch(() => {});
}
  
  function handleDetail(row) {
    getpro(row.id).then(response => {
    const data = response.data;
    // 格式化日期
    data.startTime = dayjs(data.startTime).format('YYYY-MM-DD');
    data.stopTime = dayjs(data.stopTime).format('YYYY-MM-DD');
    data.arrivaTime1 = dayjs(data.arrivaTime1).format('YYYY-MM-DD');
    
    detailInfo.value = data;
    detailVisible.value = true;
  });
}
  
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

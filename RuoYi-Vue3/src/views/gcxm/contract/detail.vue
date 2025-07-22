<template>
  <div class="contract-detail">
    <el-row :gutter="20">
      <!-- 合同基本信息 -->
      <el-col :span="16">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>合同基本信息</span>
              <el-button class="button" type="text" @click="handleEdit" v-hasPermi="['gcxm:contract:edit']">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="合同编号">{{ contractInfo.contractNo }}</el-descriptions-item>
            <el-descriptions-item label="合同状态">
              <dict-tag :options="contract_status" :value="contractInfo.contractStatus"/>
            </el-descriptions-item>
            <el-descriptions-item label="合同名称" :span="2">{{ contractInfo.contractName }}</el-descriptions-item>
            <el-descriptions-item label="合同金额">
              <span v-if="contractInfo.contractAmount">{{ formatMoney(contractInfo.contractAmount) }}</span>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="签订时间">
              {{ parseTime(contractInfo.signingDate, '{y}-{m}-{d}') }}
            </el-descriptions-item>
            <el-descriptions-item label="合同开始日期">
              {{ parseTime(contractInfo.startDate, '{y}-{m}-{d}') }}
            </el-descriptions-item>
            <el-descriptions-item label="合同结束日期">
              {{ parseTime(contractInfo.endDate, '{y}-{m}-{d}') }}
            </el-descriptions-item>
            <el-descriptions-item label="甲方">{{ contractInfo.partyA }}</el-descriptions-item>
            <el-descriptions-item label="乙方">{{ contractInfo.partyB }}</el-descriptions-item>
            <el-descriptions-item label="甲方联系人">{{ contractInfo.contactPersonA }}</el-descriptions-item>
            <el-descriptions-item label="甲方联系电话">{{ contractInfo.contactPhoneA }}</el-descriptions-item>
            <el-descriptions-item label="乙方联系人">{{ contractInfo.contactPersonB }}</el-descriptions-item>
            <el-descriptions-item label="乙方联系电话">{{ contractInfo.contactPhoneB }}</el-descriptions-item>
            <el-descriptions-item label="主要内容" :span="2">
              <div class="content-text">{{ contractInfo.mainContent || '-' }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">
              <div class="content-text">{{ contractInfo.remark || '-' }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 附件管理 -->
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>附件管理</span>
            </div>
          </template>
          
          <!-- 文件上传区域 -->
          <div class="upload-section" style="margin-bottom: 20px;">
            <el-upload
              ref="uploadRef"
              action="/prod-api/contract/attachment/upload"
              :headers="uploadHeaders"
              :data="getUploadData"
              :file-list="fileList"
              :before-upload="beforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :on-change="handleFileChange"
              :auto-upload="false"
              multiple
              drag
              accept=".doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg,.gif,.txt"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持jpg/png/gif/pdf/doc/docx/xls/xlsx/txt格式，单个文件不超过10MB
                </div>
              </template>
            </el-upload>
            
            <div style="margin-top: 10px;">
              <el-button 
                type="primary" 
                @click="submitUpload"
                :loading="uploading"
                v-hasPermi="['gcxm:contract:upload']"
              >
                确认上传
              </el-button>
            </div>
          </div>
          
          <div class="attachment-list" v-loading="attachmentLoading">
            <div v-if="!attachmentList || attachmentList.length === 0" class="no-data">
              暂无附件
            </div>
            <div v-else>
              <div 
                v-for="item in attachmentList" 
                :key="item.id" 
                class="attachment-item"
              >
                <div class="file-info">
                  <el-icon class="file-icon" :class="getFileIconClass(item.fileExtension)">
                    <Document />
                  </el-icon>
                  <div class="file-details">
                    <div class="file-name" :title="item.fileOriginalName">
                      {{ item.fileOriginalName }}
                    </div>
                    <div class="file-meta">
                      <span class="file-category">
                        <dict-tag :options="file_category" :value="item.fileCategory"/>
                      </span>
                      <span class="file-size">{{ formatFileSize(item.fileSize) }}</span>
                      <span class="upload-time">{{ parseTime(item.uploadTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                    </div>
                  </div>
                </div>
                <div class="file-actions">
                  <el-button-group>
                    <el-button 
                      size="small" 
                      type="primary" 
                      link 
                      @click="handlePreview(item)"
                      v-hasPermi="['gcxm:contract:preview']"
                    >
                      <el-icon><View /></el-icon>
                    </el-button>
                    <el-button 
                      size="small" 
                      type="success" 
                      link 
                      @click="handleDownload(item)"
                      v-hasPermi="['gcxm:contract:download']"
                    >
                      <el-icon><Download /></el-icon>
                    </el-button>
                    <el-button 
                      size="small" 
                      type="danger" 
                      link 
                      @click="handleDeleteAttachment(item)"
                      v-hasPermi="['gcxm:contract:remove']"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </el-button-group>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 文件预览对话框 -->
    <el-dialog 
      title="文件预览" 
      v-model="previewVisible" 
      width="80%" 
      append-to-body
      :before-close="handleClosePreview"
    >
      <div class="preview-container" v-loading="previewLoading">
        <!-- 图片预览 -->
        <div v-if="previewType === 'image'" class="image-preview">
          <img :src="previewUrl" alt="预览图片" style="max-width: 100%; max-height: 600px;" />
        </div>
        <!-- PDF预览 -->
        <div v-else-if="previewType === 'pdf'" class="pdf-preview">
          <iframe :src="previewUrl" width="100%" height="600px" frameborder="0"></iframe>
        </div>
        <!-- 其他文件类型 -->
        <div v-else class="other-preview">
          <div class="no-preview">
            <el-icon size="64"><Document /></el-icon>
            <p>此文件类型不支持在线预览</p>
            <el-button type="primary" @click="handleDownloadFromPreview">下载文件</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, onMounted } from 'vue';
import { getContract, listAttachmentByContractId, delAttachment, downloadAttachment, previewAttachment, uploadAttachment } from "@/api/gcxm/contract";
import { getToken } from "@/utils/auth";

const props = defineProps({
  contractId: {
    type: [String, Number],
    required: true
  }
});

const emit = defineEmits(['close', 'edit']);

const { proxy } = getCurrentInstance();
const { contract_status, file_category } = proxy.useDict('contract_status', 'file_category');

const contractInfo = ref({});
const attachmentList = ref([]);
const attachmentLoading = ref(false);
const previewVisible = ref(false);
const previewLoading = ref(false);
const previewUrl = ref('');
const previewType = ref('');
const currentPreviewFile = ref(null);

// 简单上传
const uploading = ref(false);
const fileList = ref([]);
const uploadRef = ref(null);

/** 获取合同详情 */
function getContractDetail() {
  getContract(props.contractId).then(response => {
    contractInfo.value = response.data;
  });
}

/** 获取附件列表 */
function getAttachmentList() {
  attachmentLoading.value = true;
  listAttachmentByContractId(props.contractId).then(response => {
    attachmentList.value = response.data || [];
    attachmentLoading.value = false;
  }).catch(() => {
    attachmentList.value = [];
    attachmentLoading.value = false;
  });
}

/** 编辑合同 */
function handleEdit() {
  emit('edit', contractInfo.value);
}

// Upload相关的计算属性和方法
const uploadHeaders = computed(() => {
  return {
    'Authorization': 'Bearer ' + getToken()
  };
});

const getUploadData = computed(() => {
  return {
    contractId: props.contractId,
    fileCategory: 'ATTACHMENT',
    isMain: '0'
  };
});

// 文件列表同步函数 - 关键修复
const handleFileChange = (uploadFile, uploadFiles) => {
  console.log('文件列表已改变，更新fileList变量');
  fileList.value = uploadFiles;
};

const beforeUpload = (file) => {
  // 文件大小检查
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    proxy.$modal.msgError('上传文件大小不能超过 10MB!');
    return false;
  }
  return true;
};

const handleUploadSuccess = (response, file) => {
  console.log('上传成功:', response);
  proxy.$modal.msgSuccess('上传成功');
  // 从文件列表中移除已成功上传的文件
  const index = fileList.value.findIndex(item => item.uid === file.uid);
  if (index > -1) {
    fileList.value.splice(index, 1);
  }
  getAttachmentList(); // 刷新附件列表
  uploading.value = false;
};

const handleUploadError = (error, file) => {
  console.error('上传失败:', error);
  proxy.$modal.msgError('上传失败: ' + error.message);
  uploading.value = false;
};

const handleRemove = (file) => {
  console.log('移除文件:', file.name);
  const index = fileList.value.findIndex(item => item.uid === file.uid);
  if (index > -1) {
    fileList.value.splice(index, 1);
  }
};

const submitUpload = () => {
  if (fileList.value.length === 0) {
    proxy.$modal.msgWarning('请选择要上传的文件');
    return;
  }
  
  if (!props.contractId) {
    proxy.$modal.msgError('合同ID无效，无法上传附件');
    return;
  }
  
  uploading.value = true;
  console.log('开始批量上传，文件数量:', fileList.value.length);
  uploadRef.value.submit();
};

/** 预览文件 */
function handlePreview(item) {
  previewVisible.value = true;
  previewLoading.value = true;
  currentPreviewFile.value = item;
  
  // 根据文件类型确定预览方式
  const ext = (item.fileExtension || '').toLowerCase();
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext)) {
    previewType.value = 'image';
  } else if (ext === 'pdf') {
    previewType.value = 'pdf';
  } else {
    previewType.value = 'other';
  }
  
  if (previewType.value !== 'other') {
    // 构建预览URL，添加时间戳避免缓存
    const timestamp = new Date().getTime();
    previewUrl.value = '/prod-api/contract/attachment/preview/' + item.id + '?t=' + timestamp;
  }
  
  previewLoading.value = false;
}

/** 关闭预览 */
function handleClosePreview() {
  previewVisible.value = false;
  previewUrl.value = '';
  previewType.value = '';
  currentPreviewFile.value = null;
}

/** 从预览中下载 */
function handleDownloadFromPreview() {
  if (currentPreviewFile.value) {
    handleDownload(currentPreviewFile.value);
  }
}

/** 下载文件 */
function handleDownload(item) {
  proxy.$modal.loading("正在下载数据，请稍候！");
  downloadAttachment(item.id).then(response => {
    const blob = new Blob([response], { type: 'application/octet-stream' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = item.fileOriginalName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
    proxy.$modal.closeLoading();
  }).catch(() => {
    proxy.$modal.closeLoading();
    proxy.$modal.msgError('下载失败');
  });
}

/** 删除附件 */
function handleDeleteAttachment(item) {
  proxy.$modal.confirm('是否确认删除附件"' + item.fileOriginalName + '"？').then(function() {
    return delAttachment(item.id);
  }).then(() => {
    getAttachmentList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 获取文件图标类名 */
function getFileIconClass(extension) {
  const ext = extension?.toLowerCase();
  const iconMap = {
    pdf: 'pdf-icon',
    doc: 'word-icon',
    docx: 'word-icon',
    xls: 'excel-icon',
    xlsx: 'excel-icon',
    ppt: 'ppt-icon',
    pptx: 'ppt-icon',
    jpg: 'image-icon',
    jpeg: 'image-icon',
    png: 'image-icon',
    gif: 'image-icon',
    zip: 'zip-icon',
    rar: 'zip-icon',
    '7z': 'zip-icon'
  };
  return iconMap[ext] || 'file-icon';
}

/** 格式化文件大小 */
function formatFileSize(bytes) {
  if (!bytes) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
}

/** 格式化金额 */
function formatMoney(money) {
  if (!money) return '';
  return '¥' + Number(money).toLocaleString();
}

onMounted(() => {
  getContractDetail();
  getAttachmentList();
});
</script>

<style scoped>
.contract-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-text {
  max-height: 100px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.attachment-list {
  max-height: 400px;
  overflow-y: auto;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.attachment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.attachment-item:last-child {
  border-bottom: none;
}

.file-info {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
}

.file-icon {
  font-size: 24px;
  margin-right: 12px;
  color: #409eff;
}

.file-details {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.file-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #909399;
  flex-wrap: wrap;
}

.file-actions {
  margin-left: 12px;
}

.upload-demo {
  display: inline-block;
}

.upload-section {
  display: inline-block;
}

.selected-files {
  margin-top: 8px;
  max-width: 200px;
}

.selected-file {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 8px;
  margin: 2px 0;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

.preview-container {
  text-align: center;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
}

.no-preview p {
  margin: 16px 0;
  font-size: 16px;
}

/* 文件图标颜色 */
.pdf-icon {
  color: #f56c6c;
}

.word-icon {
  color: #2b5797;
}

.excel-icon {
  color: #207245;
}

.ppt-icon {
  color: #d04423;
}

.image-icon {
  color: #67c23a;
}

.zip-icon {
  color: #e6a23c;
}

.file-icon {
  color: #909399;
}
</style>

<template>
  <div class="contract-detail">
    <!-- 合同基本信息 -->
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>合同详情</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button @click="handleClose">关闭</el-button>
          </div>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ contractInfo.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同名称">{{ contractInfo.contractName }}</el-descriptions-item>
        <el-descriptions-item label="合同金额">
          <el-tag type="success">¥{{ formatMoney(contractInfo.contractAmount) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签署日期">{{ contractInfo.signingDate }}</el-descriptions-item>
        <el-descriptions-item label="甲方">{{ contractInfo.partyA }}</el-descriptions-item>
        <el-descriptions-item label="乙方">{{ contractInfo.partyB }}</el-descriptions-item>
        <el-descriptions-item label="合同状态">
          <dict-tag :options="contract_status" :value="contractInfo.contractStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ contractInfo.createTime }}</el-descriptions-item>
        <el-descriptions-item label="主要内容" :span="2">
          <div class="contract-content">{{ contractInfo.mainContent }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 附件管理 -->
    <el-card class="box-card attachment-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>合同附件</span>
          <el-button type="primary" @click="showUploadDialog">上传附件</el-button>
        </div>
      </template>

      <div v-loading="attachmentLoading">
        <el-table :data="attachmentList" style="width: 100%">
          <el-table-column prop="fileOriginalName" label="文件名" min-width="200">
            <template #default="scope">
              <div class="file-info">
                <el-icon class="file-icon">
                  <Document v-if="scope.row.fileExtension === 'pdf'" />
                  <Picture v-else-if="['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(scope.row.fileExtension)" />
                  <Folder v-else />
                </el-icon>
                <span class="file-name">{{ scope.row.fileOriginalName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="fileCategory" label="文件分类" width="120">
            <template #default="scope">
              <dict-tag :options="file_category" :value="scope.row.fileCategory"/>
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="文件大小" width="120">
            <template #default="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="uploadUser" label="上传人" width="120"/>
          <el-table-column prop="uploadTime" label="上传时间" width="180"/>
          <el-table-column prop="isMain" label="主要文件" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.isMain === '1' ? 'success' : 'info'">
                {{ scope.row.isMain === '1' ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="handlePreview(scope.row)">预览</el-button>
              <el-button link type="success" @click="handleDownload(scope.row)">下载</el-button>
              <el-button link type="danger" @click="handleDeleteAttachment(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div v-if="!attachmentList.length" class="empty-data">
          <el-empty description="暂无附件" />
        </div>
      </div>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传附件" width="600px" @close="resetUploadForm">
      <el-form ref="uploadFormRef" :model="uploadForm" :rules="uploadRules" label-width="100px">
        <el-form-item label="文件分类" prop="fileCategory">
          <el-select v-model="uploadForm.fileCategory" placeholder="请选择文件分类" style="width: 100%">
            <el-option label="合同文件" value="CONTRACT" />
            <el-option label="附件" value="ATTACHMENT" />
            <el-option label="图片" value="IMAGE" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否主要文件" prop="isMain">
          <el-radio-group v-model="uploadForm.isMain">
            <el-radio value="1">是</el-radio>
            <el-radio value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择文件" prop="file">
          <el-upload
            ref="uploadRef"
            :action="uploadUrl"
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
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持jpg/png/gif/pdf/doc/docx/xls/xlsx格式，且不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">确认上传</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog 
      v-model="previewVisible" 
      title="文件预览" 
      width="80%" 
      append-to-body
      @close="handleClosePreview"
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
            <p>文件名：{{ currentPreviewFile?.fileOriginalName }}</p>
            <el-button type="primary" @click="handleDownloadFromPreview">下载文件</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Picture, Folder, UploadFilled } from '@element-plus/icons-vue'
import { getContract, listAttachmentByContractId, delAttachment, downloadAttachment, previewAttachment } from "@/api/gcxm/contract"
import { getToken } from "@/utils/auth"

const props = defineProps({
  contractId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['close', 'edit'])

const { proxy } = getCurrentInstance()
const { contract_status, file_category } = proxy.useDict('contract_status', 'file_category')

// 响应式数据
const contractInfo = ref({})
const attachmentList = ref([])
const attachmentLoading = ref(false)
const previewVisible = ref(false)
const previewLoading = ref(false)
const previewUrl = ref('')
const previewType = ref('')
const currentPreviewFile = ref(null)

// 上传相关
const uploadDialogVisible = ref(false)
const uploadFormRef = ref()
const uploadRef = ref()
const fileList = ref([])

// 新增此函数，用于同步el-upload组件的文件列表 - 关键修复
const handleFileChange = (uploadFile, uploadFiles) => {
  console.log('文件列表已改变，更新fileList变量');
  fileList.value = uploadFiles;
}

const uploadForm = reactive({
  fileCategory: 'ATTACHMENT',
  isMain: '0'
})

const uploadRules = {
  fileCategory: [
    { required: true, message: '请选择文件分类', trigger: 'change' }
  ]
}

const uploadUrl = import.meta.env.VITE_APP_BASE_API + '/contract/attachment/upload'
const uploadHeaders = { Authorization: 'Bearer ' + getToken() }

// 动态获取上传数据
const getUploadData = () => ({
  contractId: props.contractId,
  fileCategory: uploadForm.fileCategory,
  isMain: uploadForm.isMain
})

// 获取合同详情
const getContractDetail = () => {
  getContract(props.contractId).then(response => {
    contractInfo.value = response.data
  })
}

// 获取附件列表
const getAttachmentList = () => {
  attachmentLoading.value = true
  listAttachmentByContractId(props.contractId).then(response => {
    attachmentList.value = response.data || []
  }).catch(() => {
    attachmentList.value = []
  }).finally(() => {
    attachmentLoading.value = false
  })
}

// 显示上传对话框
const showUploadDialog = () => {
  uploadDialogVisible.value = true
}

// 重置上传表单
const resetUploadForm = () => {
  uploadFormRef.value?.resetFields()
  fileList.value = []
}

// 上传前检查
const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB!')
    return false
  }
  return true
}

// 提交上传
const submitUpload = () => {
  uploadFormRef.value.validate((valid) => {
    if (valid) {
      if (fileList.value.length === 0) {
        ElMessage.warning('请选择要上传的文件')
        return
      }
      // 开始上传
      uploadRef.value.submit()
    }
  })
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  console.log('上传成功响应:', response, '文件:', file)
  if (response.code === 200) {
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    resetUploadForm()
    // 刷新附件列表
    getAttachmentList()
  } else {
    console.error('上传失败响应:', response)
    ElMessage.error(response.msg || '上传失败')
  }
}

// 上传失败
const handleUploadError = (err, file) => {
  console.error('上传失败:', err, '文件:', file)
  ElMessage.error('上传失败: ' + (err.message || '网络错误'))
}

// 移除文件
const handleRemove = (file, fileList) => {
  console.log('移除文件:', file)
}

// 预览文件
const handlePreview = (item) => {
  previewVisible.value = true
  previewLoading.value = true
  currentPreviewFile.value = item
  
  // 根据文件类型确定预览方式
  const ext = (item.fileExtension || '').toLowerCase()
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext)) {
    previewType.value = 'image'
  } else if (ext === 'pdf') {
    previewType.value = 'pdf'
  } else {
    previewType.value = 'other'
  }
  
  if (previewType.value !== 'other') {
    // 使用blob URL来预览文件
    previewAttachment(item.id).then(response => {
      // 【重要修改】: 直接使用返回的Blob，不再重新包装
      const blob = response;
      previewUrl.value = window.URL.createObjectURL(blob)
      console.log('预览URL:', previewUrl.value, '文件类型:', blob.type)
      previewLoading.value = false
    }).catch(error => {
      console.error('预览失败:', error)
      previewType.value = 'other'
      previewLoading.value = false
      ElMessage.error('预览失败')
    })
  } else {
    previewLoading.value = false
  }
}

// 关闭预览
const handleClosePreview = () => {
  // 清理blob URL，防止内存泄漏
  if (previewUrl.value && previewUrl.value.startsWith('blob:')) {
    window.URL.revokeObjectURL(previewUrl.value)
  }
  
  previewVisible.value = false
  previewUrl.value = ''
  previewType.value = ''
  currentPreviewFile.value = null
}

// 从预览中下载
const handleDownloadFromPreview = () => {
  if (currentPreviewFile.value) {
    handleDownload(currentPreviewFile.value)
  }
}

// 下载文件
const handleDownload = (item) => {
  const loading = ElMessage({
    message: '正在下载，请稍候...',
    type: 'info',
    duration: 0
  })
  
  downloadAttachment(item.id).then(response => {
    // 【重要修改】: 当 API 返回的 response 本身就是 Blob 时，直接使用它
    const blob = response; 
    
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = item.fileOriginalName || 'download';
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
    loading.close();
    ElMessage.success('下载成功');
  }).catch((error) => {
    loading.close();
    console.error('下载失败:', error);
    ElMessage.error('下载失败: ' + (error.message || '网络错误'));
  })
}

// 删除附件
const handleDeleteAttachment = (item) => {
  ElMessageBox.confirm('是否确认删除附件"' + item.fileOriginalName + '"？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delAttachment(item.id)
  }).then(() => {
    ElMessage.success('删除成功')
    getAttachmentList()
  }).catch(() => {
    // 用户取消操作
  })
}

// 编辑合同
const handleEdit = () => {
  emit('edit', contractInfo.value)
}

// 关闭详情
const handleClose = () => {
  emit('close')
}

// 格式化金额
const formatMoney = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let unitIndex = 0
  let fileSize = size
  
  while (fileSize >= 1024 && unitIndex < units.length - 1) {
    fileSize /= 1024
    unitIndex++
  }
  
  return `${fileSize.toFixed(2)} ${units[unitIndex]}`
}

// 初始化
onMounted(() => {
  getContractDetail()
  getAttachmentList()
})
</script>

<style scoped>
.contract-detail {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.contract-content {
  max-height: 200px;
  overflow-y: auto;
  line-height: 1.6;
  color: #666;
}

.attachment-card {
  min-height: 400px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  font-size: 18px;
  color: #409eff;
}

.file-name {
  word-break: break-all;
}

.empty-data {
  text-align: center;
  padding: 40px 0;
}

.preview-container {
  text-align: center;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 600px;
  object-fit: contain;
}

.pdf-preview iframe {
  border: none;
  border-radius: 4px;
}

.no-preview {
  text-align: center;
  color: #666;
}

.no-preview p {
  margin: 10px 0;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style>

import request from '@/utils/request'

// 查询员工列表
export function listHrman(query) {
  return request({
    url: '/prod-api/hrman/list',
    method: 'get',
    params: query
  })
}

// 查询员工详细信息
export function getHrman(id) {
  return request({
    url: '/prod-api/hrman/' + id,
    method: 'get'
  })
}

// 新增员工
export function addHrman(data) {
  return request({
    url: '/prod-api/hrman',
    method: 'post',
    data: data
  })
}

// 修改员工信息
export function updateHrman(data) {
  return request({
    url: '/prod-api/hrman',
    method: 'put',
    data: data
  })
}

// 删除员工
export function delHrman(ids) {
  return request({
    url: '/prod-api/hrman',
    method: 'delete',
    data: ids
  })
}

// 导入员工数据
export function importData(data) {
  return request({
    url: '/prod-api/hrman/importData',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载导入模板
export function importTemplate() {
  return request({
    url: '/prod-api/hrman/template',
    method: 'get',
    responseType: 'blob'
  })
}

// 导出员工数据
export function input() {
  return request({
    url: '/prod-api/hrman/export',
    method: 'post',
    responseType: 'blob'
  })
}

// 获取即将到期合同的员工
export function getExpiringContracts() {
  return request({
    url: '/prod-api/hrman/expiring-contracts',
    method: 'get'
  })
}

// 文件相关接口
// 上传文件
// export function uploadFile(employeeId, fileType, file) {
//   const formData = new FormData()
//   formData.append('file', file)
//   formData.append('fileType', fileType)
//   return request({
//     url: `/prod-api/hrman/${employeeId}/files`,
//     method: 'post',
//     data: formData,
//     headers: {
//       'Content-Type': 'multipart/form-data'
//     }
//   })
// }
export function uploadFile(employeeId, fileType, file) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('fileType', fileType)
  return request({
    url: `/hrman/${employeeId}/files`, // 使用相对路径
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取员工文件列表
export function getEmployeeFiles(employeeId) {
  return request({
    url: `/prod-api/hrman/${employeeId}/files`,
    method: 'get'
  })
}

// 删除文件
export function deleteFile(fileId) {
  return request({
    url: `/prod-api/hrman/files/${fileId}`,
    method: 'delete'
  })
}

// 获取文件内容（预览/下载）
export function getFileContent(fileId, download = false) {
  return request({
    url: `/prod-api/hrman/files/${fileId}`,
    method: 'get',
    params: { download },
    responseType: 'blob'
  })
}
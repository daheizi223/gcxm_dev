import request from '@/utils/request'

// 查询合同信息列表
export function listContract(query) {
  return request({
    url: '/prod-api/contract/list',
    method: 'get',
    params: query
  })
}

// 查询合同信息详细
export function getContract(id) {
  return request({
    url: '/prod-api/contract/' + id,
    method: 'get'
  })
}

// 新增合同信息
export function addContract(data) {
  return request({
    url: '/prod-api/contract',
    method: 'post',
    data: data
  })
}

// 修改合同信息
export function updateContract(data) {
  return request({
    url: '/prod-api/contract',
    method: 'put',
    data: data
  })
}

// 删除合同信息
export function delContract(id) {
  return request({
    url: '/prod-api/contract/' + id,
    method: 'delete'
  })
}

// 生成合同编号
export function generateContractNo() {
  return request({
    url: '/prod-api/contract/generateContractNo',
    method: 'get'
  })
}

// 检查合同编号唯一性
export function checkContractNoUnique(contractNo, id) {
  return request({
    url: '/prod-api/contract/checkContractNoUnique',
    method: 'get',
    params: {
      contractNo,
      id
    }
  })
}

// 统计合同数量按状态分组
export function getContractCountByStatus() {
  return request({
    url: '/prod-api/contract/countByStatus',
    method: 'get'
  })
}

// 查询合同附件列表
export function listAttachment(query) {
  return request({
    url: '/prod-api/contract/attachment/list',
    method: 'get',
    params: query
  })
}

// 根据合同ID查询附件列表
export function listAttachmentByContractId(contractId) {
  return request({
    url: '/prod-api/contract/attachment/listByContractId/' + contractId,
    method: 'get'
  })
}

// 查询合同附件详细
export function getAttachment(id) {
  return request({
    url: '/prod-api/contract/attachment/' + id,
    method: 'get'
  })
}

// 上传附件
export function uploadAttachment(data) {
  return request({
    url: '/prod-api/contract/attachment/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 修改合同附件
export function updateAttachment(data) {
  return request({
    url: '/prod-api/contract/attachment',
    method: 'put',
    data: data
  })
}

// 删除合同附件
export function delAttachment(id) {
  return request({
    url: '/prod-api/contract/attachment/' + id,
    method: 'delete'
  })
}

// 下载附件
export function downloadAttachment(id) {
  return request({
    url: '/prod-api/contract/attachment/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}

// 预览附件
export function previewAttachment(id) {
  return request({
    url: '/prod-api/contract/attachment/preview/' + id,
    method: 'get',
    responseType: 'blob'
  })
}

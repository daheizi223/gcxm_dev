import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listProjectexpenditure(query) {
  return request({
    url: '/prod-api/Projectexpenditure/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getProjectexpenditure(id) {
  return request({
    url: '/prod-api/Projectexpenditure/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addProjectexpenditure(data) {
  return request({
    url: '/prod-api/Projectexpenditure',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateProjectexpenditure(data) {
  return request({
    url: '/prod-api/Projectexpenditure',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
// export function delHrman(id) {
//   return request({
//     url: '/hrman/' + id,
//     method: 'delete'
//   })
// }

export function delProjectexpenditure(ids) {
  // 确保传递的 id 是 Long 类型（JavaScript 中是数字）
 // const longIds = ids.map(id => Number(id));  // 将 id 转换为数字
  return request({
    url: '/prod-api/Projectexpenditure',  // 使用正确的 API
    method: 'delete',
    data: ids  // 传递数字数组
  });
}


export function importData(data) {
  return request({
    url: '/prod-api/Projectexpenditure/importData',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function importTemplate() {
  return request({
    url: '/prod-api/Projectexpenditure/template',
    method: 'get',
    responseType: 'blob',
  })
}

  export function input() {
    return request({
      url: '/prod-api/Projectexpenditure/export',
      method: 'post',
      responseType: 'blob',
    })
}

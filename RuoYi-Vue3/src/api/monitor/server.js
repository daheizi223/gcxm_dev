import request from '@/utils/request'

// 获取服务信息
export function getServer() {
  return request({
    url: '/prod-api/monitor/server',
    method: 'get'
  })
}
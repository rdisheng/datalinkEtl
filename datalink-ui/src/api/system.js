import request from '@/utils/request'

const systemApi = {
    systemInfo: '/api/system/info',
    systemStatistics: '/api/system/statistics',
}

export function getSystemInfo() {
    return request({
        url: systemApi.systemInfo,
        method: 'get'
    })
}

export function getSystemStatistics() {
  return request({
    url: systemApi.systemStatistics,
    method: 'get'
  })
}



import { asyncRouterMap, constantRouterMap } from '@/config/router.config'
import { deepCopy } from '@/components/_util/util'

/**
 * 过滤账户是否拥有某一个权限，并将菜单从加载列表移除
 *
 * @param permissions
 * @param route
 * @returns {boolean}
 */
function hasPermission(permissions, route) {
  if (permissions.includes('all')) return true
  if (route.meta && route.meta.permission) {
    for (let i = 0, len = permissions.length; i < len; i++) {
      if (route.meta.permission === permissions[i]) {
        return true
      }
    }
    return false
  }
  return true
}

function filterAsyncRouter(routerMap, permissions) {
  return routerMap.filter(route => {
    if (hasPermission(permissions, route)) {
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children, permissions)
      }
      return true
    }
    return false
  })
}

const permission = {
  state: {
    routers: constantRouterMap,
    addRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers)
    }
  },
  actions: {
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {
        const accessedRouters = filterAsyncRouter(deepCopy(asyncRouterMap), data.data.permissions)
        commit('SET_ROUTERS', accessedRouters)
        resolve()
      })
    }
  }
}

export default permission

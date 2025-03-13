// eslint-disable-next-line
import { BasicLayout, UserLayout } from '@/layouts'
import { backup, home, plugin, driver,resource, rule, script, user, variable,system } from '@/core/icons'

const RouteView = {
  name: 'RouteView',
  render: h => h('router-view')
}

export const asyncRouterMap = [
  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: '服务监控' },
    redirect: '/dashboard/monitor',
    children: [
      {
        path: '/dashboard/monitor',
        name: 'monitor',
        component: () => import('@/views/dashboard/Monitor'),
        meta: { title: '服务监控', keepAlive: false, icon: home, permission: 'dashboard' }
      },
      {
        path: '/rule/list',
        name: 'ruleList',
        component: () => import('@/views/rule/RuleList'),
        meta: { title: '流程规则', keepAlive: true, icon: rule, permission: 'rule' }
      },
      {
        path: '/rule/info/:ruleId',
        name: 'ruleInfo',
        component: () => import('@/views/rule/RuleModel'),
        props: true,
        hidden: true
      },
      {
        path: '/resource',
        name: 'resource',
        component: RouteView,
        meta: {
          title: '资源管理',
          keepAlive: true,
          icon: resource,
          permission: 'resource',
        },
        children: [
          {
            path: '/resource/list',
            name: 'resourceList',
            component: () => import('@/views/resource/ResourceList'),
            meta: { title: '资源', keepAlive: true, icon: driver, permission: 'resource' }
          },
          {
            path: '/script/list',
            name: 'scriptList',
            component: () => import('@/views/script/ScriptList'),
            meta: { title: '脚本', keepAlive: true, icon: script, permission: 'resource' }
          },
          {
            path: '/script/info/:scriptId',
            name: 'scriptInfo',
            component: () => import('@/views/script/modules/ScriptModel'),
            props: true,
            hidden: true
          },
          {
            path: '/plugin/list',
            name: 'pluginList',
            component: () => import('@/views/plugin/PluginList'),
            meta: { title: '插件', keepAlive: true, icon: plugin, permission: 'resource' }
          },
          {
            path: '/variable/list',
            name: 'variableList',
            component: () => import('@/views/variable/VariableList'),
            meta: { title: '变量', keepAlive: true, icon: variable, permission: 'resource' }
          }
        ]
      },
      {
        path: '/system',
        name: 'system',
        component: RouteView,
        meta: { title: '系统管理', keepAlive: true, icon: system, permission: 'system' },
        children: [
          {
            path: '/user/list',
            name: 'userList',
            component: () => import('@/views/user/UserList'),
            meta: { title: '用户管理', keepAlive: true, icon: user, permission: 'system' }
          },
          {
            path: '/backup/list',
            name: 'backupList',
            component: () => import('@/views/backup/BackupList'),
            meta: { title: '数据备份', keepAlive: true, icon: backup, permission: 'system' }
          }
        ]
      }

    ]
  },
  {
    path: '*',
    redirect: '/404',
    hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
      /*  {
          path: 'register',
          name: 'register',
          component: () => import(/!* webpackChunkName: "user" *!/ '@/views/user/Register')
        },*/
      /* {
         path: 'register-result',
         name: 'registerResult',
         component: () => import(/!* webpackChunkName: "user" *!/ '@/views/user/RegisterResult')
       },*/
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }
]

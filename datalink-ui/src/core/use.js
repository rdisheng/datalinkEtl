import Vue from 'vue'

// base library
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/antd.less'

// ext library
import PermissionHelper from '@/core/permission/permission'
import './directives/action'

Vue.use(Antd)
Vue.use(PermissionHelper)

process.env.NODE_ENV !== 'production' && console.warn('[antd-pro] WARNING: Antd now use fulled imported.')

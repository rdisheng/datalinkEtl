<template>
  <div :class='wrpCls'>

    <a-tooltip placement='left'>
      <template slot='title'>服务本地时间</template>
      <span class='system-info-item'><span>时间：</span>{{ systemInfo.time }}</span>
    </a-tooltip>

    <a-tooltip placement='left'>
      <template slot='title'>当前服务节点</template>
      <span class='system-info-item'><span>节点：</span>{{ systemInfo.localMemberName }}</span>
    </a-tooltip>

    <a-tooltip placement='left'>
      <template slot='title'>服务版本</template>
      <span class='system-info-item' style='padding-right: 12px'><span>版本：</span>{{ systemInfo.version }}</span>
    </a-tooltip>

    <avatar-dropdown :menu='showMenu' :current-user='currentUser' :class='prefixCls' @updatePassword='updatePassword' />
    <password-model ref='PasswordModel'></password-model>
  </div>
</template>

<script>
import AvatarDropdown from './AvatarDropdown'
import PasswordModel from '../../views/user/modules/PasswordModel'
import { getSystemInfo } from '@/api/system'

export default {
  name: 'RightContent',
  components: {
    AvatarDropdown,
    PasswordModel
  },
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-global-header-index-action'
    },
    isMobile: {
      type: Boolean,
      default: () => false
    },
    topMenu: {
      type: Boolean,
      required: true
    },
    theme: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      showMenu: true,
      currentUser: {
        username: '-'
      },
      systemInfo: {
        ip: '-',
        time: '-',
        version: '-',
        username: '-'
      },
      timer: {}
    }
  },
  computed: {
    wrpCls() {
      return {
        'ant-pro-global-header-index-right': true,
        [`ant-pro-global-header-index-${this.isMobile || !this.topMenu ? 'light' : this.theme}`]: true
      }
    }
  },
  mounted() {
    this.getInfo()
    if (this.timer) {
      clearInterval(this.timer)
    }
    this.timer = setInterval(() => {
      this.getInfo()
    }, 1000 * 30)
  },
  methods: {
    updatePassword() {
      this.$refs.PasswordModel.open(this.currentUser.username)
    },
    getInfo() {
      getSystemInfo().then((res) => {
        this.systemInfo = res.data
        this.currentUser.username = res.data.username
      })
    }
  }
}
</script>

<style>

.system-info-item {
  font-size: 14px;
  display: inline-block;
  vertical-align: middle;
  padding-right: 24px;
}

.system-info-item span {
  font-weight: bold
}

</style>

<template>
  <a-dropdown v-if='currentUser && currentUser.username' placement='bottomRight'>
    <span class='ant-pro-account-avatar'>
<!--        <a-icon type='user' style='font-size: 14px;margin-right:5px;display: inline-block;vertical-align: middle' />
        <span style='font-size: 15px;display: inline-block;vertical-align: middle'>{{ currentUser.name }}</span>-->
        <span style='font-size: 14px;display: inline-block;vertical-align: middle'><span style='font-weight: bold'>用户：</span>{{ currentUser.username }}</span>

    </span>
    <template v-slot:overlay>
      <a-menu class='ant-pro-drop-down menu' :selected-keys='[]'>
        <a-menu-item key='settings' @click='handleToPassword'>
          <a-icon type='setting' />
          修改密码
        </a-menu-item>
        <a-menu-divider />
        <a-menu-item key='logout' @click='handleLogout'>
          <a-icon type='logout' />
          退出登录
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
  <span v-else>
    <a-spin size='small' :style='{ marginLeft: 8, marginRight: 8 }' />
  </span>


</template>

<script>
import { Modal } from 'ant-design-vue'

export default {
  name: 'AvatarDropdown',
  props: {
    currentUser: {
      type: Object,
      default: () => null
    },
    menu: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    handleToCenter() {
      this.$router.push({ path: '/account/center' })
    },
    handleToPassword() {
      this.$emit('updatePassword')
    },
    handleLogout(e) {
      Modal.confirm({
        title: '退出登录',
        content: '确定要退出登录吗？',
        onOk: () => {
          // return new Promise((resolve, reject) => {
          //   setTimeout(Math.random() > 0.5 ? resolve : reject, 1500)
          // }).catch(() => console.log('Oops errors!'))
          return this.$store.dispatch('Logout').then(() => {
            this.$router.push({ name: 'login' })
          })
        },
        onCancel() {
        }
      })
    }
  }
}
</script>

<style lang='less' scoped>
.ant-pro-drop-down {
  /deep/ .action {
    margin-right: 8px;
  }

  /deep/ .ant-dropdown-menu-item {
    min-width: 160px;
  }
}
</style>

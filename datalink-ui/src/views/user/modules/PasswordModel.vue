<template>
  <a-modal
    title='修改密码'
    :width='600'
    :visible='visible'
    @cancel='onClose'
    :destroyOnClose='true'
  >

    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-row :gutter='24'>
        <a-col :span='24'>
          <a-form-model-item label='原密码' prop='oldPassword'>
            <a-input-password v-model='modal.oldPassword' placeholder='请输入原密码' />
          </a-form-model-item>
        </a-col>
        <a-col :span='24'>
          <a-form-model-item label='新密码' prop='newPassword'>
            <a-input-password v-model='modal.newPassword' placeholder='请输入新密码' />
          </a-form-model-item>
        </a-col>
        <a-col :span='24'>
          <a-form-model-item label='确认新密码' prop='newPassword2'>
            <a-input-password v-model='modal.newPassword2' placeholder='请再次输入新密码' />
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>

    <div class='bottom-button'>
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 关闭</a-button>
      <a-button type='primary' @click='updatePassword'> 确定</a-button>
    </div>
  </a-modal>
</template>

<script>
import { getAction } from '@/api/manage'

export default {
  name: 'PasswordModel',
  data() {
    return {
      visible: false,
      modal: {},
      rules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
        newPassword2: [{ required: true, message: '请再次输入新密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    open(username) {
      if (!username) return
      this.modal = { username: username }
      this.visible = true
    },
    onClose() {
      this.visible = false
    },
    updatePassword() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          if (that.modal.newPassword !== that.modal.newPassword2) {
            that.$message.error('两次新密码不一致')
            return
          }
          getAction('/api/user/password', that.modal).then(res => {
            if (res.code === 200) {
              that.$message.success('修改成功')
              that.visible = false
            } else {
              that.$message.error('修改失败')
            }
          })
        }
      })
    }

  }
}
</script>

<style scoped>
</style>

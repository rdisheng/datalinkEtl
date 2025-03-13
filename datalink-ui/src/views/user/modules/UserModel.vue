<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='用户'
    :width='600'
    :visible='visible'
    @cancel='onClose'
    :destroyOnClose='true'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-form-model-item label='用户名' prop='username'>
        <a-input v-model='modal.username' placeholder='请输入用户名' :disabled='isEdit'></a-input>
      </a-form-model-item>
      <a-form-model-item label='密码' prop='password' v-if='!isEdit'>
        <a-input-password v-model='modal.password' placeholder='请输入密码'></a-input-password>
      </a-form-model-item>
      <a-form-model-item label='备注'>
        <a-input v-model='modal.description' placeholder='请输入备注'></a-input>
      </a-form-model-item>
    </a-form-model>
    <div class='bottom-button'>
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 取消</a-button>
      <a-button type='primary' @click='handleOk'> 确定</a-button>
    </div>
  </a-modal>
</template>

<script>
import { postAction, putAction } from '@/api/manage'

export default {
  name: 'UserModel',
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        add: '/api/user/add',
        update: '/api/user/update'
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      isEdit: false
    }
  }
  ,

  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.modal = JSON.parse(JSON.stringify(record))
      this.isEdit = !!record.username
      this.visible = true
    }
    ,
    onClose() {
      this.visible = false
    }
    ,
    handleOk() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let obj
          if (this.isEdit) {
            obj = putAction(this.url.update, this.modal)
          } else {
            obj = postAction(this.url.add, this.modal)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.$emit('ok')
            } else {
              that.$message.error(res.message)
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.onClose()
            })

        } else {
          return false
        }
      })
    },
    filterOption(input, option) {
      if (!option.componentOptions.children[0].text) {
        return false
      }
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    }

  }
}
</script>

<style scoped>


</style>

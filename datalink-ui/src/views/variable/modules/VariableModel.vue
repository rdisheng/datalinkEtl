<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='全局变量'
    :width='600'
    :visible='visible'
    @cancel='onClose'
    :destroyOnClose='true'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-form-model-item label='变量名' prop='key'>
        <a-input v-model='modal.key' placeholder='请输入变量名' :disabled='isEdit'></a-input>
      </a-form-model-item>
      <a-form-model-item label='值' prop='value'>
        <a-input v-model='modal.value' placeholder='请输入变量值'></a-input>
      </a-form-model-item>
      <a-form-model-item label='说明'>
        <a-input v-model='modal.desc' placeholder='请输入说明'></a-input>
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
  name: 'VariableModel',
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        add: '/api/variable/add',
        update: '/api/variable/update'
      },
      rules: {
        key: [{ required: true, message: '请输入变量名', trigger: 'blur' }],
        value: [{ required: true, message: '请输入变量值', trigger: 'blur' }]
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
      this.isEdit = !!record.key
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

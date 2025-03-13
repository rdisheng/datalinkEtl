<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='新建资源'
    :width='600'
    :zIndex='1100'
    :maskClosable='false'
    :visible='visible'
    :destroyOnClose='true'
    @cancel='onClose'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-row :gutter='24'>
        <a-col :span='24'>
          <a-form-model-item label='资源名称' prop='resourceName'>
            <a-input v-model='modal.resourceName' placeholder='请输入资源名称' />
          </a-form-model-item>
        </a-col>
      </a-row>
      <component ref='PropertiesModal' v-if='resourceComponent' :is='resourceComponent'></component>
    </a-form-model>
    <template slot='footer'>
      <a-space>
        <a-button @click='testDriver' style='width: 75px'> 测试</a-button>
        <a-button @click='onClose' style='width: 75px'> 取消</a-button>
        <a-button @click='handleOk' type='primary' style='width: 75px'> 保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>

<script>
import { postAction } from '@/api/manage'
import { resourceComponentMap } from '@/config/resource.config'
import Vue from 'vue'
import { uuid } from '@/utils/util'

export default {
  name: 'NodeResourceModel',
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        add: '/api/resource/add',
        test: '/api/resource/test'
      },
      rules: {
        resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }]
      },
      resourceComponent: undefined,
      resourceComponentMap
    }
  },
  methods: {
    add(resourceType) {
      this.modal = { resourceType: resourceType, resourceName: resourceType.toLowerCase() + '_' + uuid(5) }
      this.importComponent(resourceType)
      this.visible = true
    },
    importComponent(resourceType) {
      import('../../resource/properties/' + this.resourceComponentMap[resourceType] + '.vue').then(component => {
        this.resourceComponent = Vue.extend(component.default)
      })
    },
    onClose() {
      this.modal = {}
      this.resourceComponent = undefined
      this.visible = false
    },
    handleOk() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (!valid) return false
        this.$refs.PropertiesModal.get((checked, prop) => {
          if (!checked) return false
          that.confirmLoading = true
          that.modal.properties = prop
          postAction(that.url.add, that.modal).then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.$emit('ok', res.data.resourceId)
            } else {
              that.$message.error('保存失败')
            }
          }).finally(() => {
            that.confirmLoading = false
            that.onClose()
          })
        })
      })
    },
    testDriver() {
      let that = this
      this.$refs.PropertiesModal.get((checked, prop) => {
        if (!checked) return false
        that.modal.properties = prop
        postAction(that.url.test, that.modal).then((res) => {
          if (res.code === 200 && res.data === true) {
            that.$message.success('资源可用')
          } else {
            that.$message.error('资源不可用')
          }
        })
      })
    }
  }
}
</script>

<style scoped>
</style>

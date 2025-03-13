<template>
  <a-drawer
    :confirmLoading='confirmLoading'
    title='资源'
    :width='720'
    :visible='visible'
    :destroyOnClose='true'
    :body-style="{ paddingBottom: '60px' }"
    @close='onClose'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-row :gutter='24'>
        <a-col :span='12'>
          <a-form-model-item label='资源类型' prop='resourceType'>
            <a-select v-model='modal.resourceType' placeholder='请选择资源类型' :disabled='!!modal.resourceId'
                      show-search @change='type => importComponent(type)'>
              <a-select-opt-group v-for='(group,groupIndex) in resourceTypeAllList' :key='groupIndex'
                                  :label='group.group'>
                <a-select-option v-for='(item,itemIndex) in group.list' :value='item.code' :key='itemIndex'>
                  {{ item.name }}
                </a-select-option>
              </a-select-opt-group>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='资源名称' prop='resourceName'>
            <a-input v-model='modal.resourceName' placeholder='请输入资源名称' />
          </a-form-model-item>
        </a-col>
      </a-row>
      <component ref='PropertiesModal' v-if='resourceComponent' :is='resourceComponent'></component>
      <a-form-model-item label='备注' prop='description'>
        <a-textarea v-model='modal.description' :rows='2' placeholder='请输入备注' />
      </a-form-model-item>
    </a-form-model>
    <div class='bottom-button'>
      <a-space>
        <a-button :style="{ width: '110px' }" @click='testDriver'> 测试</a-button>
        <a-button :style="{ width: '110px' }" @click='onClose'> 取消</a-button>
        <a-button :style="{ width: '110px' }" @click='handleOk' type='primary'> 保存</a-button>
      </a-space>
    </div>
  </a-drawer>
</template>

<script>
import { postAction, putAction } from '@/api/manage'
import { resourceComponentMap, resourceTypeAllList } from '@/config/resource.config'
import Vue from 'vue'
import { uuid } from '@/utils/util'

export default {
  name: 'ResourceModel',
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        add: '/api/resource/add',
        update: '/api/resource/update',
        test: '/api/resource/test'
      },
      rules: {
        resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
        resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }]
      },
      resourceComponent: undefined,
      resourceTypeAllList,
      resourceComponentMap
    }
  },
  methods: {
    add() {
      this.modal = { resourceName: 'resource_' + uuid(5) }
      this.resourceComponent = undefined
      this.visible = true
    },
    edit(record) {
      this.visible = true
      this.modal = Object.assign({}, record)
      this.importComponent(record.resourceType, record.properties)
    },
    importComponent(resourceType, properties) {
      import('../properties/' + this.resourceComponentMap[resourceType] + '.vue').then(component => {
        this.resourceComponent = Vue.extend(component.default)
        if (properties) {
          this.$nextTick(() => {
            this.$refs.PropertiesModal.set(properties)
          })
        }
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
          let obj
          if (that.modal.resourceId) {
            obj = putAction(that.url.update, that.modal)
          } else {
            obj = postAction(that.url.add, that.modal)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.$emit('ok')
            } else {
              that.$message.error('保存失败')
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.onClose()
            })
        })
      })
    },
    filterOption(input, option) {
      if (!option.componentOptions.children[0].text) {
        return false
      }
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
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

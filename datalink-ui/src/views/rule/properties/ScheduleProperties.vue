<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='12'>
        <a-form-model-item label='启动延迟' prop='initialDelay'>
          <a-input v-model='properties.initialDelay' placeholder='请输入启动延迟' style='width: 100%'>
            <a-select slot='addonAfter' v-model='properties.initialDelayUnit' placeholder='单位' style='width: 80px'>
              <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
              </a-select-option>
            </a-select>
          </a-input>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='Cron表达式' prop='cronExpression'>
          <easy-cron v-model='properties.cronExpression'></easy-cron>
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='数据格式' prop='payloadFormat'>
          <a-select v-model='properties.payloadFormat' placeholder='请选择数据格式'>
            <a-select-option value='json'>JSON对象</a-select-option>
            <a-select-option value='string'>字符串</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload'>
        <a-form-model-item label='数据内容'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor.vue'
import { timeUnitList } from '@/config/time.config'

export default {
  components: { MonacoEditor },
  data() {
    return {
      timeUnitList,
      properties: {
        initialDelayUnit: 'SECONDS',
        cronExpression: '0 0/1 * * * ? *',
        payloadFormat: 'json'
      },
      rules: {
        payloadFormat: [{ required: true, message: '请输入选择数据格式', trigger: 'change' }],
        initialDelay: [{ required: true, message: '请输入启动延迟', trigger: 'blur' }],
        initialDelayUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
        cronExpression: [{ required: true, message: '请输入Cron表达式', trigger: 'blur' }],
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.payload)
      })
    },
    get(callback) {
      this.properties.payload = this.$refs.MonacoEditor.get()
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    },
    checkEditor(rule, value, callback) {
      let content = this.$refs.MonacoEditor.get()
      if (!content) {
        return callback(new Error())
      } else {
        return callback()
      }
    }
  }
}
</script>

<style>


</style>

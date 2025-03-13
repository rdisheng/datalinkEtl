<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24' class='template'>
        <a-form-model-item label='数据模板' prop='template'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='输出格式' prop='outputFormat'>
          <a-select v-model='properties.outputFormat' placeholder='请选择输出格式'>
            <a-select-option value='json'>JSON对象</a-select-option>
            <a-select-option value='string'>字符串</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor.vue'

export default {
  components: { MonacoEditor },
  data() {
    return {
      properties: {
        outputFormat: 'json'
      },
      rules: {
        template: [{ required: true, validator: this.checkEditor, message: '请输入模板', trigger: 'blur' }],
        outputFormat: [{ required: true, message: '请输入选择输出格式', trigger: 'change' }]
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.template)
      })
    },
    get(callback) {
      this.properties.template = this.$refs.MonacoEditor.get()
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

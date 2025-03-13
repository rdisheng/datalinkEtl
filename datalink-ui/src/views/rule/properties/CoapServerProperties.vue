<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='12'>
        <a-form-model-item label='端口' prop='port'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='路径' prop='path'>
          <a-input v-model='properties.path' placeholder='请输入路径' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='报文格式' prop='payloadType'>
          <a-select v-model='properties.payloadType' placeholder='请选择报文格式'>
            <a-select-option value='text'>文本</a-select-option>
            <a-select-option value='hex'>十六进制</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='body'>
        <a-form-model-item label='响应体'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor'
import { postAction } from '@/api/manage'

export default {
  components: { MonacoEditor },
  data() {
    return {
      properties: {
        port:5683,
        payloadType:'text',
        response:''
      },
      rules: {
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        path: [{ required: true, message: '请输入路径', trigger: 'blur' }],
        payloadType: [{ required: true, message: '请选择报文格式', trigger: 'change' }]
      },
    }
  },
  props: {
    param: {
      type: Object,
      default: {}
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.MonacoEditor.set(this.properties.response)
    })
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.response)
      })
    },
    get(callback) {
      this.properties.response = this.$refs.MonacoEditor.get()
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    }
  }
}
</script>

<style>


</style>

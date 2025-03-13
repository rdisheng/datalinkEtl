<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='12'>
        <a-form-model-item label='IP' prop='ip'>
          <a-input v-model='properties.ip' placeholder='请输入IP' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='端口' prop='port'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='路径' prop='path'>
          <a-input v-model='properties.path' placeholder='请输入路径'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='请求方式' prop='method'>
          <a-select v-model='properties.method' placeholder='请选择请求方式' style='width: 100%'>
            <a-select-option v-for='(item,index) in methodList' :key='index' :value='item'>{{ item }}</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='报文格式' prop='payloadType'>
          <a-select v-model='properties.payloadType' placeholder='请选择报文格式'>
            <a-select-option value='text'>文本</a-select-option>
            <a-select-option value='hex'>十六进制</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='body'>
        <a-form-model-item label='请求体'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor'

export default {
  components: { MonacoEditor },
  data() {
    return {
      properties: {
        ip: 'localhost',
        port: 5683,
        payloadType: 'text',
        method: 'GET',
        payload: '',
      },
      methodList: ['GET', 'POST', 'PUT', 'DELETE', 'FETCH', 'PATCH', 'IPATCH'],
      rules: {
        ip: [{ required: true, message: '请输入IP', trigger: 'blur' }],
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        payloadType: [{ required: true, message: '请选择报文格式', trigger: 'change' }],
        path: [{ required: true, message: '请输入路径', trigger: 'blur' }],
        method: [{ required: true, message: '请选择请求方式', trigger: 'blur' }],
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
      this.$refs.MonacoEditor.set(this.properties.payload)
    })
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
    }
  }
}
</script>

<style scoped>

.ant-form-item-with-help {
  margin-bottom: 6px;
}

</style>

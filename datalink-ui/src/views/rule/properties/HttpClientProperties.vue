<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='请求URL' prop='url'>
          <a-input v-model='properties.url' placeholder='请输入请求URL' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='请求方式' prop='method'>
          <a-select v-model='properties.method' placeholder='请选择请求方式' style='width: 100%'>
            <a-select-option v-for='(item,index) in methodList' :key='index' :value='item'>{{ item }}</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接超时'>
          <a-input v-model='properties.connectTimeout' placeholder='请输入连接超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='读取超时'>
          <a-input v-model='properties.readTimeout' placeholder='请输入读取超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <http-headers-model ref='HttpHeadersModel' label='响应头'></http-headers-model>
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

import HttpHeadersModel from './model/HttpHeadersModel'
import MonacoEditor from '@/components/Editor/MonacoEditor'
import { postAction } from '@/api/manage'


export default {
  components: { MonacoEditor, HttpHeadersModel },
  data() {
    return {
      properties: {
        connectTimeout: 6000,
        readTimeout: 6000,
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        body: ''
      },
      methodList: ['GET', 'POST', 'PUT', 'DELETE', 'HEAD', 'PATCH', 'OPTIONS', 'TRACE'],
      rules: {
        url: [{ required: true, message: '请输入URL', trigger: 'blur' }],
        path: [{ required: true, message: '请输入路径', trigger: 'blur' }],
        method: [{ required: true, message: '请选择请求方式', trigger: 'blur' }]
      }
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
      this.$refs.HttpHeadersModel.set(this.properties.headers)
      this.$refs.MonacoEditor.set(this.properties.body)
    })
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.HttpHeadersModel.set(this.properties.headers)
        this.$refs.MonacoEditor.set(this.properties.body)
      })
    },
    get(callback) {
      this.properties.headers = this.$refs.HttpHeadersModel.get()
      this.properties.body = this.$refs.MonacoEditor.get()
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

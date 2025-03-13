<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='响应类型' prop='contentType'>
          <a-select v-model='properties.contentType' placeholder='请选择响应类型'>
            <a-select-option v-for='(item,index) in contentTypeList' :key='index' :value='item'>{{ item }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <http-headers-model ref='HttpHeadersModel' label='响应头'></http-headers-model>
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
import HttpHeadersModel from '@/views/rule/properties/model/HttpHeadersModel.vue'

export default {
  components: { HttpHeadersModel, MonacoEditor },
  data() {
    return {
      properties: {
        headers: {},
        requestId: '${requestId}',
        content: ''
      },
      contentTypeList: [
        'application/x-www-form-urlencoded',
        'multipart/form-data',
        'application/json',
        'application/xml',
        'text/plain',
        'text/xml',
        'text/html',
        'application/octet-stream'
      ],
      rules: {
        requestId: [{ required: true, message: '请输入RequestId字段', trigger: 'blur' }],
        contentType: [{ required: true, message: '请选择响应类型', trigger: 'change' }]
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
      this.$refs.MonacoEditor.set(this.properties.content)
    })
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.HttpHeadersModel.set(this.properties.headers)
        this.$refs.MonacoEditor.set(this.properties.content)
      })
    },
    get(callback) {
      this.properties.headers = this.$refs.HttpHeadersModel.get()
      this.properties.content = this.$refs.MonacoEditor.get()
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

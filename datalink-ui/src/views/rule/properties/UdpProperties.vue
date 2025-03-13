<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='端口' prop='port'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='body'>
        <a-form-model-item label='响应(Hex)'>
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
        response:''
      },
      rules: {
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }]
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

<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='文件路径' prop='path'>
          <a-input v-model='properties.path' placeholder='请输入文件路径' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='缓存数量' prop='buffer'>
          <a-input-number v-model='properties.buffer' placeholder='请输入缓存数量' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='行模式' prop='lineMode'>
          <bool-select v-model='properties.lineMode' placeholder='请选择是否行模式' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='content'>
        <a-form-model-item label='内容模板'>
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
        buffer: 5,
        lineMode: true
      },
      rules: {
        path: [{ required: true, message: '请输入文件路径', trigger: 'blur' }],
        buffer: [{ required: true, message: '请输入缓存数量', trigger: 'blur' }],
        lineMode: [{ required: true, message: '请选择是否行模式', trigger: 'change' }]
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
      this.$refs.MonacoEditor.set(this.properties.content)
    })
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.content)
      })
    },
    get(callback) {
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

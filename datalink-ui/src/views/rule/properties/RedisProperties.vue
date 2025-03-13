<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='资源' prop='resourceId'>
          <node-resource-select v-model='properties.resourceId' :resource-type='param.resourceType'
                                @change='(res) =>properties= Object.assign({}, properties, res.properties)'></node-resource-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='sql'>
        <a-form-model-item label='命令模板' prop='command'>
          <monaco-editor ref='MonacoEditor' language='redis'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'
import NodeResourceSelect from '@/views/rule/resource/NodeResourceSelect.vue'

export default {
  components: { NodeResourceSelect, MonacoEditor },
  data() {
    return {
      properties: {},
      rules: {
        resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }],
        command: [{ required: true, validator: this.checkEditor, message: '请输入命令模板', trigger: 'blur' }],
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
    this.properties.driverMode = this.param.driverMode
    this.properties.resourceType = this.param.resourceType
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.command)
      })
    },
    get(callback) {
      this.properties.command = this.$refs.MonacoEditor.get()
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

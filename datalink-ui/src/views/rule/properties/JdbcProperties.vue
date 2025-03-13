<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='资源' prop='resourceId'>
          <node-resource-select v-model='properties.resourceId' :resource-type='param.resourceType'
                                @change='(res) =>properties= Object.assign({}, properties, res.properties)'></node-resource-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if='param.driverMode==="SOURCE"'>
        <a-form-model-item label='传输方式' prop='transferType'>
          <a-select v-model='properties.transferType' placeholder='请选择传输方式'>
            <a-select-option value='single'>逐行传输</a-select-option>
            <a-select-option value='pack'>打包传输</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='sql'>
        <a-form-model-item label='SQL模板' prop='sql'>
          <monaco-editor ref='MonacoEditor' language='sql'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'
import NodeResourceSelect from '../resource/NodeResourceSelect.vue'

export default {
  components: { MonacoEditor, NodeResourceSelect },
  data() {
    return {
      properties: {
        transferType: 'single'
      },
      rules: {
        resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }],
        sql: [{ required: true, validator: this.checkEditor, message: '请输入SQL模板', trigger: 'blur' }],
        transferType: [{ required: true, message: '请选择传输方式', trigger: 'change' }]
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
      if (this.param.driverMode === 'TARGET') {
        delete this.properties.transferType
      }
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.sql)
      })
    },
    get(callback) {
      this.properties.sql = this.$refs.MonacoEditor.get()
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

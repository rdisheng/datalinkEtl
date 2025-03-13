<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='资源' prop='resourceId'>
          <node-resource-select v-model='properties.resourceId' :resource-type='param.resourceType'
                                @change='(res) =>properties= Object.assign({}, properties, res.properties)'></node-resource-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='交换机(Exchange)' prop='exchange'>
          <a-input v-model='properties.exchange' placeholder='请输入交换机名' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='队列(Queue)' prop='queue'>
          <a-input v-model='properties.queue' placeholder='请输入队列名' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='消息模板'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
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
        queue: [{ required: true, message: '请输入队列名', trigger: 'blur' }],
        exchange: [{ required: true, message: '请输入交换机名', trigger: 'change' }],
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
      if (this.param.driverMode === 'TARGET') {
        this.$nextTick(() => {
          this.$refs.MonacoEditor.set(this.properties.payload)
        })
      }
    },
    get(callback) {
      if (this.param.driverMode === 'TARGET') {
        this.properties.payload = this.$refs.MonacoEditor.get()
      }
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

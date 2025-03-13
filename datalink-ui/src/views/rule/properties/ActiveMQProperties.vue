<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='资源' prop='resourceId'>
          <node-resource-select v-model='properties.resourceId' :resource-type='param.resourceType'
                                @change='(res) =>properties= Object.assign({}, properties, res.properties)'></node-resource-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='模式' prop='model'>
          <a-select v-model='properties.model' placeholder='请选择模式'>
            <a-select-option value='queue'>Queue</a-select-option>
            <a-select-option value='topic'>Topic</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>

      <a-col :span='24' v-if='properties.model==="queue"'>
        <a-form-model-item label='Queue' prop='queue'>
          <a-input v-model='properties.queue' placeholder='请输入Queue' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if='properties.model==="topic"'>
        <a-form-model-item label='Topic' prop='topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
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
      properties: {
        model: 'queue'
      },
      rules: {
        resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }],
        topic: [{ required: true, message: '请输入Topic', trigger: 'blur' }],
        queue: [{ required: true, message: '请输入Queue', trigger: 'change' }],
        model: [{ required: true, message: '请选择消费模式', trigger: 'change' }]
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
      if(this.properties.model==='queue'){
        delete this.properties.topic
      }else {
        delete this.properties.queue
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

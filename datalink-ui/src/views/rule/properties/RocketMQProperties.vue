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
        <a-form-model-item label='Topic' prop='topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='消息Tags' prop='tags'>
          <a-input v-model='properties.tags' placeholder='请输入消息Tags' />
        </a-form-model-item>
      </a-col>

      <a-col :span='12' v-if="param.driverMode==='SOURCE'">
        <a-form-model-item label='消费模式' prop='model'>
          <a-select v-model='properties.model' placeholder='请选择消费模式'>
            <a-select-option value='CLUSTERING'>集群模式</a-select-option>
            <a-select-option value='BROADCASTING'>广播模式</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>

      <a-col :span='12' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='发送超时(ms)' prop='timeout'>
          <a-input-number v-model='properties.timeout' placeholder='请输入发送超时' style='width: 100%' />
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
      sourceDefaultValue: {
        tags: '*',
        model:'CLUSTERING'
      },
      destDefaultValue: {
        tags: '',
        timeout: 10000
      },
      rules: {
        resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }],
        topic: [{ required: true, message: '请输入Topic', trigger: 'blur' }],
        timeout: [{ required: true, message: '请输入发送超时', trigger: 'change' }],
        model: [{ required: true, message: '请选择消费模式', trigger: 'change' }]
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
    this.properties = Object.assign({}, this.param.driverMode === 'SOURCE' ? this.sourceDefaultValue : this.destDefaultValue)
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

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
        <a-form-model-item :label='param.driverMode==="SOURCE" ? "订阅Topic":"发布Topic"' prop='topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic'>
            <a-tooltip slot='suffix' title='可订阅单个或多个主题，订阅多主题时，请使用逗号分隔（,）' v-if="param.driverMode==='SOURCE'">
              <a-icon type='info-circle' style='color: rgba(92,92,92,0.45)' />
            </a-tooltip>
          </a-input>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='Qos' prop='qos'>
          <a-select v-model='properties.qos' placeholder='请选择Qos'>
            <a-select-option :value='0'>0</a-select-option>
            <a-select-option :value='1'>1</a-select-option>
            <a-select-option :value='2'>2</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='SOURCE'">
        <a-form-model-item label='共享订阅' prop='share'>
          <bool-select v-model='properties.share' placeholder='请选择是否共享订阅' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='SOURCE' && properties.version === 5">
        <a-form-model-item label='No Local' prop='noLocal'>
          <bool-select v-model='properties.noLocal' placeholder='No Local flag' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='SOURCE' && properties.version === 5">
        <a-form-model-item label='Retain As Publish' prop='retainAsPublished'>
          <bool-select v-model='properties.retainAsPublished' placeholder='Retain as Published flag'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='SOURCE' && properties.version === 5">
        <a-form-model-item label='Retain Handling' prop='retainHandling'>
          <a-select v-model='properties.retainHandling' placeholder='Retain Handling'>
            <a-select-option :value='0'>0</a-select-option>
            <a-select-option :value='1'>1</a-select-option>
            <a-select-option :value='2'>2</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='消息保留' prop='retained'>
          <bool-select v-model='properties.retained' placeholder='请选择是否消息保留' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='连接池大小' prop='maxTotal'>
          <a-input-number v-model='properties.maxTotal' placeholder='请输入连接池大小' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='最大空闲' prop='maxIdle'>
          <a-input-number v-model='properties.maxIdle' placeholder='请输入最大空闲数' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='最小空闲' prop='minIdle'>
          <a-input-number v-model='properties.minIdle' placeholder='请输入最小空闲数' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET' && properties.version === 5">
        <a-form-model-item label='消息过期时间(秒)' prop='messageExpiryInterval'>
          <a-input v-model='properties.messageExpiryInterval' placeholder='请输入消息过期时间' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET' && properties.version === 5">
        <a-form-model-item label='有效载荷标识' prop='payloadFormat'>
          <bool-select v-model='properties.payloadFormat' placeholder='请选择有效载荷标识' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET' && properties.version === 5">
        <a-form-model-item label='内容类型' prop='contentType'>
          <a-input v-model='properties.contentType' placeholder='请输入内容类型' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET' && properties.version === 5">
        <a-form-model-item label='主题别名' prop='topicAlias'>
          <a-input-number v-model='properties.topicAlias' placeholder='请输入主题别名' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="properties.version === 5">
        <a-form-model-item label='数据订阅标识符' prop='subscriptionIdentifier'>
          <a-input-number v-model='properties.subscriptionIdentifier' placeholder='请输入数据订阅标识符' style='width: 100%;' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET' && properties.version === 5">
        <a-form-model-item label='响应主题' prop='responseTopic'>
          <a-input v-model='properties.responseTopic' placeholder='请输入响应主题' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="param.driverMode==='TARGET' && properties.version === 5">
        <a-form-model-item label='对比数据' prop='correlationData'>
          <a-input v-model='properties.correlationData' placeholder='请输入对比数据' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload' v-if="param.driverMode==='TARGET'">
        <a-form-model-item label='消息模板'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload' v-if="param.driverMode==='TARGET' && properties.version === 5">
        <mqtt-user-properties-model ref='MqttUserPropertiesModel'></mqtt-user-properties-model>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor'
import MqttUserPropertiesModel from './model/MqttUserPropertiesModel'
import NodeResourceSelect from '@/views/rule/resource/NodeResourceSelect.vue'

export default {
  components: { NodeResourceSelect, MonacoEditor, MqttUserPropertiesModel },
  data() {
    return {
      properties: {},
      sourceDefaultValue: {
        qos: 0,
        share: false,
        noLocal: false,
        retainAsPublished: false,
        retainHandling: 0
      },
      destDefaultValue: {
        qos: 0,
        maxTotal: 8,
        maxIdle: 8,
        minIdle: 4,
        retained: false,
        payloadFormat: false
      },
      rules: {
        resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }],
        topic: [{ required: true, message: '请输入Topic', trigger: 'blur' }],
        retained: [{ required: true, message: '请选择是否消息保留', trigger: 'change' }],
        share: [{ required: true, message: '请选择是否共享订阅', trigger: 'change' }],
        qos: [{ required: true, message: '请选择Qos', trigger: 'change' }],
        maxTotal: [{ required: true, message: '请输入连接池大小', trigger: 'blur' }],
        maxIdle: [{ required: true, message: '请输入最大空闲数', trigger: 'blur' }],
        minIdle: [{ required: true, message: '请输入最小空闲数', trigger: 'blur' }]
      },
      resourceList: [],
      url: {
        resourceList: '/api/resource/list'
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
      delete this.properties.version
      this.properties = Object.assign({}, this.properties, properties)
      if (this.param.driverMode==='TARGET') {
        this.$nextTick(() => {
          this.$refs.MonacoEditor.set(this.properties.payload)
          if (this.properties.version === 5) {
            this.$refs.MqttUserPropertiesModel.set(this.properties.userProperties)
          }
        })
      }
    },
    get(callback) {
      if (this.param.driverMode==='TARGET') {
        this.properties.payload = this.$refs.MonacoEditor.get()
        if (this.properties.version === 5) {
          this.properties.userProperties = this.$refs.MqttUserPropertiesModel.get()
        } else {
          delete this.properties.messageExpiryInterval
          delete this.properties.payloadFormat
          delete this.properties.contentType
          delete this.properties.topicAlias
          delete this.properties.subscriptionIdentifier
          delete this.properties.responseTopic
          delete this.properties.correlationData
        }
      } else if (this.param.driverMode === 'SOURCE') {
        if (this.properties.version !== 5) {
          delete this.properties.noLocal
          delete this.properties.retainAsPublished
          delete this.properties.retainHandling
          delete this.properties.subscriptionIdentifier
        }
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

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
            <a-select-option value='single'>每个点位逐条传输</a-select-option>
            <a-select-option value='pack'>所有点位打包传输</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if='param.driverMode==="TARGET"'>
        <a-form-model-item label='数据地址' prop='address'>
          <a-input v-model='properties.address' placeholder='请输入数据地址' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if='param.driverMode==="TARGET"'>
        <a-form-model-item label='数据值' prop='dataValue'>
          <a-input v-model='properties.dataValue' placeholder='请输入数据值' />
        </a-form-model-item>
      </a-col>
    </a-form-model>
    <a-col :span='24' v-if='param.driverMode==="SOURCE"'>
      <a-button @click='editPoint' icon='bars'>点位配置 ({{ properties.points.length}})</a-button>
    </a-col>
    <points-config-model v-if='param.driverMode==="SOURCE"' ref='PointsConfigModel' @update='updatePoints'></points-config-model>
  </a-row>
</template>

<script>

import PointsConfigModel from '../points/PointsConfigModel.vue'
import NodeResourceSelect from '@/views/rule/resource/NodeResourceSelect.vue'

export default {
  components: { NodeResourceSelect, PointsConfigModel },
  data() {
    return {
      properties: {
        points: [],
        transferType: 'single'
      },
      rules: {
        resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }],
        transferType: [{ required: true, message: '请选择传输方式', trigger: 'change' }],
        address: [{ required: true, message: '请输入数据地址', trigger: 'blur' }],
        dataValue: [{ required: true, message: '请输入数据值', trigger: 'blur' }]
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
        delete this.properties.points
        delete this.properties.transferType
      }
      this.properties = Object.assign({}, this.properties, properties)
    },
    get(callback) {
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    },
    editPoint() {
      this.$refs.PointsConfigModel.edit(this.properties.resourceType, this.properties.points)
    },
    updatePoints(points) {
      this.properties.points = points
    }
  }
}
</script>

<style>


</style>

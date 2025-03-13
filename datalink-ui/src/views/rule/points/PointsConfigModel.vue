<template>
  <a-modal v-model='visible' title='点位配置' :width='850' :closable='false' :maskClosable='false'
           :bodyStyle='{paddingTop:"15px"}'>
    <template slot='footer'>
      <a-button key='back' @click='handleClose'>取消</a-button>
      <a-button key='submit' type='primary' @click='handleOk' v-show='!disableEdit'>保存</a-button>
    </template>
    <component ref='PointModel' :is='resourceComponent' v-if='resourceComponent'
               :disable-edit='disableEdit'></component>
  </a-modal>
</template>

<script>
import Vue from 'vue'

export default {
  name: 'PointsConfigModel',
  data() {
    return {
      visible: false,
      resourceType: null,
      disableEdit: false,
      resourceComponent: undefined,
      resourceComponentMap: {
        OPCUA: 'OpcUAPointsModel',
        SNMP: 'SnmpPointsModel',
        MODBUSTCP: 'ModbusTcpPointsModel'
      }
    }
  },
  methods: {
    show(resourceType, points) {
      this.init(resourceType, points, true)
    },
    edit(resourceType, points) {
      this.init(resourceType, points, false)
    },
    init(resourceType, points, disableEdit) {
      this.visible = true
      this.resourceType = resourceType
      this.disableEdit = disableEdit
      if (resourceType && this.resourceComponentMap[resourceType]) {
        import('./model/' + this.resourceComponentMap[resourceType] + '.vue')
          .then(component => {
            this.resourceComponent = Vue.extend(component.default)
            this.$nextTick(() => {
              this.$refs.PointModel.set(points)
            })
          })
      }
    },
    handleOk() {
      this.$emit('update', this.$refs.PointModel.get())
      this.handleClose()
    },
    handleClose() {
      this.visible = false
      this.resourceType = null
    }
  }
}
</script>

<style scoped>

</style>
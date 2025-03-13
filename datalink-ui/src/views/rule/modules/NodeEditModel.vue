<template>
    <a-form-model ref='ruleForm' :model='nodeData' layout='vertical' :rules='rules'>
      <a-form-model-item label='节点名称' prop='name'>
        <a-input v-model='nodeData.name' placeholder='请输入节点名称' />
      </a-form-model-item>
      <component ref='PropertiesModal' :is='nodeComponent' v-if='nodeComponent' :param='componentParam'></component>
    </a-form-model>
</template>

<script>
import Vue from 'vue'

export default {
  name: 'NodeEditModel',
  data() {
    return {
      nodeData: {},
      nodeComponent: undefined,
      componentParam: {},
      rules: {
        name: [{ required: true, message: '请输入节点名称', trigger: 'change' }]
      }
    }
  }
  ,
  methods: {
    edit(nodeData) {
      this.nodeData = Object.assign({}, nodeData)
      this.importComponent()
    },
    importComponent() {
      if (this.nodeData.component.param) {
        this.componentParam = Object.assign({}, this.nodeData.component.param)
      }
      import('../properties/' + this.nodeData.component.name + '.vue').then(component => {
        this.nodeComponent = Vue.extend(component.default)
        this.$nextTick(() => {
          if (this.nodeData.properties) {
            this.$refs.PropertiesModal.set(this.nodeData.properties)
          }
        })
      })
    },
    get(callback) {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (!valid) return false
        that.$refs.PropertiesModal.get((checked, prop, outPortCount) => {
          if (!checked) return false
          that.nodeData.properties = prop
          callback(that.nodeData, outPortCount)
          that.close()
        })
      })
    }
  }
}
</script>

<style scoped>


</style>

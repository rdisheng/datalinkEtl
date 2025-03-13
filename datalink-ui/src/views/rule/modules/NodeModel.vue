<template>
  <a-drawer
    :title='title'
    :width='600'
    :visible='visible'
    @close='visible=false'
    :destroyOnClose='true'
    :bodyStyle='{maxHeight:"calc(100% - 110px)",overflowY:"auto",padding:"5px 30px 0 30px"}'
  >
    <a-tabs default-active-key='config' @change='callback'>
      <a-tab-pane key='config'>
        <span slot='tab'><a-icon type='setting' />配置</span>
        <node-edit-model ref='NodeEditModel'></node-edit-model>
      </a-tab-pane>
      <a-tab-pane key='status'>
        <span slot='tab'><a-icon type='thunderbolt' />状态</span>
        <node-status-model ref='NodeStatusModel'></node-status-model>
      </a-tab-pane>
      <a-tab-pane key='help'>
        <span slot='tab'><a-icon type='profile' />说明</span>
        <node-help-model ref='NodeHelpModel'></node-help-model>
      </a-tab-pane>
    </a-tabs>
    <div class='bottom-button'>
      <a-button type='primary' @click='handleOk' style='width: 90px'> 确定</a-button>
    </div>
  </a-drawer>
</template>

<script>
import NodeEditModel from './NodeEditModel.vue'
import NodeStatusModel from './NodeStatusModel.vue'
import NodeHelpModel from './NodeHelpModel.vue'


export default {
  name: 'NodeModel',
  components: { NodeEditModel, NodeStatusModel, NodeHelpModel },
  data() {
    return {
      visible: false,
      title: '节点',
      ruleId: undefined,
      nodeId: undefined,
      nodeData: {}
    }
  },
  methods: {
    open(ruleId, nodeId, nodeData) {
      this.visible = true
      this.ruleId = ruleId
      this.nodeId = nodeId
      this.title = nodeData.name
      this.nodeData = Object.assign({}, nodeData)
      this.$nextTick(() => {
        this.$refs.NodeEditModel.edit(this.nodeData)
      })
    },
    callback(key) {
      this.$nextTick(() => {
        if (key === 'status') {
          this.$refs.NodeStatusModel.show(this.ruleId, this.nodeId)
        } else if (key === 'help') {
          this.$refs.NodeHelpModel.show(this.nodeData.code)
        }
      })
    },
    handleOk() {
      this.$refs.NodeEditModel.get((nodeData, outPortCount) => {
        this.$emit('save', nodeData, outPortCount)
        this.visible = false
      })
    }
  }
}
</script>

<style scoped>

</style>
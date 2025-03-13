<template>
  <div>
    <a-card :bordered='false' style='margin-bottom: 10px' :body-style='{padding:"17px 24px"}'>
      <a-row>
        <a-col :span='6' v-if='!ruleNameEdit'
               style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 4px'>
          {{ rule.ruleName }}
          <a-icon type='edit' @click='ruleNameEdit = true' />
        </a-col>
        <a-col :span='6' v-if='ruleNameEdit'>
          <a-input-search v-model='rule.ruleName' placeholder='请输入规则名称' @search='saveRuleName'>
            <a-button slot='enterButton'>确定</a-button>
          </a-input-search>
        </a-col>
        <a-col :span='18' style='text-align: right'>
          <a-button style='width:85px;margin-right: 8px' @click='onClose'>返回</a-button>
          <a-button style='width:85px;margin-right: 8px'
                    @click='()=>{refreshRuntime(false);$message.success("已刷新",1)}'
                    :loading='enableLoading'
                    v-if='rule.ruleId && rule.enable'>刷新
          </a-button>
          <a-button style='width:85px;margin-right: 8px' @click='handleStart' :loading='enableLoading'
                    v-if='rule.ruleId && !rule.enable'>启动
          </a-button>
          <a-button style='width:85px;margin-right: 8px' @click='handleStop' :loading='enableLoading'
                    v-if='rule.ruleId && rule.enable'>停止
          </a-button>
          <a-button style='width:85px;margin-right: 8px' @click='handleRestart' :loading='enableLoading'
                    v-if='rule.ruleId && rule.enable'>重启
          </a-button>
          <a-button style='width:85px' type='primary' @click='saveRule' :loading='enableLoading'>保存</a-button>
        </a-col>
      </a-row>
    </a-card>

    <a-card :bordered='false'>
      <div class='rule-create'>
        <div class='node-content'>
          <div class='node-search'>
            <a-input placeholder='搜索节点' v-model='nodeSearchKeyword' @change='searchNodes' allowClear />
          </div>
          <div class='node-list'>
            <a-collapse default-active-key='0' :bordered='false' accordion>
              <a-collapse-panel v-for='(group,groupIndex) in nodeSearchList' :key='groupIndex'
                                :header='group.groupName'>
                <div class='node-list-item' v-for='(node,nodeIndex) in group.nodeList' :key='nodeIndex' draggable='true'
                     @dragend='handleDragEnd($event, node)'>
                  {{ node.name }}
                </div>
                <div v-if='group.nodeList <= 0' style='text-align: center'>
                  无{{ group.groupName }}节点
                </div>
              </a-collapse-panel>
            </a-collapse>
          </div>
        </div>
        <div class='graph-content'>
          <div id='minimap'></div>
          <div id='control'>
            <a-button-group>
              <a-button icon='undo' @click='graph.undo()' :disabled='!canUndo' style='width: 40px'
                        title='撤销(添加/删除)' />
              <a-button icon='redo' @click='graph.redo()' :disabled='!canRedo' style='width: 40px'
                        title='重做(添加/删除)' />
              <a-button icon='environment' @click='graph.centerContent() && graph.zoomTo(1)' style='width: 40px'
                        title='重置视角' />
              <a-popover placement='bottomRight' trigger='click'>
                <template slot='title'>
                  <span>操作说明</span>
                </template>
                <template slot='content'>
                  <p>
                    <a-tag>左键点击</a-tag>
                    选中节点
                  </p>
                  <p>
                    <a-tag>左键框选</a-tag>
                    多选节点
                  </p>
                  <p>
                    <a-tag>右键滑动</a-tag>
                    拖动画布
                  </p>
                  <p>
                    <a-tag>delete</a-tag>
                    删除选中节点
                  </p>
                  <p>
                    <a-tag>ctrl+c</a-tag>
                    复制选中节点
                  </p>
                  <p>
                    <a-tag>ctrl+v</a-tag>
                    粘贴选中节点
                  </p>
                </template>
                <a-button icon='question-circle' style='width: 40px' title='操作说明' />
              </a-popover>
            </a-button-group>
          </div>
          <div id='graph' @dragover='e=>e.preventDefault()'></div>
        </div>
      </div>
    </a-card>

    <node-model ref='NodeModel' @save='saveNodeData'></node-model>

  </div>
</template>

<script>
import { Graph } from '@antv/x6'
import '@antv/x6-vue-shape'
import RuleGraphNode from './node/RuleGraphNode.vue'
import NodeModel from './modules/NodeModel.vue'
import { nodeListWithGroup } from '@/config/node.config'
import { getAction, postAction, putAction } from '@/api/manage'
import { uuid } from '@/utils/util'

let groups = {
  in: {
    position: 'left',
    attrs: {
      circle: {
        r: 4,
        magnet: 'passive',
        stroke: '#C2C8D5',
        strokeWidth: 1,
        fill: '#fff'
      }
    },
    magnet: true
  },
  out: {
    position: 'right',
    attrs: {
      circle: {
        r: 4,
        magnet: true,
        stroke: '#C2C8D5',
        strokeWidth: 1,
        fill: '#fff'
      }
    },
    magnet: 'passive' // 设置为被动磁力吸附
  }
}

export default {
  name: 'RuleGraphModel',
  components: { NodeModel },
  data() {
    return {
      rule: {},
      graph: null,
      nodeAllList: [],
      nodeSearchList: [],
      nodeSearchKeyword: '',
      currSelectNode: undefined,
      url: {
        add: '/api/rule/add',
        update: '/api/rule/update',
        info: '/api/rule/info',
        startAndSave: '/api/rule/startAndSave',
        stop: '/api/rule/stop',
        restart: '/api/rule/restart',
        runtime: '/api/runtime/rule/info',
        plugin: '/api/plugin/list'
      },
      graphWidth: undefined,
      graphHeight: undefined,
      ruleNameEdit: false,
      enableLoading: false,
      canRedo: false,
      canUndo: false
    }
  },
  mounted() {
    this.getNodeList()

    this.graph = new Graph({
      container: document.getElementById('graph'),
      grid: {
        size: 15,
        visible: true,
        type: 'doubleMesh',
        args: [
          {
            color: '#d2d1d1', // 主网格线颜色
            thickness: 0.5     // 主网格线宽度
          },
          {
            color: '#dedede', // 次网格线颜色
            thickness: 0.5,     // 次网格线宽度
            factor: 4        // 主次网格线间隔
          }
        ]
      },
      clipboard: { // 允许复制粘贴
        enabled: true,
        useLocalStorage: true
      },
      keyboard: true, // 允许快捷键
      autoResize: true,
      history: {  //记录历史
        enabled: true,
        ignoreChange: true
      },
      connecting: {
        snap: true, // 自动吸附
        allowNode: false,
        allowBlank: false, //是否允许连接到画布空白位置的点
        allowMulti: 'withPort', //是否允许在相同的起始节点和终止之间创建多条边
        allowLoop: false, //是否允许创建循环连线，即边的起始节点和终止节点为同一节点
        highlight: true, //拖动边时，是否高亮显示所有可用的节点
        connector: {
          name: 'smooth'
        }
      },
      selecting: { // 框选
        enabled: true,
        multiple: true,
        rubberband: true,
        movable: true,
        showNodeSelectionBox: true
      },
      scroller: {  // 滚动
        enabled: true,
        padding: 100,
        pannable: {  // 拖动
          enabled: true,
          eventTypes: ['rightMouseDown']
        }
      },
      minimap: {
        enabled: true,
        width: 155,
        height: 85,
        container: document.getElementById('minimap')
      }
    })

    //delete
    this.graph.bindKey('delete', () => {
      const cells = this.graph.getSelectedCells()
      if (cells.length) {
        this.graph.removeCells(cells)
        this.$message.success('已删除', 0.5)
      }
    })

    this.graph.bindKey('ctrl+c', () => {
      const cells = this.graph.getSelectedCells()
      if (cells.length) {
        this.graph.copy(cells)
        this.$message.success('复制成功', 0.5)
      }
      return false
    })

    this.graph.bindKey('ctrl+v', () => {
      if (!this.graph.isClipboardEmpty()) {
        const cells = this.graph.paste({ offset: 32 })
        this.graph.cleanSelection()
        this.graph.select(cells)
        this.$message.success('粘贴成功', 0.5)
      }
      return false
    })

    this.graph.on('node:mouseenter', ({ node }) => {
      node.addTools([{
        name: 'button-remove', args: {
          x: '100%',
          y: 0,
          offset: { x: -18, y: 21 }
        }
      }])
    })

    this.graph.on('node:mouseleave', ({ node }) => {
      if (node.hasTool('button-remove')) {
        node.removeTool('button-remove')
      }
    })


    this.graph.on('node:click', ({ node }) => {
      if (node.hasTool('button-remove')) {
        node.removeTool('button-remove')
      }
    })

    this.graph.on('edge:mouseenter', ({ edge }) => {
      edge.addTools([{ name: 'button-remove' }])
    })

    this.graph.on('edge:mouseleave', ({ edge }) => {
      if (edge.hasTool('button-remove')) {
        edge.removeTool('button-remove')
      }
    })

    this.graph.on('edge:added', ({ edge }) => {
      edge.attr({
        line: {
          stroke: 'rgba(89,87,87,0.87)',
          strokeWidth: 2,
          targetMarker: {
            name: 'classic',
            size: 6
          }
        }
      })
    })

    this.graph.on('node:dblclick', ({ node }) => {
      this.currSelectNode = node
      this.$refs.NodeModel.open(this.rule.ruleId, node.id, node.getData())
    })

    this.graph.history.on('change', () => {
      let history = this.graph.history
      this.canRedo = history.canRedo()
      this.canUndo = history.canUndo()
    })

    Graph.registerVueComponent('RuleGraphNode', {
      template: `
        <RuleGraphNode />`,
      components: { RuleGraphNode }
    }, true)


    let ruleId = this.$route.params.ruleId
    if (ruleId !== 'new') {
      getAction(this.url.info, { ruleId: ruleId }).then(res => {
        this.rule = res.data
        this.graph.fromJSON(JSON.parse(this.rule.graphJson))
        this.graph.centerContent()
        this.refreshRuntime(true)
      })
    } else {
      this.rule = { ruleName: 'rule_' + uuid(5), enable: false }
    }
    //为了防止火狐浏览器拖拽的时候以新标签打开
    document.body.ondrop = function(event) {
      event.preventDefault()
      event.stopPropagation()
    }

    let container = document.getElementById('graph')
    this.graphWidth = container.clientWidth
    this.graphHeight = container.clientHeight

  },
  methods: {

    saveNodeData(nodeData, outPort) {
      this.currSelectNode.replaceData(nodeData, { silent: true })
      if (outPort) {
        let ports = this.currSelectNode.getPortsByGroup('out')
        if (ports.length < outPort) {
          for (let i = 0; i < outPort - ports.length; i++) {
            this.currSelectNode.addPort({ group: 'out' })
          }
        }
        if (ports.length > outPort) {
          for (let i = 0; i < ports.length - outPort; i++) {
            this.currSelectNode.removePort(ports[ports.length - 1 - i])
          }
        }
      }
    },

    handleDragEnd(e, nodeData) {
      let clientToLocalPoint = this.graph.clientToLocal(e.clientX, e.clientY)

      let items = []
      for (let i = 0; i < nodeData.ports.in; i++) {
        items.push({ group: 'in' })
      }
      for (let i = 0; i < nodeData.ports.out; i++) {
        items.push({ group: 'out' })
      }
      this.graph.addNode({
        shape: 'vue-shape',
        component: 'RuleGraphNode',
        width: 120,
        height: 40,
        x: clientToLocalPoint.x - 50,
        y: clientToLocalPoint.y - 20,
        data: Object.assign({ properties: {} }, nodeData),
        ports: {
          groups: groups,
          items: items
        }
      })
    },
    onClose() {
      this.$router.push({ name: 'ruleList' })
    },

    saveRule() {
      this.enableLoading = true
      this.rule.graphJson = JSON.stringify(this.graph.toJSON())
      let obj = undefined
      if (this.rule.ruleId) {
        obj = putAction(this.url.update, this.rule)
      } else {
        obj = postAction(this.url.add, this.rule)
      }
      obj.then(res => {
        if (res.code === 200) {
          this.$message.success('保存成功', 0.5)
          if (!this.rule.ruleId) {
            this.$router.push({ path: '/rule/info/' + res.data.ruleId })
            this.$set(this.rule, 'ruleId', res.data.ruleId)
          }
        } else {
          this.$message.error('保存失败,' + res.message)
        }
      }).finally(() => {
        this.enableLoading = false
      })
    },

    saveRuleName() {
      if (!this.rule.ruleName) {
        this.$message.warn('规则名称不可为空')
        return
      }
      this.ruleNameEdit = false
    },

    handleStart() {
      this.enableLoading = true
      this.rule.graphJson = JSON.stringify(this.graph.toJSON())
      postAction(this.url.startAndSave, this.rule).then(res => {
        if (res.code === 200) {
          this.$message.success('启动成功', 1)
          this.rule.enable = true
          this.refreshRuntime(true)
        } else {
          this.$message.error('启动失败,' + response.message)
        }
      }).finally(() => {
        this.enableLoading = false
      })
    },

    handleStop() {
      this.enableLoading = true
      postAction(this.url.stop, this.rule).then(res => {
        if (res.code === 200) {
          this.$message.success('停止成功', 0.5)
          this.rule.enable = false
          this.refreshRuntime(false)
        } else {
          this.$message.error('停止失败')
        }
      }).finally(() => {
        this.enableLoading = false
      })
    },
    handleRestart() {
      this.enableLoading = true
      this.rule.graphJson = JSON.stringify(this.graph.toJSON())
      postAction(this.url.restart, this.rule).then(res => {
        if (res.code === 200) {
          this.$message.success('重启成功', 0.5)
          this.refreshRuntime(true)
        } else {
          this.$message.error('重启失败')
        }
      }).finally(() => {
        this.enableLoading = false
      })
    },
    refreshRuntime(repeat) {
      if (!this.rule.ruleId) return
      this.enableLoading = true
      getAction(this.url.runtime, { ruleId: this.rule.ruleId }).then(res => {
        if (res.code === 200 && res.data) {
          Object.keys(res.data).forEach(key => {
            this.graph.getCellById(key).updateData({ status: res.data[key] })
          })
        }
        if (repeat === true) {
          setTimeout(() => this.refreshRuntime(false), 2000)
        }
      }).finally(() => {
        this.enableLoading = false
      })
    },
    searchNodes() {
      if (!this.nodeSearchKeyword) {
        this.nodeSearchList = this.nodeAllList
        return
      }
      const filteredData = []
      this.nodeAllList.forEach(group => {
        const filteredGroup = {
          groupName: group.groupName,
          groupCode: group.groupCode,
          nodeList: []
        }
        group.nodeList.forEach(node => {
          if (node.name.toLowerCase().includes(this.nodeSearchKeyword.toLowerCase())) {
            filteredGroup.nodeList.push(node)
          }
        })
        if (filteredGroup.nodeList.length > 0) {
          filteredData.push(filteredGroup)
        }
      })
      this.nodeSearchList = filteredData
    },
    getNodeList() {
      let group = {
        groupName: '插件',
        groupCode: 'PLUGIN',
        nodeList: []
      }
      postAction(this.url.plugin, {}).then(res => {
        if (res.code === 200 && res.data) {
          for (let plugin of res.data) {
            group.nodeList.push({
              name: plugin.pluginName,
              code: 'PLUGIN',
              group: 'PLUGIN',
              style: plugin.inputCount > 0 ? (plugin.outputCount > 0 ? 'handle' : 'target') : 'source',
              ports: { in: plugin.inputCount, out: plugin.outputCount },
              component: { name: 'PluginProperties' },
              properties: { plugin: plugin }
            })
          }
        }
      }).finally(() => {
        this.nodeAllList = Object.assign([], nodeListWithGroup)
        this.nodeAllList.push(group)
        this.nodeSearchList = this.nodeAllList
      })
    }
  }
}
</script>

<style>

.rule-create {
  display: flex;
  align-items: stretch;
}

.node-content {
  flex: 0 0 200px;
  display: flex;
  flex-direction: column;
  height: 72vh;
  border: 1px #e0dfdf solid;
}

.node-content .node-search {
  padding: 10px;
  height: 53px;
  border-bottom: 1px #e0dfdf solid;
}

.node-content .node-list {
  flex-grow: 1;
  overflow-y: auto;
  padding-bottom: 20px;
}

.graph-content {
  flex: 1;
  height: 72vh;
  border: 1px #e0dfdf solid;
  border-left: none;
}

.graph-content #graph {
  width: 100%;
  height: 100%;
}

.graph-content #minimap {
  position: absolute;
  top: 34px;
  right: 40px;
  border: 1px #cccccc solid;
  z-index: 100;
  box-shadow: #f1f1f1 4px 4px 4px;
}

.graph-content #control {
  position: absolute;
  top: 130px;
  right: 40px;
  z-index: 100;
  box-shadow: #f1f1f1 4px 4px 4px;
}

.x6-widget-minimap-viewport {
  border: 1px solid #a2a2a2;
}

.x6-widget-minimap-viewport-zoom {
  border: 1px solid #a2a2a2;
}


.node-list-item {
  background: white;
  padding: 8px 0 8px 25px;
  margin-bottom: 6px;
  border: 1px #e0dfdf solid;
  border-radius: 3px;
  cursor: pointer;
}

.node-list-item:hover {
  border: 1px #1890ff solid;
}

.ant-collapse > .ant-collapse-item > .ant-collapse-header {
  padding-top: 10px;
  padding-bottom: 10px;
}

/*滚动条整体粗细样式*/
div.node-list::-webkit-scrollbar {
  /*高宽分别对应横竖滚动条的尺寸*/
  width: 6px;
  height: 6px;
}

/*滚动条整体粗细样式*/
div.graph-content *::-webkit-scrollbar {
  /*高宽分别对应横竖滚动条的尺寸*/
  width: 6px;
  height: 6px;
}

/*滚动条里面小方块*/
::-webkit-scrollbar-thumb {
  border-radius: 2px !important;
  /*box-shadow: inset 0 0 5px rgba(9, 9, 9, 0.2) !important;*/
  background: #d0d0d0 !important;
}

/*滚动条轨道*/
::-webkit-scrollbar-track {
  border-radius: 2px !important;
  /*box-shadow: inset 0 0 5px rgba(9, 9, 9, 0.2) !important;*/
  background: #f1f1f1 !important;
}


</style>
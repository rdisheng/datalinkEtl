<template>
  <div class='node' :class='nodeClass'>
    <div class='name'>{{ node.name }}</div>
    <div class='status'>
      <div class='status-icon' :class='statusClass'>
      </div>
      <div class='status-text'>
        {{ statusMap[status] }}
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'RuleGraphNode',
  inject: ['getNode'],
  data() {
    return {
      node: {
        name: '',
        style: ''
      },
      status: 'INIT',
      statusMap: {
        INIT: '未启动',
        NORMAL: '运行中',
        ABNORMAL: '运行失败'
      }
    }
  },
  created() {
    let node = this.getNode()
    // 从node的data中同步到本组件的data中
    this.mapper(node.data, this.$data.node)
    // 更新状态时外部调用node.updateData(),触发事件
    node.on('change:data', () => {
      if (node.data.status) {
        this.status = node.data.status
        delete node.data.status
      }
    })
  },
  computed: {
    nodeClass: function() {
      let clazz = {}
      if (this.node.style) {
        clazz[this.node.style] = true
      }
      return clazz
    },
    statusClass: function() {
      let clazz = {}
      if (this.status) {
        clazz[this.status.toLowerCase()] = true
      }
      return clazz
    }
  },
  methods: {
    mapper(source, target) {
      for (let key in target) {
        target[key] = source?.[key] ?? target[key]
      }
    }
  }
}
</script>

<style scoped>

.node {
  width: 120px;
  height: 40px;
  background-color: #fff;
  border-radius: 3px;
}

.source {
  border: 2px solid #1890ff;
  border-right: 4px solid #1890ff;
}

.handle {
  border: 2px solid #e17217;
  border-left: 4px solid #e17217;
  border-right: 4px solid #e17217;
}

.target {
  border: 2px solid #339a08;
  border-left: 4px solid #339a08;
}

.node .name {
  width: 100%;
  height: 40px;
  font-size: 14px;
  line-height: 38px;
  text-align: center;
}

.node .status {
  display: flex;
  align-items: center;
}

.node .status div {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 3px;
}

.node .status .status-text {
  font-size: 12px;
}

.node .status .status-icon {
  width: 11px;
  height: 11px;
  border: 1px #cccccc solid;
  border-radius: 4px;
}

.init {
  background-color: #d0d2d0;
}

.normal {
  background-color: #85d963;
}

.abnormal {
  background-color: #ee4747;
}

</style>
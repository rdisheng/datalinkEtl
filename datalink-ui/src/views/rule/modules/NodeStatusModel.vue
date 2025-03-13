<template>

  <div class='node-status'>
    <a-row :gutter='20'>
      <a-col :span='12' style='padding-top: 5px'>
        <a-tag v-if='runtime.status === "INIT"' color='#a6a3a3'>未启动</a-tag>
        <a-tag v-if='runtime.status === "NORMAL"' color='#87d068'>运行中</a-tag>
        <a-tag v-if='runtime.status === "ABNORMAL"' color='#f50'>运行失败</a-tag>
      </a-col>
      <a-col :span='12' style='text-align: right'>
        <a-space size='small'>
          <a-button @click='refreshRuntime'>刷新</a-button>
          <a-button @click='resetRuntime'>重置</a-button>
        </a-space>
      </a-col>
    </a-row>
    <a-card size='small' style='margin-top: 15px'>
      <span slot='title' style='font-size: 14px'>统计</span>
      <a-row :gutter='20'>
        <a-col :span='6'>
          <div>输入</div>
          <div style='font-size: 16px;font-weight: bold'>{{ runtime.count['INPUT'] }}</div>
        </a-col>
        <a-col :span='6'>
          <div>输出</div>
          <div style='font-size: 16px;font-weight: bold'>{{ runtime.count['OUTPUT'] }}</div>
        </a-col>
        <a-col :span='6'>
          <div>异常</div>
          <div style='font-size: 16px;font-weight: bold'>{{ runtime.count['EXCEPTION'] }}</div>
        </a-col>
      </a-row>
    </a-card>

    <a-card size='small' style='margin-top: 20px' :body-style='{padding:"0"}'>
      <a-tabs default-active-key='INPUT' size='small' @change='(key) => currDataTab = key' style='margin-bottom: 0'>
        <a-tab-pane key='INPUT'><span slot='tab'><a-icon type='login' />输入</span></a-tab-pane>
        <a-tab-pane key='OUTPUT'><span slot='tab'><a-icon type='logout' />输出</span></a-tab-pane>
        <a-tab-pane key='EXCEPTION'><span slot='tab'><a-icon type='bug' />异常</span></a-tab-pane>
      </a-tabs>
      <div class='data-list'>
        <div class='data-list-item' v-for='item in runtime.data[currDataTab]'>
          <div class='data-list-item-content'>{{ item.data }}</div>
          <a-row class='data-list-item-desc'>
            <a-col :span='12' style='text-align: left'>
              {{ item.time }}
            </a-col>
            <a-col :span='12' style='text-align: right'>
              <a-space>
                <span v-if='item.port'><a-icon type='logout' style='font-size: 12px' /> {{ item.port }}</span>
                <span>{{ item.member }}</span>
              </a-space>
            </a-col>
          </a-row>
        </div>
        <div class='data-list-tip'>
          {{ runtime.data[currDataTab].length > 0 ? '仅记录最近数据' : '暂无数据' }}
        </div>
      </div>
    </a-card>

  </div>


</template>

<script>
import { getAction } from '@/api/manage'

export default {
  name: 'NodeStatusModel',
  data() {
    return {
      ruleId: undefined,
      nodeId: undefined,
      runtime: {
        status: 'INIT',
        count: { INPUT: 0, OUTPUT: 0, EXCEPTION: 0 },
        data: { INPUT: [], OUTPUT: [], EXCEPTION: [] }
      },
      url: {
        info: '/api/runtime/node/info',
        reset: '/api/runtime/node/reset'
      },
      currDataTab: 'INPUT'
    }
  },
  methods: {
    show(ruleId, nodeId) {
      this.initRuntime()
      if (!ruleId) return
      this.ruleId = ruleId
      this.nodeId = nodeId
      this.getRuntime()
    },
    initRuntime() {
      this.runtime = {
        status: 'INIT',
        count: { INPUT: 0, OUTPUT: 0, EXCEPTION: 0 },
        data: { INPUT: [], OUTPUT: [], EXCEPTION: [] }
      }
    },
    getRuntime() {
      getAction(this.url.info, { ruleId: this.ruleId, nodeId: this.nodeId }).then((res) => {
        if (res.code === 200) {
          this.runtime = res.data
        }
      })
    },
    refreshRuntime() {
      this.getRuntime()
      this.$message.success('已刷新', 0.5)
    },
    resetRuntime() {
      this.$confirm({
        title: '重置此节点的运行统计数据?',
        onOk: () => {
          getAction(this.url.reset, { ruleId: this.ruleId, nodeId: this.nodeId }).then((res) => {
            if (res.code === 200) {
              this.$message.success('重置成功')
              this.getRuntime()
            } else {
              this.$message.error('重置失败')
            }
          })
        }
      })
    }
  }
}
</script>

<style>

.node-status .ant-tabs-bar {
  margin: 0 !important;
}

.node-status .data-list {
  height: 51vh;
  overflow-y: auto;
  padding: 5px 0 20px 0
}

.node-status .data-list .data-list-tip {
  text-align: center;
  font-size: 13px;
  color: #c5c5c5;
  padding-top: 15px
}


.node-status .data-list .data-list-item {
  padding: 5px 10px;
  border-bottom: 1px #ecebeb solid
}

.node-status .data-list .data-list-item .data-list-item-content {
  padding: 10px 0;
}

.node-status .data-list .data-list-item .data-list-item-desc {
  font-size: 13px;
  color: #a6a3a3
}


/*滚动条整体粗细样式*/
div.node-status *::-webkit-scrollbar {
  /*高宽分别对应横竖滚动条的尺寸*/
  width: 6px;
  height: 6px;
}

/*滚动条里面小方块*/
div.node-status *::-webkit-scrollbar-thumb {
  border-radius: 2px !important;
  /*box-shadow: inset 0 0 5px rgba(9, 9, 9, 0.2) !important;*/
  background: #d0d0d0 !important;
}

/*滚动条轨道*/
div.node-status *::-webkit-scrollbar-track {
  border-radius: 2px !important;
  /*box-shadow: inset 0 0 5px rgba(9, 9, 9, 0.2) !important;*/
  background: #f1f1f1 !important;
}



</style>
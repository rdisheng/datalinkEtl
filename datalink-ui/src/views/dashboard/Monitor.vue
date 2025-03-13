<template>
  <page-header-wrapper :breadcrumb='false'>
    <div style='padding-bottom: 20px'>
      <a-row :gutter='24'>
        <a-col :span='6'>
          <a-card :bordered='false'>
            <a-statistic
              title='总CPU使用情况'
              :value="' '+Number(useCPU / totalCPU).toFixed(2) + '%'"
              class='demo-class'
              :value-style="{ color:'#1890ff',fontSize: '29px' }"
            >
              <template #prefix>
                <a-icon type='rocket' theme='twoTone' />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span='6'>
          <a-card :bordered='false'>
            <a-statistic
              title='总内存使用情况'
              :value="' '+Number(useMem).toFixed(2) + 'M'"
              class='demo-class'
              :value-style="{color:'#1890ff', fontSize: '29px' }"
            >
              <template #prefix>
                <!--                <a-icon type="smile" theme="twoTone" />-->
                <a-icon type='thunderbolt' theme='twoTone' />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span='6'>
          <a-card :bordered='false'>
            <a-statistic
              title='总资源数量'
              :value="' '+systemStatistics.resourceCount"
              class='demo-class'
              :value-style="{ color:'#1890ff', fontSize: '29px' }"
            >
              <template #prefix>
                <a-icon type='appstore' theme='twoTone' />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span='6'>
          <a-card :bordered='false'>
            <a-statistic
              title='总规则数量'
              :value="' '+systemStatistics.ruleCount"
              class='demo-class'
              :value-style="{ color:'#1890ff',fontSize: '29px' }"
            >
              <template #prefix>
                <a-icon type='api' theme='twoTone' />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>
    </div>
    <a-card :body-style='{paddingTop:"10px"}' :bordered='false' class='monitor-info'>
      <a-tabs default-active-key='1' v-model='activeKey'>
        <a-tab-pane key='1' tab='基本信息'>
          <SystemInfo ref='systemInfo' :nodeUrl='node'></SystemInfo>
        </a-tab-pane>
        <a-tab-pane key='2' tab='JVM信息'>
          <JvmInfo ref='jvmInfo' :nodeUrl='node'></JvmInfo>
        </a-tab-pane>
        <a-tab-pane key='3' tab='磁盘信息'>
          <DiskInfo ref='diskInfo'></DiskInfo>
        </a-tab-pane>
        <a-tab-pane key='4' tab='监听器'>
          <ListenerInfo ref='listenerInfo'></ListenerInfo>
        </a-tab-pane>
        <a-tab-pane key='5' tab='集群节点'>
          <ClusterInfo ref='clusterInfo'></ClusterInfo>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import moment from 'moment'
import { getSystemStatistics } from '@/api/system'
import { getAction } from '@/api/manage'
import JvmInfo from './module/JvmInfo'
import SystemInfo from './module/SystemInfo'
import DiskInfo from './module/DiskInfo'
import ListenerInfo from './module/ListenerInfo'
import ClusterInfo from './module/ClusterInfo'

export default {
  name: 'Monitor',
  components: {
    JvmInfo,
    SystemInfo,
    DiskInfo,
    ListenerInfo,
    ClusterInfo
  },
  data() {
    return {
      node: '',
      nodeList: [],
      totalCPU: 1,
      useCPU: 1,
      totalJVMMem: 1,
      useMem: 1,
      activeKey: '1',
      systemStatistics: {
        resourceCount: 0,
        ruleCount: 0
      },
      timer: null
    }
  },
  mounted() {
    const _this = this // 声明一个变量指向Vue实例this，保证作用域一致
    this.getTotalSystemInfo()
    if (this.timer) {
      clearInterval(this.timer)
    }
    this.timer = setInterval(() => {
      _this.getTotalSystemInfo()
    }, 10000)
  },
  methods: {
    getTotalSystemInfo() {
      this.totalCPU = 1
      this.useCPU = 0
      this.totalJVMMem = 1
      this.useMem = 0
      getAction('/actuator/metrics/system.cpu.count').then(value => {
        const val = value.measurements[0].value
        this.totalCPU += val
        getAction('/actuator/metrics/process.cpu.usage').then(value => {
          let usage = value.measurements[0].value
          usage = this.convert(usage, Number)
          this.useCPU += val * usage
        })
      })
      getAction('/actuator/metrics/jvm.memory.max').then(value => {
        let val = value.measurements[0].value
        val = this.convertMem(val, Number)
        this.totalJVMMem += val
      })
      getAction('/actuator/metrics/jvm.memory.used').then(value => {
        let used = value.measurements[0].value
        used = this.convertMem(used, Number)
        this.useMem = Number(this.useMem) + Number(used)
      })
      getSystemStatistics().then(res => {
        this.systemStatistics = res.data
      })
    },
    convert(value, type) {
      if (type === Number) {
        return Number(value * 100).toFixed(2)
      } else if (type === Date) {
        return moment(value * 1000).format('YYYY-MM-DD HH:mm:ss')
      }
      return value
    },
    convertMem(value, type) {
      if (type === Number) {
        return Number(value / 1048576).toFixed(2)
      }
      return value
    }
  }
}
</script>

<style>

.monitor-info .ant-tabs-bar {
  margin: 0 !important;
}

</style>

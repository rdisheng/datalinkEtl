<!--<template>-->
<!--  <div>-->

<!--    <a-card :bordered='false' style='margin-bottom: 24px' :body-style='{padding:"17px 24px"}'>-->
<!--      <a-row>-->
<!--        <a-col :span='12' style='padding-top: 4px'>-->
<!--          <span style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);'>{{ rule.ruleName }}</span>-->
<!--          <a-tag color='green' v-if='rule.enable' style='margin-left: 10px'>-->
<!--            运行中-->
<!--          </a-tag>-->
<!--        </a-col>-->
<!--        <a-col :span='12' style='text-align: right'>-->
<!--          <a-space size='small'>-->
<!--            <a-button @click='refresh' style='width:70px'>刷新</a-button>-->
<!--            <a-button @click='handleStart' style='width:70px' v-if='!rule.enable'>启动</a-button>-->
<!--            <a-button @click='handleStop' style='width:70px' v-if='rule.enable'>停止</a-button>-->
<!--            <a-button @click='handleEdit' style='width:70px' :disabled='rule.enable'>配置</a-button>-->
<!--            <a-button @click='resetRuntime' style='width:70px' :disabled='rule.enable' type="danger">重置</a-button>-->
<!--            <a-button @click='onClose' style='width:70px' type='primary'>返回</a-button>-->
<!--          </a-space>-->
<!--        </a-col>-->
<!--      </a-row>-->
<!--    </a-card>-->

<!--    <a-card :bordered='false' style='margin-bottom: 24px'>-->
<!--      <a-row>-->
<!--        <a-col :span='3'>-->
<!--          <a-statistic title='接收成功' :value='runtime.receiveSuccessCount' />-->
<!--        </a-col>-->
<!--        <a-col :span='3'>-->
<!--          <a-statistic title='转换成功' :value='runtime.transformSuccessCount' />-->
<!--        </a-col>-->
<!--        <a-col :span='3'>-->
<!--          <a-statistic title='发送成功' :value='runtime.publishSuccessCount' />-->
<!--        </a-col>-->
<!--        <a-col :span='3'>-->
<!--          <a-statistic title='接收失败' :value='runtime.receiveFailCount' />-->
<!--        </a-col>-->
<!--        <a-col :span='3'>-->
<!--          <a-statistic title='转换失败' :value='runtime.transformFailCount' />-->
<!--        </a-col>-->
<!--        <a-col :span='3'>-->
<!--          <a-statistic title='发送失败' :value='runtime.publishFailCount' />-->
<!--        </a-col>-->
<!--        <a-col :span='6'>-->
<!--          <a-statistic title='启动时间' :value="runtime.startTime ? runtime.startTime : '—'"-->
<!--                       :value-style='{fontSize: "21px"}' />-->
<!--        </a-col>-->
<!--      </a-row>-->
<!--    </a-card>-->

<!--    <a-card :bordered='false' style='margin-bottom: 24px'>-->
<!--      <runtime-graph ref='RuntimeGraph'></runtime-graph>-->
<!--    </a-card>-->

<!--    <a-card style='margin-bottom: 24px' :bordered='false' :body-style='{paddingBottom:"10px"}'>-->
<!--      <div class='title'>数据源</div>-->
<!--      <a-list :grid='{ gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1 }' :data-source='rule.sourceResourceList'-->
<!--              v-if='rule.sourceResourceList && rule.sourceResourceList.length>0'>-->
<!--        <a-list-item slot='renderItem' slot-scope='resource,index'>-->
<!--          <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px'>-->
<!--            <a-col :span='20'>-->
<!--              <a-descriptions :column='2'>-->
<!--                <a-descriptions-item label='名称'>-->
<!--                  {{ resource.resourceName }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item label='资源ID'>-->
<!--                  {{ resource.resourceRuntimeId }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item v-for='(element,index) in getDetails(resource)' :key='index'-->
<!--                                     :label='element.name'>-->
<!--                  {{ element.value }}-->
<!--                </a-descriptions-item>-->
<!--              </a-descriptions>-->
<!--            </a-col>-->
<!--            <a-col :span='4' style='text-align: right;padding-right: 5px'>-->
<!--              <a v-if='resource.properties.points !== undefined' @click='pointConfig(resource)'>查看点位</a>-->
<!--              <a-divider type='vertical' v-if='resource.properties.points !== undefined' />-->
<!--              <a @click='lastData("source",resource.resourceRuntimeId)'>最近数据</a>-->
<!--            </a-col>-->
<!--          </a-row>-->

<!--        </a-list-item>-->
<!--      </a-list>-->
<!--    </a-card>-->


<!--    <a-card style='margin-bottom: 24px' :bordered='false' :body-style='{paddingBottom:"10px"}'>-->
<!--      <div class='title'>数据转换</div>-->
<!--      <a-list :grid='{ gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1 }' :data-source='rule.transformList'-->
<!--              v-if='rule.transformList && rule.transformList.length>0'>-->
<!--        <a-list-item slot='renderItem' slot-scope='transform,index'>-->
<!--          <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px'>-->
<!--            <a-col :span='20'>-->
<!--              <a-descriptions :column='2'>-->
<!--                <a-descriptions-item label='类型'>-->
<!--                  {{ transformModeMap[transform.transformMode] }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item label='处理器'>-->
<!--                  {{ transform.workerNum }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item label='执行顺序'>-->
<!--                  {{ index + 1 }}-->
<!--                </a-descriptions-item>-->
<!--                &lt;!&ndash;                  <a-descriptions-item label='转换ID'>-->
<!--                                    {{ transform.transformRuntimeId }}-->
<!--                                  </a-descriptions-item>&ndash;&gt;-->
<!--                <a-descriptions-item label='SQL' v-if='transform.transformMode === "SQL"'>-->
<!--                  {{ transform.properties.sql }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item label='脚本' v-if='transform.transformMode === "SCRIPT"'>-->
<!--                  {{ extractScript(transform.properties.script, 80) }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item label='插件' v-if='transform.transformMode === "PLUGIN"'>-->
<!--                  {{ transform.properties.plugin.pluginName }}-->
<!--                </a-descriptions-item>-->
<!--              </a-descriptions>-->
<!--            </a-col>-->
<!--            <a-col :span='4' style='text-align: right;padding-right: 5px'>-->
<!--              <a-->
<!--                @click='showScript(transform.properties)'-->
<!--                v-if='transform.transformMode === "SCRIPT"'>查看脚本</a>-->
<!--              <a-divider type='vertical' v-if='transform.transformMode === "SCRIPT"' />-->
<!--              <a @click='lastData("transform",transform.transformRuntimeId)'>最近数据</a>-->
<!--            </a-col>-->
<!--          </a-row>-->

<!--        </a-list-item>-->
<!--      </a-list>-->
<!--    </a-card>-->


<!--    <a-card style='margin-bottom: 24px' :bordered='false' :body-style='{paddingBottom:"10px"}'>-->
<!--      <div class='title'>目标资源</div>-->
<!--      <a-list :grid='{ gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1 }' :data-source='rule.destResourceList'-->
<!--              v-if='rule.destResourceList && rule.destResourceList.length>0'>-->
<!--        <a-list-item slot='renderItem' slot-scope='resource,index'>-->
<!--          <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px'>-->
<!--            <a-col :span='20'>-->
<!--              <a-descriptions :column='2'>-->
<!--                <a-descriptions-item label='名称'>-->
<!--                  {{ resource.resourceName }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item label='资源ID'>-->
<!--                  {{ resource.resourceRuntimeId }}-->
<!--                </a-descriptions-item>-->
<!--                <a-descriptions-item v-for='(element,index) in getDetails(resource)' :key='index'-->
<!--                                     :label='element.name'>-->
<!--                  {{ element.value }}-->
<!--                </a-descriptions-item>-->
<!--              </a-descriptions>-->
<!--            </a-col>-->
<!--            <a-col :span='4' style='text-align: right;padding-right: 5px'>-->
<!--              <a v-if='resource.properties.points !== undefined' @click='pointConfig(resource)'>查看点位</a>-->
<!--              <a-divider type='vertical' v-if='resource.properties.points !== undefined' />-->
<!--              <a @click='lastData("dest",resource.resourceRuntimeId)'>最近数据</a>-->
<!--            </a-col>-->
<!--          </a-row>-->

<!--        </a-list-item>-->
<!--      </a-list>-->
<!--    </a-card>-->

<!--    <a-card :bordered='false' style='margin-bottom: 24px' v-show='rule.description'>-->
<!--      <a-row>-->
<!--        <a-col :span='22'>-->
<!--          <a-descriptions title='备注' :column='2'>-->
<!--            <a-descriptions-item> {{ rule.description ? rule.description : '无' }}</a-descriptions-item>-->
<!--          </a-descriptions>-->
<!--        </a-col>-->
<!--      </a-row>-->
<!--    </a-card>-->

<!--    <points-config-model ref='PointsConfigModel'></points-config-model>-->
<!--    <script-view-model ref='ScriptViewModel'></script-view-model>-->
<!--    <data-view-model ref='DataViewModel'></data-view-model>-->

<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import { extractScript } from '@/utils/util'-->
<!--import { getAction, postAction } from '@/api/manage'-->
<!--import { getResourceDetails, resourceTypeMap } from '@/config/resource.config'-->
<!--import { transformModeMap } from '@/config/transform.config'-->
<!--import { scriptLanguageMap } from '@/config/language.config'-->
<!--import ScriptViewModel from './modules/ScriptViewModel'-->
<!--import DataViewModel from './modules/DataViewModel'-->
<!--import PointsConfigModel from './points/PointsConfigModel'-->
<!--import RuntimeGraph from '@/views/rule/graph/RuntimeGraph'-->

<!--export default {-->
<!--  components: {-->
<!--    RuntimeGraph, ScriptViewModel, PointsConfigModel, DataViewModel-->
<!--  },-->
<!--  data() {-->
<!--    return {-->
<!--      url: {-->
<!--        start: '/api/rule/start',-->
<!--        stop: '/api/rule/stop',-->
<!--        runtime: '/api/runtime/info',-->
<!--        rule: '/api/rule/info',-->
<!--        reset: '/api/runtime/reset'-->
<!--      },-->
<!--      rule: {},-->
<!--      runtime: {},-->
<!--      transformModeMap: transformModeMap,-->
<!--      resourceTypeMap: resourceTypeMap,-->
<!--      scriptLanguageMap: scriptLanguageMap,-->
<!--      extractScript: extractScript,-->
<!--      varColumns: [-->
<!--        {-->
<!--          title: '变量名称',-->
<!--          dataIndex: 'name',-->
<!--          width: '30%'-->
<!--        },-->
<!--        {-->
<!--          title: '初始值',-->
<!--          dataIndex: 'initValue'-->
<!--        },-->
<!--        {-->
<!--          title: '当前值',-->
<!--          dataIndex: 'value'-->
<!--        }-->
<!--      ]-->
<!--    }-->
<!--  },-->
<!--  mounted() {-->
<!--    this.ruleId = this.$route.params.ruleId-->
<!--    this.getInfo()-->
<!--  },-->
<!--  methods: {-->
<!--    showScript(properties) {-->
<!--      this.$refs.ScriptViewModel.show(properties)-->
<!--    },-->
<!--    refresh() {-->
<!--      this.getInfo()-->
<!--      this.$message.success('刷新成功')-->
<!--    },-->
<!--    resetRuntime() {-->
<!--      this.$confirm({-->
<!--        title: '重置运行状态?',-->
<!--        content: '此操作将会清空运行统计数据',-->
<!--        onOk: () => {-->
<!--          getAction(this.url.reset, { ruleId: this.ruleId }).then(res => {-->
<!--            if (res.code === 200) {-->
<!--              this.getInfo()-->
<!--              this.$message.success('重置成功')-->
<!--            } else {-->
<!--              this.$message.error('重置失败')-->
<!--            }-->
<!--          })-->
<!--        }-->
<!--      })-->
<!--    },-->
<!--    getInfo() {-->
<!--      if (this.ruleId) {-->
<!--        getAction(this.url.rule, { ruleId: this.ruleId }).then(res => {-->
<!--          this.rule = res.data-->
<!--          if (this.rule) {-->
<!--            this.getRuntime()-->
<!--          }-->
<!--        })-->
<!--      }-->
<!--    },-->
<!--    getRuntime() {-->
<!--      getAction(this.url.runtime, { ruleId: this.ruleId }).then(res => {-->
<!--        this.runtime = res.data-->
<!--        if (!this.runtime) return-->
<!--        this.$refs.RuntimeGraph.init(this.rule, this.runtime)-->
<!--        this.countHandle()-->
<!--      })-->
<!--    },-->
<!--    handleStart() {-->
<!--      postAction(this.url.start, this.rule).then(res => {-->
<!--        if (res.code === 200) {-->
<!--          this.$message.success('启动成功')-->
<!--          this.getInfo()-->
<!--        } else {-->
<!--          this.$message.error('启动失败')-->
<!--        }-->
<!--      })-->
<!--    },-->
<!--    handleStop() {-->
<!--      this.$confirm({-->
<!--        title: '停止此规则?',-->
<!--        content: this.rule.ruleName,-->
<!--        onOk: () => {-->
<!--          postAction(this.url.stop, this.rule).then(res => {-->
<!--            if (res.code === 200) {-->
<!--              this.$message.success('停止成功')-->
<!--              this.getInfo()-->
<!--            } else {-->
<!--              this.$message.error('停止失败')-->
<!--            }-->
<!--          })-->
<!--        }-->
<!--      })-->
<!--    },-->
<!--    handleEdit() {-->
<!--      this.$router.push({ path: '/rule/info/' + this.rule.ruleId })-->
<!--    },-->
<!--    onClose() {-->
<!--      this.$router.push({ name: 'ruleList' })-->
<!--    },-->
<!--    getDetails(resource) {-->
<!--      return getResourceDetails(resource, 'rule')-->
<!--    },-->
<!--    pointConfig(resource) {-->
<!--      this.$refs.PointsConfigModel.show(resource.resourceType, resource.properties.points)-->
<!--    },-->
<!--    countHandle() {-->
<!--      let receiveSuccessCount = 0-->
<!--      let receiveFailCount = 0-->
<!--      let publishSuccessCount = 0-->
<!--      let publishFailCount = 0-->
<!--      let transformSuccessCount = 0-->
<!--      let transformFailCount = 0-->
<!--      let sourceRuntimes = Object.values(this.runtime.sourceRuntimeList)-->
<!--      for (let sourceRuntime of sourceRuntimes) {-->
<!--        receiveSuccessCount += sourceRuntime.successCount-->
<!--        receiveFailCount += sourceRuntime.failCount-->
<!--      }-->
<!--      let destRuntimes = Object.values(this.runtime.destRuntimeList)-->
<!--      for (let destRuntime of destRuntimes) {-->
<!--        publishSuccessCount += destRuntime.successCount-->
<!--        publishFailCount += destRuntime.failCount-->
<!--      }-->
<!--      let transformRuntimes = Object.values(this.runtime.transformRuntimeList)-->
<!--      for (let transformRuntime of transformRuntimes) {-->
<!--        transformSuccessCount += transformRuntime.successCount-->
<!--        transformFailCount += transformRuntime.failCount-->
<!--      }-->
<!--      this.runtime.receiveSuccessCount = receiveSuccessCount-->
<!--      this.runtime.receiveFailCount = receiveFailCount-->
<!--      this.runtime.publishSuccessCount = publishSuccessCount-->
<!--      this.runtime.publishFailCount = publishFailCount-->
<!--      this.runtime.transformSuccessCount = transformSuccessCount-->
<!--      this.runtime.transformFailCount = transformFailCount-->
<!--    },-->
<!--    lastData(mode, runtimeId) {-->
<!--      let dataList = []-->
<!--      if (mode === 'dest') {-->
<!--        dataList = this.runtime.destRuntimeList[runtimeId].runtimeDataList-->
<!--      } else if (mode === 'source') {-->
<!--        dataList = this.runtime.sourceRuntimeList[runtimeId].runtimeDataList-->
<!--      } else if (mode === 'transform') {-->
<!--        dataList = this.runtime.transformRuntimeList[runtimeId].runtimeDataList-->
<!--      }-->
<!--      this.$refs.DataViewModel.show(dataList)-->
<!--    }-->
<!--  }-->

<!--}-->
<!--</script>-->

<!--<style lang='less' scoped>-->
<!--.title {-->
<!--  font-size: 16px;-->
<!--  font-weight: bold;-->
<!--  color: rgba(0, 0, 0, 0.85);-->
<!--  margin-bottom: 16px;-->
<!--}-->
<!--</style>-->

<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span="properties.mode==='SENTINEL'?12:24">
        <a-form-model-item label='模式' prop='mode'>
          <a-select v-model='properties.mode' placeholder='请选择模式'>
            <a-select-option value='STANDALONE'>单节点模式</a-select-option>
            <a-select-option value='CLUSTER'>Cluster模式</a-select-option>
            <a-select-option value='SENTINEL'>Sentinel模式</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="properties.mode==='SENTINEL'">
        <a-form-model-item label='主节点名称' prop='masterName'>
          <a-input v-model='properties.masterName' placeholder='请输入主节点名称' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if="properties.mode==='CLUSTER' || properties.mode==='SENTINEL'">
        <a-form-model-item label='服务节点' prop='nodes'>
          <a-input v-model='properties.nodes' placeholder='请输入服务节点' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="properties.mode==='STANDALONE'">
        <a-form-model-item label='IP' prop='ip'>
          <a-input v-model='properties.ip' placeholder='请输入IP' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="properties.mode==='STANDALONE'">
        <a-form-model-item label='端口' prop='port'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="properties.mode==='STANDALONE' || properties.mode==='SENTINEL'">
        <a-form-model-item label='数据库' prop='database'>
          <a-input v-model='properties.database' placeholder='请输入数据库' />
        </a-form-model-item>
      </a-col>
      <a-col :span="properties.mode==='CLUSTER'?24:12">
        <a-form-model-item label='密码'>
          <a-input-password v-model='properties.password' placeholder='请输入密码' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接池大小'>
          <a-input v-model='properties.maxTotal' placeholder='请输入连接池大小' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接超时'>
          <a-input v-model='properties.timeout' placeholder='请输入连接超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='最大空闲'>
          <a-input v-model='properties.maxIdle' placeholder='请输入最大空闲数' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='最小空闲'>
          <a-input v-model='properties.minIdle' placeholder='请输入最小空闲数' />
        </a-form-model-item>
      </a-col>
    </a-row>
  </a-form-model>

</template>

<script>

export default {
  data() {
    return {
      properties: {
        mode: 'STANDALONE',
        ip: '127.0.0.1',
        port: '6379',
        database: 0,
        timeout: 6000,
        maxIdle: 8,
        minIdle: 4,
        maxTotal: 8
      },
      rules: {
        mode: [{ required: true, message: '请选择模式', trigger: 'change' }],
        masterName: [{ required: true, message: '请输主节点名称', trigger: 'blur' }],
        ip: [{ required: true, message: '请输入IP', trigger: 'blur' }],
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        nodes: [{ required: true, message: '请输入服务节点', trigger: 'blur' }],
        database: [{ required: true, message: '请输入数据库', trigger: 'blur' }]
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
    },
    get(callback) {
      if (this.properties.mode === 'STANDALONE') {
        delete this.properties.nodes
        delete this.properties.masterName
      }
      if (this.properties.mode === 'CLUSTER') {
        delete this.properties.ip
        delete this.properties.port
        delete this.properties.masterName
        delete this.properties.database
      }
      if (this.properties.mode === 'SENTINEL') {
        delete this.properties.ip
        delete this.properties.port
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

<style scoped>
</style>

<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='24'>
        <a-form-model-item label='URL' prop='url'>
          <a-input v-model='properties.url' placeholder='请输入URL [ tcp://ip:port 或 ssl://ip:port ]' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='MQTT版本' prop='version'>
          <a-select v-model='properties.version' placeholder='请选择MQTT版本'>
            <a-select-option :value='3'>3.1</a-select-option>
            <a-select-option :value='4'>3.1.1</a-select-option>
            <a-select-option :value='5'>5.0</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='SSL/TLS' prop='ssl'>
          <bool-select v-model='properties.ssl' placeholder='请选择是否启用SSL' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='账户'>
          <a-input v-model='properties.username' placeholder='请输入账户' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='密码'>
          <a-input-password v-model='properties.password' placeholder='请输入密码' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接超时(秒)'>
          <a-input v-model='properties.connectionTimeout' placeholder='请输入连接超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='保活间隔(秒)'>
          <a-input v-model='properties.keepAliveInterval' placeholder='请输入保活间隔' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='自动重连'>
          <bool-select v-model='properties.autoReconnect' placeholder='请选择是否自动重连' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version!==5'>
        <a-form-model-item label='Clean Session'>
          <bool-select v-model='properties.cleanSession' placeholder='Clean Session' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version===5'>
        <a-form-model-item label='Clean Start'>
          <bool-select v-model='properties.cleanStart' placeholder='Clean Start'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version===5'>
        <a-form-model-item label='会话过期时间(秒)'>
          <a-input v-model='properties.sessionExpiryInterval' placeholder='请输入会话过期时间' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version===5'>
        <a-form-model-item label='最大数据包大小'>
          <a-input v-model='properties.maximumPacketSize' placeholder='请输入最大数据包大小' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version===5'>
        <a-form-model-item label='接收最大数值'>
          <a-input v-model='properties.receiveMaximum' placeholder='请输入接收最大数值' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version===5'>
        <a-form-model-item label='主题别名最大值'>
          <a-input v-model='properties.topicAliasMaximum' placeholder='请输入主题别名最大值' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version===5'>
        <a-form-model-item label='请求失败信息'>
          <bool-select v-model='properties.requestProblemInfo' placeholder='请选择是否请求失败信息'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='properties.version===5'>
        <a-form-model-item label='请求响应信息'>
          <bool-select v-model='properties.requestResponseInfo' placeholder='请选择是否请求响应信息'/>
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
        url: 'tcp://127.0.0.1:1883',
        ssl: false,
        version: 3,
        connectionTimeout: 10,
        keepAliveInterval: 30,
        autoReconnect: true,
        cleanSession: true,
        cleanStart: true,
        receiveMaximum: 65535,
        topicAliasMaximum: 0,
        requestProblemInfo: true,
        requestResponseInfo: false
      },
      rules: {
        url: [{ required: true, message: '请输入URL', trigger: 'blur' }]
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
    },
    get(callback) {
      if (this.properties.version === 5) {
        delete this.properties.cleanSession
      } else {
        delete this.properties.cleanStart
        delete this.properties.sessionExpiryInterval
        delete this.properties.maximumPacketSize
        delete this.properties.receiveMaximum
        delete this.properties.topicAliasMaximum
        delete this.properties.requestProblemInfo
        delete this.properties.requestResponseInfo
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

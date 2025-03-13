<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='12'>
        <a-form-model-item label='IP' prop='ip'>
          <a-input v-model='properties.ip' placeholder='请输入IP' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='端口' prop='port'>
          <a-input-number v-model='properties.port' placeholder='请输入端口' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='用户名' prop='username'>
          <a-input v-model='properties.username' placeholder='请输入用户名' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='密码' prop='password'>
          <a-input v-model='properties.password' placeholder='请输入密码' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接超时(毫秒)'>
          <a-input v-model='properties.connectTimeout' placeholder='请输入连接超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='请求超时(毫秒)'>
          <a-input v-model='properties.requestTimeout' placeholder='请输入请求超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='保活间隔(毫秒)'>
          <a-input v-model='properties.keepAliveInterval' placeholder='请输入保活间隔' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='保活超时(毫秒)'>
          <a-input v-model='properties.keepAliveTimeout' placeholder='请输入保活超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接池大小' prop='maxTotal'>
          <a-input-number v-model='properties.maxTotal' placeholder='请输入连接池大小' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='最大空闲' prop='maxIdle'>
          <a-input-number v-model='properties.maxIdle' placeholder='请输入最大空闲数' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='最小空闲' prop='minIdle'>
          <a-input-number v-model='properties.minIdle' placeholder='请输入最小空闲数' style='width: 100%' />
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
        connectTimeout: 5000,
        requestTimeout: 60000,
        keepAliveInterval: 5000,
        keepAliveTimeout: 5000,
        maxTotal: 8,
        maxIdle: 8,
        minIdle: 4
      },
      rules: {
        ip: [{ required: true, message: '请输入IP', trigger: 'blur' }],
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }]
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
    },
    get(callback) {
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

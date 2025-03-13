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
          <a-input-number v-model='properties.port' placeholder='请输入端口' style='width: 100%'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='从站地址' prop='salve'>
          <a-input-number v-model='properties.salve' placeholder='请输入从站地址' style='width: 100%'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='超时时间(ms)' prop='timeout'>
          <a-input-number v-model='properties.timeout' placeholder='请输入超时时间' style='width: 100%'/>
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
        ip: "127.0.0.1",
        port: 502,
        salve: 1,
        timeout: 1000
      },
      rules: {
        ip: [{ required: true, message: '请输入IP', trigger: 'blur' }],
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        salve: [{ required: true, message: '请输入从站地址', trigger: 'blur' }],
        timeout: [{ required: true, message: '请输入超时时间', trigger: 'blur' }],
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

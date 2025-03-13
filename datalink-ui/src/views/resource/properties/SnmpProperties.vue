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
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='版本' prop='version'>
          <a-select v-model='properties.version' placeholder='请选择版本'>
            <a-select-option :value='0'>v1</a-select-option>
            <a-select-option :value='1'>v2c</a-select-option>
            <!--            <a-select-option :value='3'>v3</a-select-option>-->
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='团体名' prop='community'>
          <a-input v-model='properties.community' placeholder='请输入团体名' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='最大重连次数' prop='retries'>
          <a-input-number v-model='properties.retries' placeholder='请输入最大重连次数' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='超时时间' prop='timeout'>
          <a-input-number v-model='properties.timeout' placeholder='请输入超时时间' style='width: 100%' />
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
        community: 'public',
        version: 1,
        retries: 3,
        timeout: 3000
      },
      rules: {
        ip: [{ required: true, message: '请输入IP', trigger: 'blur' }],
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        version: [{ required: true, message: '请选择版本', trigger: 'blur' }],
        community: [{ required: true, message: '请输入团体名', trigger: 'blur' }]
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

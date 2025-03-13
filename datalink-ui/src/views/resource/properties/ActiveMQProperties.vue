<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='24'>
        <a-form-model-item label='Url' prop='url'>
          <a-input v-model='properties.url' placeholder='请输入Url' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='账户' prop='username'>
          <a-input v-model='properties.username' placeholder='请输入账户' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='密码' prop='password'>
          <a-input-password v-model='properties.password' placeholder='请输入密码' />
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
        url: 'tcp://127.0.0.1:61616'
      },
      rules: {
        url: [{ required: true, message: '请输入Url', trigger: 'blur' }]
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

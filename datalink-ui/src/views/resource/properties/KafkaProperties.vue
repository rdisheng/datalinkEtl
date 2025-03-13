<template>
  <a-form-model layout="vertical" :model="properties" ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='24'>
        <a-form-model-item label='URL' prop='url'>
          <a-input v-model='properties.url' placeholder='请输入URL' />
        </a-form-model-item>
      </a-col>
    </a-row>
  </a-form-model>

</template>

<script>

export default {
  data() {
    return {
      properties: {},
      rules: {
        url: [{ required: true, message: '请输入URL', trigger: 'blur' }],
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

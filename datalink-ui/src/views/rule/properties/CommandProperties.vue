<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='命令' prop='command'>
          <a-input v-model='properties.command' placeholder='请输入命令' />
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

export default {
  components: {},
  data() {
    return {
      properties: {},
      rules: {
        command: [{ required: true, message: '请输入命令', trigger: 'change' }]
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

<style>


</style>

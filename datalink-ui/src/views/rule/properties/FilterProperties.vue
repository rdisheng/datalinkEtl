<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='过滤条件' prop='condition'>
          <a-input v-model='properties.condition' placeholder='请输入过滤条件表达式' />
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
        condition: [{ required: true, message: '请输入条件表达式', trigger: 'change' }]
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

<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='文件路径' prop='path'>
          <a-input v-model='properties.path' placeholder='请输入文件路径' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='监听延迟(ms)' prop='delay'>
          <a-input-number v-model='properties.delay' placeholder='请输入监听延迟' style='width: 100%' />
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
      properties: {
        delay: 1000
      },
      rules: {
        path: [{ required: true, message: '请输入文件路径', trigger: 'blur' }],
        delay: [{ required: true, message: '请输入监听延迟', trigger: 'blur' }]
      }
    }
  },
  props: {
    param: {
      type: Object,
      default: {}
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

<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='打包数量' prop='dataCount'>
          <a-input-number v-model='properties.dataCount' placeholder='请输入打包数量' style='width: 100%'/>
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
        dataCount: [{ required: true, message: '请输入打包数量', trigger: 'change' }]
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

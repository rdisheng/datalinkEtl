<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='端口' prop='port'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' :key='index' v-for='(path,index) in properties.paths'>
        <a-form-model-item :label='"路径" + (index+1)' prop='paths'>
          <a-input-search v-model='properties.paths[index]' placeholder='请输入路径'
                          @search='deletePath(index)'>
            <a-button slot='enterButton'>删除</a-button>
          </a-input-search>
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-button icon='plus' @click='addPath'>添加路径</a-button>
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
        paths: ['']
      },
      rules: {
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        paths: [{ required: true, validator: this.checkValue, message: '请输入路径', trigger: 'change' }]
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
          return callback(true, that.properties, that.properties.paths.length)
        } else {
          return callback(false)
        }
      })
    },
    addPath() {
      this.properties.paths.push('')
    },
    deletePath(index) {
      if (this.properties.paths.length - 1 < 1) {
        this.$message.warn('至少一个路径')
        return
      }
      this.properties.paths.splice(index, 1)
    },
    checkValue(rule, value, callback) {
      if (!value || value.indexOf('') >= 0) {
        return callback(new Error())
      } else {
        return callback()
      }
    }
  }
}
</script>

<style>


</style>

<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24' :key='index' v-for='(condition,index) in properties.conditions'>
        <a-form-model-item :label='"出口" + (index+1)' prop='conditions'>
          <a-input-search v-model='properties.conditions[index]' placeholder='请输入条件表达式'
                          @search='deleteCondition(index)'>
            <a-button slot='enterButton'>删除</a-button>
          </a-input-search>
        </a-form-model-item>
      </a-col>
    </a-form-model>
    <a-col :span='24'>
      <a-button icon='plus' @click='addCondition'>添加条件</a-button>
    </a-col>
  </a-row>
</template>

<script>

export default {
  components: {},
  data() {
    return {
      properties: {
        conditions: ['', '']
      },
      rules: {
        conditions: [{ required: true, validator: this.checkValue, message: '请输入条件表达式', trigger: 'change' }]
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
          return callback(true, that.properties, that.properties.conditions.length)
        } else {
          return callback(false)
        }
      })
    },
    addCondition() {
      this.properties.conditions.push('')
    },
    deleteCondition(index) {
      if (this.properties.conditions.length - 1 < 2) {
        this.$message.warn('至少两个出口')
        return
      }
      this.properties.conditions.splice(index, 1)
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

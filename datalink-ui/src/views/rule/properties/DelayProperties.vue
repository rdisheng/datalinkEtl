<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='延迟时长' prop='delay'>
          <a-input v-model='properties.delay' placeholder='请输入延迟时长' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='时间单位' prop='delayUnit'>
          <a-select v-model='properties.delayUnit' placeholder='请选择时间单位'>
            <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>
import { timeUnitList } from '@/config/time.config'

export default {
  components: {},
  data() {
    return {
      timeUnitList,
      properties: {
        delayUnit: 'SECONDS',
      },
      rules: {
        delay: [{ required: true, message: '请输入延迟时长', trigger: 'blur' }],
        delayUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
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

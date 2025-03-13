<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='输出数量' prop='limit'>
          <a-input v-model='properties.limit' placeholder='请输入输出数量' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='输出间隔' prop='interval'>
          <a-input v-model='properties.interval' placeholder='请输入输出间隔时长' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='时间单位' prop='intervalUnit'>
          <a-select v-model='properties.intervalUnit' placeholder='请选择时间单位'>
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
        intervalUnit: 'SECONDS',
      },
      rules: {
        limit: [{ required: true, message: '请输入输出数量', trigger: 'blur' }],
        interval: [{ required: true, message: '请输入输出间隔', trigger: 'blur' }],
        intervalUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
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

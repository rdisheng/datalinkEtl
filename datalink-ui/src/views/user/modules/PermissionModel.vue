<template>

  <a-modal
    title='权限'
    :width='600'
    :visible='visible'
    @cancel='onClose'
    :destroyOnClose='true'
  >

    <a-checkbox-group v-model='checkedList' style='width: 100%'>
      <a-row :gutter='24'>
        <a-col :span='6' v-for='(item,index) in dataList' :key='index' style='margin: 10px 0'>
          <a-checkbox :value='item.value' :disabled='item.value === "dashboard"'>
            {{ item.name }}
          </a-checkbox>
        </a-col>
      </a-row>
    </a-checkbox-group>

    <div class='bottom-button'>
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 取消</a-button>
      <a-button type='primary' @click='handleOk'> 确定</a-button>
    </div>
  </a-modal>
</template>
<script>
import { asyncRouterMap } from '@/config/router.config'
import {  putAction } from '@/api/manage'

export default {
  data() {
    return {
      record: {},
      dataList: [],
      checkedList: [],
      visible: false
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    open(record) {
      this.record = record
      this.checkedList = Object.assign([], record.permissions)
      this.visible = true
    },
    onClose() {
      this.record = {}
      this.checkedList = []
      this.visible = false
    },
    handleOk() {
      if (!this.checkedList || this.checkedList.length < 1) {
        this.$message.error('至少选择一项')
        return
      }
      this.record.permissions = this.checkedList
      putAction('/api/user/update', this.record).then(res => {
        if (res.code === 200) {
          this.$message.success('保存成功')
        } else {
          this.$message.error(res.message)
        }
      }).finally(() => {
        this.onClose()
      })
    },
    getList() {
      this.dataList = []
      for (let menu of asyncRouterMap[0].children) {
        if (menu.meta && menu.meta.permission) {
          this.dataList.push({
            name: menu.meta.title,
            value: menu.meta.permission
          })
        }
      }
    }
  }
}
</script>

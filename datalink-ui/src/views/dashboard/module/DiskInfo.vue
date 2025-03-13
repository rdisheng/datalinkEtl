<template>
  <a-skeleton active :loading='loading' :paragraph='{ rows: 17 }'>

    <a-table
      rowKey='name'
      size='middle'
      :columns='columns'
      :dataSource='dataSource'
      :pagination='false'
      :loading='tableLoading'
      style='margin-top: 20px'
    >
      <template slot='value' slot-scope='text'>
        {{ Math.round(text / 1024 / 1024) }} MB
      </template>
      <template slot='progress' slot-scope='text,record'>
        <a-progress :percent='Math.round(((record.total - record.free)/record.total)*100)' size='small' style='width: 50%'/>
      </template>

    </a-table>
  </a-skeleton>
</template>
<script>
import moment from 'moment'
import { getAction } from '@/api/manage'

moment.locale('zh-cn')

export default {
  data() {
    return {
      loading: true,
      tableLoading: true,
      columns: [
        {
          title: '磁盘名称',
          width: '20%',
          dataIndex: 'name'
        },
        {
          title: '使用情况',
          width: '40%',
          dataIndex: 'progress',
          scopedSlots: { customRender: 'progress' }
        },
        {
          title: '总空间',
          width: '20%',
          dataIndex: 'total',
          scopedSlots: { customRender: 'value' }
        },
        {
          title: '可用空间',
          width: '20%',
          dataIndex: 'free',
          scopedSlots: { customRender: 'value' }
        }
      ],
      dataSource: []
    }
  },
  mounted() {
    this.loadDiskInfo()
  },
  methods: {
    loadDiskInfo() {
      this.tableLoading = true
      getAction('/api/system/disk', {}).then(res => {
        this.dataSource = res.data
      }).catch(e => {
        this.$message.error('获取磁盘信息失败')
      })
        .finally(() => {
          this.loading = false
          this.tableLoading = false
        })
    }
  }
}
</script>
<style></style>

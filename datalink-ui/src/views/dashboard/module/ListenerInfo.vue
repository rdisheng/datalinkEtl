<template>
  <a-skeleton active :loading='loading' :paragraph='{ rows: 17 }'>
    <a-table
      ref='table'
      rowKey='port'
      :columns='columns'
      :data-source='dataSource'
      :pagination='false'
      style="margin-top: 20px"
    >

        <span slot='serial' slot-scope='text, record, index'>
          {{ index + 1 }}
        </span>

      <span slot='port' slot-scope='text, record, index'>
          {{ record.ip }}:{{ text }}
        </span>

      <span slot='desc' slot-scope='text, record, index'>
          {{ text ? text : '-' }}
        </span>

    </a-table>
  </a-skeleton>
</template>

<script>

import { postAction } from '@/api/manage'

export default {
  name: 'ListenerList',
  components: {},
  data() {
    return {
      columns: [
        /*        {
                  title: '#',
                  scopedSlots: { customRender: 'serial' }
                },*/
        {
          title: '监听地址',
          dataIndex: 'port',
          width: '30%',
          scopedSlots: { customRender: 'port' }
        },
        {
          title: '类型',
          dataIndex: 'type',
          width: '30%'
        },
        {
          title: '说明',
          dataIndex: 'desc',
          scopedSlots: { customRender: 'desc' }
        }
        /*   {
             title: '操作',
             dataIndex: 'action',
             width: '150px',
             scopedSlots: { customRender: 'action' }
           }*/
      ],
      dataSource: [],
      loading: true,
      queryParam: {}
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      postAction('/api/listener/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    }
  }
}
</script>

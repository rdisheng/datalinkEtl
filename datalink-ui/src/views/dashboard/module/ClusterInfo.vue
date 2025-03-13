<template>
  <a-skeleton active :loading='loading' :paragraph='{ rows: 17 }'>
      <a-table
        ref='table'
        rowKey='memberName'
        :columns='columns'
        :data-source='dataSource'
        :pagination='false'
        style="margin-top: 20px"
      >

        <span slot='memberName' slot-scope='text, record, index'>
            {{ text }}   <a-tag v-if='record.local'>当前节点</a-tag>
        </span>

        <span slot='memberState' slot-scope='text, record, index'>
           <a-tag color="#87d068" v-if='text==="UP"'>UP</a-tag>
           <a-tag color="#f50" v-if='text==="DOWN"'>DOWN</a-tag>
        </span>

      </a-table>

  </a-skeleton>
</template>

<script>

import { postAction } from '@/api/manage'

export default {
  name: 'ClusterList',
  components: {},
  data() {
    return {
      columns: [
        {
          title: '节点',
          dataIndex: 'memberName',
          scopedSlots: { customRender: 'memberName' }
        },
        {
          title: '状态',
          dataIndex: 'memberState',
          scopedSlots: { customRender: 'memberState' }
        },
        {
          title: '更新时间',
          dataIndex: 'updateTime',
        }
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
      this.loading = true
      postAction('/api/cluster/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
  }
}
</script>

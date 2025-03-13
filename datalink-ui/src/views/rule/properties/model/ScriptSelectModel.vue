<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='脚本'
    :width='800'
    :visible='visible'
    @cancel='onClose'
    :footer='null'
    :bodyStyle='{padding: 0}'
  >
    <div style='min-height: 400px'>

      <a-table :columns='columns' :data-source='scriptList' size='small' :pagination='false'>
          <span slot='action' slot-scope='text,record'>
            <a @click='select(record)'>选择</a>
          </span>
      </a-table>

    </div>
  </a-modal>
</template>

<script>
import { getAction, postAction } from '@/api/manage'

export default {
  name: 'ScriptSelectModel',
  components: {},
  data() {
    return {
      visible: false,
      confirmLoading: false,
      url: {
        info: '/api/script/info',
        list: '/api/script/list'
      },
      columns: [
        {
          title: '名称',
          align: 'center',
          dataIndex: 'scriptName'
        },
        {
          title: '最后修改',
          align: 'center',
          dataIndex: 'updateTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          scopedSlots: { customRender: 'action' }
        }
      ],
      scriptList: []
    }
  },
  methods: {
    show(language) {
      postAction(this.url.list, { language: language }).then(res => {
        if (res.code === 200) {
          this.scriptList = res.data
        }
      })
      this.visible = true
    },
    onClose() {
      this.visible = false
    },
    select(record) {
      getAction(this.url.info, { scriptId: record.scriptId }).then(res => {
        this.$emit('select', res.data.scriptContent)
        this.visible = false
      })
    }

  }
}
</script>

<style scoped>
</style>

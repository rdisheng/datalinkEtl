<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='6' :sm='24'>
              <a-form-item label='脚本名称'>
                <a-input v-model='queryParam.scriptName' placeholder='请输入脚本名称' />
              </a-form-item>
            </a-col>
            <a-col :md='6' :sm='24'>
              <a-form-item label='脚本语言'>
                <a-select v-model='queryParam.scriptLanguage' placeholder='请选择脚本语言'>
                  <a-select-option v-for='(item,index) in scriptLanguageList' :key='index' :value='item.value'>{{item.name}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md='8' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='4' :sm='24' style='text-align: right'>
              <a-dropdown>
                <a-menu slot='overlay' @click='handleAdd'>
                  <a-menu-item :key='item.value' v-for='item in scriptLanguageList'> {{ item.name }}脚本</a-menu-item>
                </a-menu>
                <a-button type='primary'> 新建脚本
                  <a-icon type='down' />
                </a-button>
              </a-dropdown>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>
    <a-card :body-style='{minHeight:"500px"}' :bordered='false'>
      <a-table
        ref='table'
        rowKey='scriptId'
        :columns='columns'
        :data-source='dataSource'
        :pagination='false'
        :loading='loading'
      >
        <span slot='serial' slot-scope='text, record, index'>
          {{ index + 1 }}
        </span>

        <span slot='scriptLanguage' slot-scope='text, record, index'>
          {{ scriptLanguageMap[text].name }}
        </span>

        <span slot='description' slot-scope='text, record, index'>
          {{ text ? text : '-' }}
        </span>

        <span slot='action' slot-scope='text, record'>
          <template>
            <a @click='handleEdit(record)'>编辑</a>
            <a-divider type='vertical' />
            <a-popconfirm v-if='dataSource.length' title='删除此脚本?' @confirm='() => handleDelete(record)'>
              <a href='javascript:;'>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </a-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>

import { postAction } from '@/api/manage'
import { scriptLanguageMap, scriptLanguageList } from '@/config/language.config'

export default {
  name: 'ScriptList',
  components: {},
  data() {
    return {
      scriptLanguageMap: scriptLanguageMap,
      scriptLanguageList: scriptLanguageList,
      columns: [
        {
          title: '脚本名称',
          dataIndex: 'scriptName'
        },
        {
          title: '脚本语言',
          dataIndex: 'scriptLanguage',
          scopedSlots: { customRender: 'scriptLanguage' }
        },
        {
          title: '说明',
          dataIndex: 'description',
          scopedSlots: { customRender: 'description' }
        },
        {
          title: '最后修改',
          dataIndex: 'updateTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '150px',
          scopedSlots: { customRender: 'action' }
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
      postAction('/api/script/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    handleDelete(record) {
      postAction('/api/script/remove', { scriptId: record.scriptId }).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.error('删除失败')
        }
      })
    },
    handleAdd(event) {
      this.$router.push({ path: '/script/info/new-' + event.key })
    },
    handleEdit(record) {
      this.$router.push({ path: '/script/info/' + record.scriptId })
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>

<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='用户名'>
                <a-input v-model='queryParam.username' placeholder='请输入用户名' />
              </a-form-item>
            </a-col>
            <a-col :md='14' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='3' :sm='24' style='text-align: right'>
              <a-button type='primary' @click='handleAdd()' icon='plus'>新建用户</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>
    <a-card :body-style='{minHeight:"500px"}' :bordered='false'>
      <a-table
        ref='table'
        rowKey='username'
        :columns='columns'
        :data-source='dataSource'
        :pagination='false'
        :loading='loading'
      >
        <span slot='serial' slot-scope='text, record, index'>
          {{ index + 1 }}
        </span>

        <span slot='description' slot-scope='text, record, index'>
          {{ text ? text : '-' }}
        </span>

        <span slot='action' slot-scope='text, record'>
          <template>
            <a @click='handleEdit(record)'>编辑</a>
            <a-divider type='vertical' />
             <a @click='handlePassword(record)'>密码</a>
            <a-divider type='vertical' />
            <a @click='handlePermission(record)' :disabled='record.system'>权限</a>
            <a-divider type='vertical' />
            <a-popconfirm v-if='dataSource.length' title='删除此用户?' @confirm='() => handleDelete(record)'>
              <a href='javascript:;' :disabled='record.system'>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </a-table>
    </a-card>

    <user-model ref='UserModel' @ok='loadData'></user-model>
    <password-model ref='PasswordModel'></password-model>
    <permission-model ref='PermissionModel'></permission-model>

  </page-header-wrapper>
</template>

<script>
import UserModel from './modules/UserModel'
import PasswordModel from './modules/PasswordModel'
import PermissionModel from './modules/PermissionModel'
import { postAction } from '@/api/manage'

export default {
  name: 'UserList',
  components: { PermissionModel, PasswordModel, UserModel },
  data() {
    return {
      columns: [
        {
          title: '用户名',
          dataIndex: 'username'
        },
        {
          title: '备注',
          dataIndex: 'description',
          scopedSlots: { customRender: 'description' }
        },
        {
          title: '创建时间',
          dataIndex: 'createTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '250px',
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
      postAction('/api/user/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    handleDelete(record) {
      postAction('/api/user/remove', { username: record.username }).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.error('删除失败')
        }
      })
    },
    handleAdd() {
      this.$refs.UserModel.add()
    },
    handleEdit(record) {
      this.$refs.UserModel.edit(record)
    },
    handlePassword(record) {
      this.$refs.PasswordModel.open(record.username)
    },
    handlePermission(record) {
      this.$refs.PermissionModel.open(record)
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>

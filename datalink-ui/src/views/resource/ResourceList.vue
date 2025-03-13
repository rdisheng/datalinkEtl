<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='6' :sm='24'>
              <a-form-item label='资源名称'>
                <a-input v-model='queryParam.resourceName' placeholder='请输入资源名称' />
              </a-form-item>
            </a-col>
            <a-col :md='6' :sm='24'>
              <a-form-item label='资源类型'>
                <a-select v-model='queryParam.resourceType' placeholder='请选择资源类型' show-search>
                  <a-select-opt-group v-for='(group,groupIndex) in resourceTypeAllList' :key='groupIndex'
                                      :label='group.group'>
                    <a-select-option v-for='(item,itemIndex) in group.list' :value='item.code' :key='itemIndex'>
                      {{ item.name
                      }}
                    </a-select-option>
                  </a-select-opt-group>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md='6' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='6' :sm='24' style='text-align: right'>
              <a-button type='primary' @click='handleAdd()' icon='plus'>新建资源</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>

    <a-card :body-style='{minHeight:"500px"}' :bordered='false'>

      <a-table
        ref='table'
        rowKey='resourceId'
        :columns='columns'
        :data-source='dataSource'
        :pagination='false'
        :loading='loading'
      >
        <span slot='serial' slot-scope='text, item, index'>
          {{ index + 1 }}
        </span>

        <span slot='resourceType' slot-scope='text, item, index'>
        {{ resourceTypeMap[item.resourceType] }}
        </span>

        <span slot='details' slot-scope='text, item, index'>
          {{ getDetails(item).value }}
        </span>

        <span slot='action' slot-scope='text, item'>
              <a @click='handleEdit(item)' :disabled='item.enable'>配置</a>
             <a-divider type='vertical' />
                <a @click='handleDelete(item)'>删除</a>
        </span>
      </a-table>
    </a-card>


    <resource-model ref='ResourceModel' @ok='loadData'></resource-model>

  </page-header-wrapper>
</template>

<script>
import { postAction } from '@/api/manage'
import ResourceModel from './modules/ResourceModel'
import { resourceTypeMap, resourceTypeAllList, getResourceDetails } from '@/config/resource.config'

export default {
  name: 'ResourceList',
  components: {
    ResourceModel
  },
  data() {
    return {
      loading: true,
      dataSource: [],
      columns: [
        // {
        //   title: '#',
        //   scopedSlots: { customRender: 'serial' }
        // },
        {
          title: '资源名称',
          dataIndex: 'resourceName',
        },
        {
          title: '资源类型',
          dataIndex: 'resourceType',
          scopedSlots: { customRender: 'resourceType' }
        },
        {
          title: '详情',
          dataIndex: 'details',
          scopedSlots: { customRender: 'details' }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      queryParam: {},
      url: {
        list: '/api/resource/list',
        remove: '/api/resource/remove',
        update: '/api/resource/update'
      },
      resourceTypeMap: resourceTypeMap,
      resourceTypeAllList: resourceTypeAllList
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    getDetails(resource) {
      return getResourceDetails(resource, 'resource')
    },
    handleAdd() {
      this.$refs.ResourceModel.add()
    },
    handleEdit(record) {
      this.$refs.ResourceModel.edit(record)
    },
    loadData() {
      this.loading = true
      postAction(this.url.list, this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    handleDelete(item) {
      this.$confirm({
        title: '删除此资源?',
        content: item.resourceName,
        okType: 'danger',
        onOk: () => {
          postAction(this.url.remove, item).then(res => {
            if (res.code === 200) {
              this.$message.success('删除成功')
              this.loadData()
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>

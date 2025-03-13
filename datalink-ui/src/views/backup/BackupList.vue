<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='备份名称'>
                <a-input v-model='queryParam.backupName' placeholder='请输入备份名称' />
              </a-form-item>
            </a-col>
            <a-col :md='11' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='6' :sm='24' style='text-align: right'>
              <a-space size='middle'>
                <a-upload
                  :showUploadList='false'
                  :beforeUpload='(file) => checkFile(file,50,["json"])'
                  @change='handlerUpload'
                  :customRequest='customRequest'
                >
                  <a-button>
                    <a-icon type='upload' />
                    上传并恢复备份
                  </a-button>
                </a-upload>
                <a-button type='primary' @click='handleAdd()' icon='plus'>创建备份</a-button>
              </a-space>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>
    <a-card :body-style='{minHeight:"500px"}' :bordered='false'>
      <a-table
        ref='table'
        rowKey='backupId'
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
            <a @click='recover(record)'>恢复</a>
            <a-divider type='vertical' />
            <a @click='download(record)'>下载</a>
            <a-divider type='vertical' />
            <a-popconfirm v-if='dataSource.length' title='删除此备份?' @confirm='() => handleDelete(record)'>
              <a href='javascript:;'>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </a-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { checkFile } from '@/utils/util'
import { postAction, downFile, getAction, uploadAction } from '@/api/manage'

export default {
  name: 'BackupList',
  components: {},
  data() {
    return {
      columns: [
   /*     {
          title: '#',
          scopedSlots: { customRender: 'serial' }
        },*/
        {
          title: '名称',
          dataIndex: 'backupName'
        },
        // {
        //   title: '说明',
        //   dataIndex: 'description',
        //   scopedSlots: { customRender: 'description' }
        // },
        {
          title: '时间',
          dataIndex: 'createTime'
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
    checkFile,
    loadData() {
      this.loading = true
      postAction('/api/backup/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    handleDelete(record) {
      postAction('/api/backup/remove', record).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.error('删除失败')
        }
      })
    },
    handleAdd() {
      postAction('/api/backup/create', {}).then(res => {
        if (res.code === 200) {
          this.$message.success('创建成功')
          this.loadData()
        } else {
          this.$message.error('创建失败')
        }
      })
    },
    download(record) {
      downFile('/api/backup/download', { backupName: record.backupName }).then(res => {
        const blob = new Blob([res])
        let downloadElement = document.createElement('a')
        let href = window.URL.createObjectURL(blob)
        downloadElement.href = href
        downloadElement.download = decodeURIComponent(record.backupName)
        document.body.appendChild(downloadElement)
        downloadElement.click()
        document.body.removeChild(downloadElement)
        window.URL.revokeObjectURL(href)
      })
    },
    recover(record) {
      this.$confirm({
        title: '恢复此备份数据?',
        content: "此操作将导致现有数据丢失",
        onOk: () => {
          getAction('/api/backup/recover', { backupName: record.backupName }).then(res => {
            if (res.code === 200) {
              this.$message.success('恢复成功')
              this.loadData()
            } else {
              this.$message.error('恢复失败')
            }
          })
        }
      })
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    },
    handlerUpload(info) {
      if (info.file.status !== 'uploading') {
      }
      if (info.file.status === 'done') {
        if (info.file.response.code === 200) {
          this.$message.success(`${info.file.name} 上传成功`)
          this.loadData();
        } else {
          this.$message.error(`上传出错,${info.file.response.message}`)
        }
      } else if (info.file.status === 'error') {
        this.$message.error(`上传出错`)
      }
    },
    customRequest(options) {
      let params = new FormData()
      params.append('file', options.file)
      uploadAction('/api/backup/upload', params).then((res) => {
        options.onSuccess(res, options.file)
      })
    }
  }
}
</script>

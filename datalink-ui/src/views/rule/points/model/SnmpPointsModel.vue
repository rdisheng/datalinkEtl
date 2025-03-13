<template>
  <div>
    <a-row style='width: 100%;padding-bottom: 10px' v-show='!disableEdit'>
      <a-col :span='18'>
        <a-space size='small'>
          <a-button type='primary' @click='newItem' icon='plus'>添加</a-button>
          <a-upload :showUploadList='false' :beforeUpload='(file) => checkFile(file,5,["xlsx"])'
                    @change='handlerUpload' :customRequest='customRequest'>
            <a-button icon='upload'>导入</a-button>
          </a-upload>
          <a-button icon='download' @click='download(false)'>导出</a-button>
          <a-popconfirm title='删除选中点位？' @confirm='removeBatch'>
            <a-button type='danger' icon='delete' v-show='selectedRowKeys.length > 0'>
              删除选中({{ selectedRowKeys.length }})
            </a-button>
          </a-popconfirm>
        </a-space>
      </a-col>
      <a-col :span='6' style='text-align: right'>
        <a-button @click='download(true)' type='link' icon='download'>下载模板</a-button>
      </a-col>
    </a-row>

    <a-table
      :columns='columns'
      :dataSource='data'
      :pagination='false'
      :loading='loading'
      size='middle'
      style='border: 1px #e8e3e3 solid'
      :row-selection='disableEdit ? null : { selectedRowKeys: selectedRowKeys,onChange:onSelectChange }'
      :scroll='{ y: 400 }'
    >
      <template v-for='(col, i) in Object.keys(fieldMap)' :slot='col' slot-scope='text, record'>
        <a-input
          :key='col'
          v-if='record.editable'
          style='margin: -5px 0'
          :value='text'
          :placeholder='Object.values(fieldMap)[i]'
          @change='e => handleChange(e.target.value, record.key, col)'
        />
        <template v-else>{{ text }}</template>
      </template>

      <template slot='index' slot-scope='text, record,index'>
        {{ index + 1 }}
      </template>

      <template slot='action' slot-scope='text, record'>
        <template v-if='record.editable'>
                      <span v-if='record.isNew'>
                        <a @click='saveRow(record)'>确定</a>
                        <a-divider type='vertical' />
                        <a-popconfirm title='删除此点位？' @confirm='remove(record.key)'>
                          <a>删除</a>
                        </a-popconfirm>
                      </span>
          <span v-else>
                        <a @click='saveRow(record)'>保存</a>
                        <a-divider type='vertical' />
                        <a @click='cancel(record.key)'>取消</a>
                    </span>
        </template>
        <span v-else>
                      <a @click='toggle(record.key)'>编辑</a>
                      <a-divider type='vertical' />
                      <a-popconfirm title='删除此点位？' @confirm='remove(record.key)'>
                        <a>删除</a>
                      </a-popconfirm>
                    </span>
      </template>
    </a-table>
  </div>
</template>

<script>

import { checkFile, timeStr } from '@/utils/util'
import { downFileByPost, uploadAction } from '@/api/manage'

export default {
  name: 'SnmpPointsModel',
  props: {
    disableEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      data: [],
      selectedRowKeys: [],
      fieldMap: { oid: 'OID', type: '读取方式' },
      columns: [
        {
          title: '#',
          dataIndex: 'index',
          width: '10%',
          scopedSlots: { customRender: 'index' }
        },
        {
          title: 'OID',
          dataIndex: 'oid',
          key: 'oid',
          width: '40%',
          scopedSlots: { customRender: 'oid' }
        },
        {
          title: '读取方式(GET/WALK)',
          dataIndex: 'type',
          key: 'type',
          width: '30%',
          scopedSlots: { customRender: 'type' }
        }
      ]
    }
  },
  mounted() {
    if (!this.disableEdit) {
      this.columns.push({
        title: '操作',
        key: 'action',
        scopedSlots: { customRender: 'action' }
      })
    }
  },
  methods: {
    checkFile,
    set(points) {
      this.data = []
      if (!points) return
      let index = 1
      for (let point of points) {
        this.data.push({
          key: index++,
          oid: point.oid,
          type: point.type,
          editable: false,
          isNew: false
        })
      }
    },
    get() {
      let result = []
      for (let item of this.data) {
        if (item.isNew) continue
        result.push({
          oid: item.oid,
          type: item.type
        })
      }
      return result
    },
    newItem() {
      const length = this.data.length
      this.data.push({
        key: length === 0 ? '1' : (parseInt(this.data[length - 1].key) + 1).toString(),
        oid: '',
        type: 'GET',
        editable: true,
        isNew: true
      })
    },
    remove(key) {
      this.data = this.data.filter(item => item.key !== key)
      this.selectedRowKeys = this.selectedRowKeys.filter(item => item !== key)
    },
    removeBatch() {
      this.data = this.data.filter(item => this.selectedRowKeys.indexOf(item.key) === -1)
      this.selectedRowKeys = []
    },
    saveRow(record) {
      this.loading = true
      const { key, oid, type } = record
      if (!oid || !type) {
        this.loading = false
        this.$message.error('填写不完整')
        return
      }
      if ('GET' !== type && 'WALK' !== type) {
        this.loading = false
        this.$message.error('读取方式必须是GET或WALK')
        return
      }
      const target = this.data.find(item => item.key === key)
      target.editable = false
      target.isNew = false
      this.loading = false
    },
    toggle(key) {
      const target = this.data.find(item => item.key === key)
      target._originalData = { ...target }
      target.editable = !target.editable
    },
    cancel(key) {
      const target = this.data.find(item => item.key === key)
      Object.keys(target).forEach(key => {
        target[key] = target._originalData[key]
      })
      target._originalData = undefined
    },
    handleChange(value, key, column) {
      const newData = [...this.data]
      const target = newData.find(item => key === item.key)
      if (target) {
        target[column] = value
        this.data = newData
      }
    },
    handlerUpload(info) {
      if (info.file.status === 'done') {
        if (info.file.response.code === 200) {
          let values = info.file.response.data
          let length = this.data.length
          let key = length ? parseInt(this.data[length - 1].key) : 0
          for (let value of values) {
            value.key = (++key).toString()
            value.editable = false
            value.isNew = false
            this.data.push(value)
          }
          this.$message.success('已导入' + values.length + '个点位')
        } else {
          this.$message.error(`上传出错,${info.file.response.message}`)
        }
      } else if (info.file.status === 'error') {
        this.$message.error(`上传出错`)
      }
    },
    customRequest(options) {
      let params = new FormData()
      const transformedMap = {}
      Object.keys(this.fieldMap).forEach((key) => {
        transformedMap[this.fieldMap[key]] = key
      })
      params.append('file', options.file)
      params.append('field', JSON.stringify(transformedMap))
      uploadAction('/api/rule/points/import', params).then((res) => {
        options.onSuccess(res, options.file)
      })
    },
    download(template) {
      let data = template ? [] : this.get()
      let fileName = template ? 'snmp_template.xlsx' : `snmp_${timeStr()}.xlsx`
      downFileByPost('/api/rule/points/export', {
        data: data,
        fieldMap: this.fieldMap,
        fileName: fileName
      }).then(res => {
        const blob = new Blob([res])
        let downloadElement = document.createElement('a')
        let href = window.URL.createObjectURL(blob)
        downloadElement.href = href
        downloadElement.download = decodeURIComponent(fileName)
        document.body.appendChild(downloadElement)
        downloadElement.click()
        document.body.removeChild(downloadElement)
        window.URL.revokeObjectURL(href)
      })
    },
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    }

  }
}
</script>


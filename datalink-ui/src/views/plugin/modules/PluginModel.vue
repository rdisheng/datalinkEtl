<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='插件'
    :width='600'
    :visible='visible'
    @cancel='onClose'
    :destroyOnClose='true'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-row :gutter='24'>
        <a-col :span='24'>
          <a-form-model-item label='插件名称' prop='pluginName'>
            <a-input v-model='modal.pluginName' placeholder='请输入插件名称'></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='输入端口' prop='inputCount'>
            <a-input-number v-model='modal.inputCount' placeholder='输入端口数量' :step='1' :min='0' :max='1'
                            style='width: 100%'></a-input-number>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='输出端口' prop='outputCount'>
            <a-input-number v-model='modal.outputCount' placeholder='输出端口数量' :step='1' :min='0' :max='5'
                            style='width: 100%'></a-input-number>
          </a-form-model-item>
        </a-col>
        <a-col :span='24'>
          <a-form-model-item label='包路径' prop='packagePath'>
            <a-input v-model='modal.packagePath' placeholder='请输入包路径'></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :span='24'>
          <a-form-model-item label='插件文件' prop='fileName'>
            <a-input v-model='modal.fileName' placeholder='请上传插件文件' :read-only='true'
                     style='display: inline-block;width: 80%'></a-input>
            <a-upload
              :showUploadList='false'
              :beforeUpload='(file) => checkFile(file,100,["jar"])'
              @change='handlerUpload'
              :customRequest='customRequest'
              style='display:inline-block;width: 20%'
            >
              <a-button style='width: 100px;margin-left: 10px'>
                <a-icon type='upload' />
                上传
              </a-button>
            </a-upload>
          </a-form-model-item>
        </a-col>
        <a-col :span='24'>
          <a-form-model-item label='说明'>
            <a-textarea :rows='3' v-model='modal.description' placeholder='请输入说明'></a-textarea>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>


    <div class='bottom-button'>
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 取消</a-button>
      <a-button type='primary' @click='handleOk'> 确定</a-button>
    </div>
  </a-modal>
</template>

<script>
import { checkFile } from '@/utils/util'
import { postAction, putAction, uploadAction } from '@/api/manage'

export default {
  name: 'PluginModel',
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {
        pluginName: '',
        inputCount: 1,
        outputCount: 1,
        fileName: ''
      },
      url: {
        add: '/api/plugin/add',
        update: '/api/plugin/update'
      },
      rules: {
        pluginName: [{ required: true, message: '请输入插件名称', trigger: 'blur' }],
        inputCount: [{ required: true, message: '请设置输入端口数量', trigger: 'change' }],
        outputCount: [{ required: true, message: '请设置输出端口数量', trigger: 'change' }],
        packagePath: [{ required: true, message: '请输入包路径', trigger: 'blur' }],
        fileName: [{ required: true, message: '请上传插件文件', trigger: 'blur' }]
      }
    }
  },
  methods: {
    checkFile,
    add() {
      this.edit({})
    },
    edit(record) {
      this.modal = Object.assign({}, this.modal, record)
      this.visible = true
    }
    ,
    onClose() {
      this.modal = { pluginName: '' }
      this.visible = false
    }
    ,
    handleOk() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let obj
          if (this.modal.pluginId) {
            obj = putAction(this.url.update, this.modal)
          } else {
            obj = postAction(this.url.add, this.modal)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.$emit('ok')
            } else {
              that.$message.error('保存失败')
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.onClose()
            })
        } else {
          return false
        }
      })
    },
    filterOption(input, option) {
      if (!option.componentOptions.children[0].text) {
        return false
      }
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    handlerUpload(info) {
      if (info.file.status !== 'uploading') {
      }
      if (info.file.status === 'done') {
        if (info.file.response.code === 200) {
          this.modal.fileName = info.file.response.data.fileName
          this.$message.success(`${info.file.name} 上传成功`)
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
      uploadAction('/api/plugin/upload', params).then((res) => {
        options.onSuccess(res, options.file)
      })
    }

  }
}
</script>

<style scoped>


</style>

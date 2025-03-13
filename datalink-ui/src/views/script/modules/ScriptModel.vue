<template>
  <div>
    <a-card :bordered='false' style='margin-bottom: 10px' :body-style='{padding:"17px 24px"}'>
      <a-row>
        <a-col :span='6' v-if='!scriptNameEdit'
               style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 4px'>
          {{ modal.scriptName }}
          <a-icon type='edit' @click='scriptNameEdit = true' />
        </a-col>
        <a-col :span='6' v-if='scriptNameEdit'>
          <a-input-search v-model='modal.scriptName' placeholder='请输入脚本名称' @search='saveScriptName'>
            <a-button slot='enterButton'>确定</a-button>
          </a-input-search>
        </a-col>
        <a-col :span='18' style='text-align: right'>
          <a-space size='small'>
            <a-button style='width:75px;' @click='onClose'> 返回</a-button>
            <a-button style='width:75px;' @click='showDebug = !showDebug'> 调试</a-button>
            <a-button type='primary' @click='saveScript' style='width:75px'> 保存</a-button>
          </a-space>
        </a-col>
      </a-row>
    </a-card>

    <a-row :gutter='12'>
      <a-col :span='showDebug?16:24'>
        <a-card :bordered='false'>
          <monaco-editor height='72vh' :minimap='true' ref='ScriptContentEditor' :auto-init='false'></monaco-editor>
        </a-card>
      </a-col>
      <a-col :span='8' v-if='showDebug'>
        <script-debug-model ref='ScriptDebugModel' :language='modal.scriptLanguage'></script-debug-model>
      </a-col>
    </a-row>
  </div>


</template>

<script>
import { getAction, postAction, putAction } from '@/api/manage'
import { scriptLanguageMap } from '@/config/language.config'
import MonacoEditor from '@/components/Editor/MonacoEditor'
import ScriptDebugModel from '@/views/script/modules/ScriptDebugModel'
import { uuid } from '@/utils/util'

export default {
  name: 'ScriptModel',
  components: { ScriptDebugModel, MonacoEditor },
  data() {
    return {
      title: '操作',
      visible: false,
      showDebug: false,
      scriptNameEdit: false,
      modal: {},
      url: {
        info: '/api/script/info',
        add: '/api/script/add',
        update: '/api/script/update'
      },
      scriptLanguageMap
    }
  },
  provide() {
    return {
      getScriptContent: () => {
        return this.$refs.ScriptContentEditor.get()
      }
    }
  },
  mounted() {
    let scriptId = this.$route.params.scriptId
    if (scriptId.startsWith('new')) {
      let scriptIds = scriptId.split('-')
      this.modal = {
        scriptName: 'script_' + uuid(5),
        scriptLanguage: scriptIds[1],
        scriptContent: this.scriptLanguageMap[scriptIds[1]].default
      }
      this.initEditorValue()
    } else {
      getAction(this.url.info, { scriptId: scriptId }).then(res => {
        this.modal = res.data
        this.initEditorValue()
      })
    }
  },
  methods: {
    initEditorValue() {
      this.$nextTick(() => {
        this.$refs.ScriptContentEditor.init(this.modal.scriptContent, this.scriptLanguageMap[this.modal.scriptLanguage].editor)
      })
    },
    saveScriptName() {
      if (!this.modal.scriptName) {
        this.$message.warn('脚本名称不可为空')
        return
      }
      this.scriptNameEdit = false
    },
    saveScript() {
      this.modal.scriptContent = this.$refs.ScriptContentEditor.get()

      if (!this.modal.scriptContent) {
        this.$message.warn('脚本不可为空')
        return
      }

      let obj
      if (this.modal.scriptId) {
        obj = putAction(this.url.update, this.modal)
      } else {
        obj = postAction(this.url.add, this.modal)
      }
      obj.then(res => {
        if (res.code === 200) {
          this.modal.scriptId = res.data.scriptId
          this.$message.success('保存成功')
        } else {
          this.$message.warning('保存失败')
        }
      })
    },
    onClose() {
      this.$router.push({ name: 'scriptList' })
    }
  }
}
</script>

<style>

</style>

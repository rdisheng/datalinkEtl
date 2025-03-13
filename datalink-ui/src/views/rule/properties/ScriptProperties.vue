<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24' class='body'>
        <a-form-model-item label='脚本' prop='script'>
          <monaco-editor ref='MonacoEditor' height='55vh' :auto-init='false'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
    <a-col :span='24'>
      <a-button @click='selectScript' icon='code'>选择脚本</a-button>
    </a-col>
    <script-select-model ref='ScriptSelectModel' @select='onSelectedScript'></script-select-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor'
import { scriptLanguageMap } from '@/config/language.config'
import ScriptSelectModel from './model/ScriptSelectModel.vue'

export default {
  components: { ScriptSelectModel, MonacoEditor },
  data() {
    return {
      scriptLanguageMap,
      properties: {},
      rules:{
        script: [{ required: true, validator: this.checkEditor, message: '请输入脚本', trigger: 'blur' }]
      }
    }
  },
  props: {
    param: {
      type: Object,
      default: {}
    }
  },
  mounted() {
    this.properties.language = this.param.language
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      if(!this.properties.script) this.properties.script = this.scriptLanguageMap[this.properties.language].default
      this.$nextTick(() => {
        this.$refs.MonacoEditor.init(this.properties.script, this.scriptLanguageMap[this.properties.language].editor)
      })
    },
    get(callback) {
      this.properties.script = this.$refs.MonacoEditor.get()
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    },
    checkEditor(rule, value, callback) {
      let content = this.$refs.MonacoEditor.get()
      if (!content) {
        return callback(new Error())
      } else {
        return callback()
      }
    },
    selectScript(){
      this.$refs.ScriptSelectModel.show(this.properties.language)
    },
    onSelectedScript(script){
      this.$refs.MonacoEditor.set(script)
    },
  }
}
</script>

<style>


</style>

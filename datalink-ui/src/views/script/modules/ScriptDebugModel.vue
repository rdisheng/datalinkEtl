<template>
  <a-card title='调试' :body-style='{}' :bordered='false'>
    <span slot='extra'>
      <a-button type='primary' @click='runScript' icon='caret-right' class='runBtn'> 运行</a-button>
    </span>

    <div style='padding-bottom: 5px'>输入参数</div>
    <monaco-editor height='28vh' ref='ParamContentEditor' language='json'></monaco-editor>

    <div style='padding: 5px 0;display: grid;grid-template-columns: auto auto;justify-content: space-between;'>
      <span style=' justify-self: start;'>运行结果</span>
      <span v-show='time >= 0' style=' justify-self: end;'>用时：{{ time }}ms</span>
    </div>
    <monaco-editor height='28vh' ref='ResultContentEditor' language='json'></monaco-editor>

  </a-card>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor.vue'
import { postAction } from '@/api/manage'

export default {
  name: 'ScriptDebugModel',
  components: { MonacoEditor },
  inject: ['getScriptContent'],
  props: {
    language: {
      type: String,
      default: undefined
    }
  },
  data() {
    return {
      url: {
        test: '/api/script/test'
      },
      time: -1
    }
  },
  mounted() {
    this.$refs.ParamContentEditor.set('{}')
    this.$refs.ResultContentEditor.set('')
  },
  methods: {
    runScript() {
      let param = {
        language: this.language,
        script: this.getScriptContent(),
        param: this.$refs.ParamContentEditor.get()
      }
      if (!param.param) {
        this.$message.warn('输入参数不可为空')
        return
      }
      if (!param.script) {
        this.$message.warn('脚本不可为空')
        return
      }
      postAction(this.url.test, param).then(res => {
        if (res.code === 200) {
          this.$refs.ResultContentEditor.set(JSON.stringify(res.data.result))
          this.$refs.ResultContentEditor.format()
          this.time = res.data.time
          this.$message.success('运行成功')
        } else {
          this.$message.error('运行失败: ' + res.message)
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
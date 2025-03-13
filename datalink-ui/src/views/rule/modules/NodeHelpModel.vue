<template>

  <div v-html='content' class='content'></div>

</template>

<script>
import { getNodeHelp } from '@/config/node.help'

export default {
  name: 'NodeHelpModel',
  data() {
    return {
      content: '暂无说明'
    }
  },
  methods: {
    show(nodeCode) {
      let nodeHelp = getNodeHelp(nodeCode)
      if (nodeHelp) {
        this.content = nodeHelp
      }
    }
  }
}
</script>

<style scoped>

.content{
  line-height: 30px;
  padding: 20px;
  border:1px #d5d5d5 solid;
  border-radius: 4px;
}

</style>
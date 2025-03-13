<template>
  <a-config-provider :locale='locale'>
    <div id='app'>
      <router-view />
    </div>
  </a-config-provider>
</template>

<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'

export default {
  data() {
    return {
      locale: zhCN
    }
  }
}
</script>

<style>

.ant-card-head {
  font-size: 15px !important;
}

.ant-layout-footer {
  padding: 0 !important;
}

.ant-card-bordered {
  border-radius: 3px !important;
}

.bottom-button {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e9e9e9;
  padding: 10px 16px;
  background: #fff;
  text-align: right;
  z-index: 1
}

</style>

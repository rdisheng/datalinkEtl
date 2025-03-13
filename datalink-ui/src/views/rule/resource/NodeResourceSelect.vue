<template>
  <div>
    <a-select v-model='internalValue' placeholder='请选择或新建资源' @change='changeResource'>
      <div slot='dropdownRender' slot-scope='menu'>
        <v-nodes :vnodes='menu' />
        <a-divider style='margin: 4px 0;' />
        <div
          style='padding: 4px 8px 8px 8px; cursor: pointer;text-align: center'
          @mousedown='e => e.preventDefault()'
          @click='addResource'
        >
          <a-icon type='plus' />
          新建资源
        </div>
      </div>
      <a-select-option v-for='(item,index) in resourceList' :value='item.resourceId' :key='index'>
        {{ item.resourceName }}
      </a-select-option>
    </a-select>
    <node-resource-model v-if='resourceType' ref='NodeResourceModel' @ok='onAddSuccess'></node-resource-model>
  </div>
</template>

<script>
import NodeResourceModel from './NodeResourceModel.vue'
import { postAction } from '@/api/manage'

export default {
  components: {
    NodeResourceModel,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  props: {
    resourceType: {
      type: String
    },
    value: {
      type: String
    }
  },
  data() {
    return {
      internalValue: undefined,
      resourceList: [],
      url: {
        resourceList: '/api/resource/list'
      }
    }
  },
  mounted() {
    postAction(this.url.resourceList, { resourceType: this.resourceType }).then(res => {
      this.resourceList = res.data
    })
  },
  watch: {
    value(newValue) {
      this.internalValue = newValue
    }
  },
  methods: {
    addResource() {
      this.$refs.NodeResourceModel.add(this.resourceType)
    },
    onAddSuccess(resourceId) {
      postAction(this.url.resourceList, { resourceType: this.resourceType }).then(res => {
        this.resourceList = res.data
        this.internalValue = resourceId
        this.changeResource()
      })
    },
    changeResource() {
      this.$emit('input', this.internalValue) // 触发双向绑定
      let result = this.resourceList.find(resource => resource.resourceId === this.internalValue)
      this.$emit('change', result) // 触发change事件
    }
  }
}
</script>
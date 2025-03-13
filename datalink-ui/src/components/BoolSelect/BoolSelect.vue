<template>
  <a-select v-model='internalValue' @change='handleChange' :placeholder='placeholder' :dropdownStyle='{zIndex:1200}'>
    <a-select-option :value='stringTrue'>是</a-select-option>
    <a-select-option :value='stringFalse'>否</a-select-option>
  </a-select>
</template>

<script>
export default {
  props: {
    value: {
      type: Boolean
    },
    placeholder: {
      type: String,
      default: '请选择'
    }
  },
  data() {
    return {
      internalValue: this.value ? this.stringTrue : this.stringFalse,
      stringTrue: 'true',
      stringFalse: 'false'
    }
  },
  watch: {
    value(newValue) {
      this.internalValue = newValue ? this.stringTrue : this.stringFalse
    }
  },
  methods: {
    handleChange(value) {
      this.internalValue = value
      const boolValue = value === this.stringTrue
      this.$emit('input', boolValue) // 触发双向绑定
      this.$emit('change', boolValue) // 触发change事件
    }
  }
}
</script>
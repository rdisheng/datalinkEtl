<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='12'>
        <a-form-model-item label='IP' prop='ip'>
          <a-input v-model='properties.ip' placeholder='请输入IP' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='端口' prop='port'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='账户' prop='username'>
          <a-input v-model='properties.username' placeholder='请输入账户' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='密码' prop='password'>
          <a-input-password v-model='properties.password' placeholder='请输入密码' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='数据库名称' prop='databaseName'>
          <a-input v-model='properties.databaseName' placeholder='请输入数据库名称' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接数初始大小'>
          <a-input v-model='properties.initSize' placeholder='请输入连接数初始大小' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接数下限'>
          <a-input v-model='properties.minIdle' placeholder='请输入连接池下限' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接数上限'>
          <a-input v-model='properties.maxActive' placeholder='请输入连接数上限' />
        </a-form-model-item>
      </a-col>
    </a-row>
  </a-form-model>

</template>

<script>

export default {
  data() {
    return {
      properties: {
        ip: '127.0.0.1',
        port: '3306',
        initSize: 8,
        minIdle: 1,
        maxActive: 20
      },
      rules: {
        ip: [{ required: true, message: '请输入IP', trigger: 'blur' }],
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        username: [{ required: true, message: '请输入账户', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        databaseName: [{ required: true, message: '请输入数据库名称', trigger: 'blur' }]
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
    },
    get(callback) {
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    }
  }
}
</script>

<style scoped>
</style>

const JDBC_READ_HELP = `
要连接的数据库地址需先在资源管理中创建。<br>
节点启动后将定时执行SQL获取数据。<br>
SQL可使用\${变量名}的方式引用全局变量。<br><br>
<h4>输出字段：</h4>
<pre><code>sql 执行的SQL
result 查询到的数据结果,按行传输为Object,打包传输为Object[]</code></pre>
`

const JDBC_WRITE_HELP = `
要连接的数据库地址需先在资源管理中创建。<br>
节点启动后有数据输入时将执行SQL。<br>
SQL可使用\${变量名}的方式引用全局变量或输入字段。<br>
`

const nodeHelp = {
  MYSQL_READ: JDBC_READ_HELP,
  POSTGRESQL_READ: JDBC_READ_HELP,
  TDENGINE_READ: JDBC_READ_HELP,
  SQLSERVER_READ: JDBC_READ_HELP,
  TIMESCALEDB_READ: JDBC_READ_HELP,
  MARIADB_READ: JDBC_READ_HELP,
  DM8_READ: JDBC_READ_HELP,
  KINGBASE_READ: JDBC_READ_HELP,

  MYSQL_WRITE: JDBC_WRITE_HELP,
  POSTGRESQL_WRITE: JDBC_WRITE_HELP,
  TDENGINE_WRITE: JDBC_WRITE_HELP,
  SQLSERVER_WRITE: JDBC_WRITE_HELP,
  TIMESCALEDB_WRITE: JDBC_WRITE_HELP,
  MARIADB_WRITE: JDBC_WRITE_HELP,
  DM8_WRITE: JDBC_WRITE_HELP,
  KINGBASE_WRITE: JDBC_WRITE_HELP,

  MQTT_SUBSCRIBE: `
要连接的MQTT地址需先在资源管理中创建。<br>
可订阅单个或多个主题，订阅多主题时，请使用逗号分隔（,）<br>
订阅主题可使用\${变量名}的方式引用全局变量<br><br>
<h4>输出字段：</h4>
<pre><code>topic 订阅到消息的Topic
qos  订阅到消息的QoS
retained  订阅到消息是否为保留消息
payload 订阅到消息内容,UTF-8字符串</code></pre>
`,

  MQTT_PUBLISH: `
要连接的MQTT地址需先在资源管理中创建。<br>
发布主题和消息模板可使用\${变量名}的方式引用全局变量或输入字段。<br>
消息模板置空时原样发布输入内容<br>
`,

  SWITCH: `
输入的数据按照不同的表达式进行匹配，分发到不同到的输出口。<br>
条件表达式可直接引用输入字段及全局变量。<br>
例如上游输入字段有payload,则条件可写为:<br>
出口1：payload == "a"<br>
出口2：payload == "b"<br>
则当输入数据payload字段值为a时,数据发送到出口1,值为b时发送到出口2。<br>
出口顺序为从上到下,最上为1,依次类推。<br>
条件表达式支持>,<,==,!=,>=,<=,!,&&,||等逻辑运算符。<br>
`,

  PACK: `
输入的数据将被节点缓存，当达到设定数量时以数组形式批量输出。<br>
`,

  HTTP_SERVER: `
启动后将监听配置的端口和路径。<br>
需要配合HTTP响应节点使用。<br>
<h4>输出字段：</h4>
<pre><code>path 请求路径
method  请求方法
headers  请求头
params  请求参数
body 请求体</code></pre>
`,


  HTTP_RESPONSE: `
需要配合HTTP服务节点使用。<br>
对HTTP服务收到的请求做出响应。
`,



}

function getNodeHelp(code) {
  return nodeHelp[code]
}

export { getNodeHelp }
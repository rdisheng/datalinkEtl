import { timeUnitMap } from './time.config'

const resourceGroupMap = {
  CHANNEL: '消息通道',
  PROTOCOL: '通讯协议',
  DATABASE: '数据存储'
}

const resourceConfigMap = {
  MQTT: {
    name: 'MQTT',
    type: 'all',
    group: 'CHANNEL',
    component: 'MqttProperties',
    details: {
      resource: { name: '地址', value: (resource) => resource.properties.url },
      rule: [
        { name: '地址', value: (resource) => resource.properties.url },
        { name: 'Topic', value: (resource) => resource.properties.topic }
      ]
    }
  },
  SNMP: {
    name: 'SNMP',
    type: 'source',
    group: 'PROTOCOL',
    component: 'SnmpProperties',
    details: {
      resource: { name: '地址', value: (resource) => `udp:${resource.properties.ip}/${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `udp:${resource.properties.ip}/${resource.properties.port}` },
        {
          name: '读取点位',
          value: (resource) => resource.properties.points ? resource.properties.points.length : undefined
        },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        }
      ]
    }
  },
  KAFKA: {
    name: 'Kafka',
    type: 'all',
    group: 'CHANNEL',
    component: 'KafkaProperties',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.url}` },
        { name: 'Topic', value: (resource) => resource.properties.topic }
      ]
    }
  },
  RABBITMQ: {
    name: 'RabbitMQ',
    type: 'all',
    group: 'CHANNEL',
    component: 'RabbitMQProperties',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '虚拟主机', value: (resource) => resource.properties.virtualHost },
        { name: '交换机', value: (resource) => resource.properties.exchange },
        { name: '队列', value: (resource) => resource.properties.queue }
      ]
    }
  },
  ROCKETMQ: {
    name: 'RocketMQ',
    type: 'all',
    group: 'CHANNEL',
    component: 'RocketMQProperties',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.url}` },
        { name: '分组', value: (resource) => resource.properties.group },
        { name: 'Topic', value: (resource) => resource.properties.topic }
      ]
    }
  },
  ACTIVEMQ: {
    name: 'ActiveMQ',
    type: 'all',
    group: 'CHANNEL',
    component: 'ActiveMQProperties',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.url}` },
        {
          name: (resource) => resource.properties.model === 'queue' ? 'Queue' : 'Topic',
          value: (resource) => resource.properties.model === 'queue' ? resource.properties.queue : resource.properties.topic
        }
      ]
    }
  },

  MYSQL: {
    name: 'MySQL',
    type: 'all',
    group: 'DATABASE',
    component: 'JdbcProperties',
    details: {
      resource: {
        name: '地址',
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        }
      ]
    }
  },

  POSTGRESQL: {
    name: 'PostgreSQL',
    type: 'all',
    group: 'DATABASE',
    component: 'JdbcProperties',
    details: {
      resource: {
        name: '地址',
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        }
      ]
    }
  },

  SQLSERVER: {
    name: 'SQL Server',
    type: 'all',
    group: 'DATABASE',
    component: 'JdbcProperties',
    details: {
      resource: {
        name: '地址',
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        }
      ]
    }
  },
  TDENGINE: {
    name: 'TDengine',
    type: 'all',
    group: 'DATABASE',
    component: 'JdbcProperties',
    details: {
      resource: {
        name: '地址',
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        }
      ]
    }
  },

  OPCUA: {
    name: 'OPC UA',
    type: 'all',
    group: 'PROTOCOL',
    component: 'OpcUAProperties',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `opc.tcp://${resource.properties.ip}:${resource.properties.port}` },
        {
          name: '读取点位',
          value: (resource) => resource.properties.points ? resource.properties.points.length : undefined
        },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        },
        {
          name: '点位地址', value: (resource) => resource.properties.address
        },
        {
          name: '数据值', value: (resource) => resource.properties.dataValue
        }
      ]
    }
  },
  MODBUSTCP: {
    name: 'Modbus TCP',
    type: 'all',
    group: 'PROTOCOL',
    component: 'ModbusTcpProperties',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        {
          name: '读取点位',
          value: (resource) => resource.properties.points ? resource.properties.points.length : undefined
        },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        },
        {
          name: '数据地址', value: (resource) => resource.properties.address
        },
        {
          name: '数据值', value: (resource) => resource.properties.dataValue
        }
      ]
    }
  },
  REDIS: {
    name: 'Redis',
    type: 'all',
    group: 'DATABASE',
    component: 'RedisProperties',
    details: {
      resource: {
        name: '地址',
        value: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
        },
        { name: '执行命令', value: (resource) => `${resource.properties.command}` },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.initialDelayUnit]}` : undefined
        },
        {
          name: 'Cron表达式', value: (resource) => resource.properties.cronExpression
        }
      ]
    }
  },
}

function createComponentMap() {
  let result = {}
  for (let resourceConfigMapKey in resourceConfigMap) {
    result[resourceConfigMapKey] = resourceConfigMap[resourceConfigMapKey].component
  }
  return result
}

const resourceComponentMap = createComponentMap()

function createTypeMap() {
  let result = {}
  for (let resourceConfigMapKey in resourceConfigMap) {
    result[resourceConfigMapKey] = resourceConfigMap[resourceConfigMapKey].name
  }
  return result
}

const resourceTypeMap = createTypeMap()

function getResourceListByType(type) {
  let result = []
  for (let resourceGroupMapKey in resourceGroupMap) {
    let resourceTypeList = getResourceListByTypeAndGroup(type, resourceGroupMapKey)
    if (!resourceTypeList || resourceTypeList.length === 0) {
      continue
    }
    result.push({
      group: resourceGroupMap[resourceGroupMapKey],
      list: resourceTypeList
    })
  }
  return result
}

function getResourceListByTypeAndGroup(type, group) {
  let result = []
  for (let resourceConfigMapKey in resourceConfigMap) {
    let resourceConfig = resourceConfigMap[resourceConfigMapKey]
    if (type && resourceConfig.type !== type && resourceConfig.type !== 'all') {
      continue
    }
    if (group && resourceConfig.group !== group) {
      continue
    }
    let item = {
      name: resourceConfig.name,
      type: resourceConfig.type,
      code: resourceConfigMapKey
    }
    result.push(item)
  }
  return result
}

const resourceTypeAllList = getResourceListByType(undefined)

const emptyDetail = { name: '', value: '' }

function getResourceDetails(resource, mode) {
  if (!resource || !resource.resourceType) {
    return mode === 'resource' ? emptyDetail : [emptyDetail]
  }
  let resourceConfigMapElement = resourceConfigMap[resource.resourceType]
  let detail = resourceConfigMapElement.details[mode]
  if (mode === 'resource') {
    return toResultItem(detail, resource)
  } else {
    return detail.map((item) => toResultItem(item, resource)).filter((item) => item.name && item.value)
  }
}

function toResultItem(detailItem, resource) {
  let name = typeof detailItem.name === 'string' ? detailItem.name : detailItem.name(resource)
  if (!name) return emptyDetail
  let value = typeof detailItem.value === 'string' ? detailItem.value : detailItem.value(resource)
  if (!value) return emptyDetail
  return { name: name, value: value }
}


export {
  resourceComponentMap,
  resourceTypeMap,
  resourceTypeAllList,
  getResourceListByType,
  getResourceListByTypeAndGroup,
  getResourceDetails
}

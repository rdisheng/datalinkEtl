const transformModeList = [
  {
    name: '透传',
    value: 'WITHOUT'
  },
  {
    name: 'SQL转换',
    value: 'SQL'
  },
  {
    name: '脚本转换',
    value: 'SCRIPT',
    items: [
      {
        name: 'JavaScript脚本',
        value: 'SCRIPT-JAVASCRIPT'
      },
      {
        name: 'Groovy脚本',
        value: 'SCRIPT-GROOVY'
      }
    ]
  },
  {
    name: '插件转换',
    value: 'PLUGIN'
  }
]

function createModeMap() {
  let result = {}
  for (let i = 0; i < transformModeList.length; i++) {
    result[transformModeList[i].value] = transformModeList[i].name
  }
  return result
}

const transformModeMap = createModeMap()

export {
  transformModeMap, transformModeList
}

const scriptLanguageMap = {
  javascript: {
    name: 'JavaScript',
    editor:'javascript',
    default:
      '/**\n' +
      '* 数据处理JavaScript脚本,方法名transform不可修改\n' +
      '* 参数: data Object 输入数据\n' +
      '* 返回值: data Object 输出数据\n' +
      '*/\n' +
      'function transform(data) {\n' +
      '    // do somethings \n' +
      '    return data;\n' +
      '}'
  },
  groovy: {
    name: 'Groovy',
    editor:'java',
    default:
      '/**\n' +
      '* 数据处理Groovy脚本,方法名transform不可修改\n' +
      '* 参数: data Object 输入数据\n' +
      '* 返回值: data Object 输出数据\n' +
      '*/\n' +
      'def transform(data) {\n' +
      '    // do somethings \n' +
      '    return data;\n' +
      '}'
  },
}

function createLanguageList() {
  let result = []
  for (let key in scriptLanguageMap) {
    result.push({
      name: scriptLanguageMap[key].name,
      value: key
    })
  }
  return result
}

const scriptLanguageList = createLanguageList()
export {
  scriptLanguageMap, scriptLanguageList
}

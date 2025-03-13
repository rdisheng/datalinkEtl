/**
 * components util
 */

/**
 * 清理空值，对象
 * @param children
 * @returns {*[]}
 */
export function filterEmpty(children = []) {
  return children.filter(c => c.tag || (c.text && c.text.trim() !== ''))
}

/**
 * 获取字符串长度，英文字符 长度1，中文字符长度2
 * @param {*} str
 */
export const getStrFullLength = (str = '') =>
  str.split('').reduce((pre, cur) => {
    const charCode = cur.charCodeAt(0)
    if (charCode >= 0 && charCode <= 128) {
      return pre + 1
    }
    return pre + 2
  }, 0)

/**
 * 截取字符串，根据 maxLength 截取后返回
 * @param {*} str
 * @param {*} maxLength
 */
export const cutStrByFullLength = (str = '', maxLength) => {
  let showLength = 0
  return str.split('').reduce((pre, cur) => {
    const charCode = cur.charCodeAt(0)
    if (charCode >= 0 && charCode <= 128) {
      showLength += 1
    } else {
      showLength += 2
    }
    if (showLength <= maxLength) {
      return pre + cur
    }
    return pre
  }, '')
}

/**
 * 深拷贝
 * @param obj
 * @returns {*[]}
 */
export const deepCopy = function(obj) {
  // 只拷贝对象
  if (typeof obj !== 'object') return
  // 根据obj的类型判断是新建一个数组还是一个对象
  var newObj = obj instanceof Array ? [] : {}
  for (var key in obj) {
    // 遍历obj,并且判断是obj的属性才拷贝
    if (obj.hasOwnProperty(key)) {
      // 判断属性值的类型，如果是对象递归调用深拷贝
      newObj[key] = typeof obj[key] === 'object' ? deepCopy(obj[key]) : obj[key]
    }
  }
  return newObj
}

export function timeFix() {
  const time = new Date()
  const hour = time.getHours()
  return hour < 9 ? '早上好' : hour <= 11 ? '上午好' : hour <= 13 ? '中午好' : hour < 20 ? '下午好' : '晚上好'
}

/**
 * 触发 window.resize
 */
export function triggerWindowResizeEvent() {
  const event = document.createEvent('HTMLEvents')
  event.initEvent('resize', true, true)
  event.eventType = 'message'
  window.dispatchEvent(event)
}

export function handleScrollHeader(callback) {
  let timer = 0

  let beforeScrollTop = window.pageYOffset
  callback = callback || function() {
  }
  window.addEventListener(
    'scroll',
    event => {
      clearTimeout(timer)
      timer = setTimeout(() => {
        let direction = 'up'
        const afterScrollTop = window.pageYOffset
        const delta = afterScrollTop - beforeScrollTop
        if (delta === 0) {
          return false
        }
        direction = delta > 0 ? 'down' : 'up'
        callback(direction)
        beforeScrollTop = afterScrollTop
      }, 50)
    },
    false
  )
}

export function isIE() {
  const bw = window.navigator.userAgent
  const compare = (s) => bw.indexOf(s) >= 0
  const ie11 = (() => 'ActiveXObject' in window)()
  return compare('MSIE') || ie11
}

/**
 * Remove loading animate
 * @param id parent element id or class
 * @param timeout
 */
export function removeLoadingAnimate(id = '', timeout = 1500) {
  if (id === '') {
    return
  }
  setTimeout(() => {
    document.body.removeChild(document.getElementById(id))
  }, timeout)
}

export function scorePassword(pass) {
  let score = 0
  if (!pass) {
    return score
  }
  // award every unique letter until 5 repetitions
  const letters = {}
  for (let i = 0; i < pass.length; i++) {
    letters[pass[i]] = (letters[pass[i]] || 0) + 1
    score += 5.0 / letters[pass[i]]
  }

  // bonus points for mixing it up
  const variations = {
    digits: /\d/.test(pass),
    lower: /[a-z]/.test(pass),
    upper: /[A-Z]/.test(pass),
    nonWords: /\W/.test(pass)
  }

  let variationCount = 0
  for (var check in variations) {
    variationCount += (variations[check] === true) ? 1 : 0
  }
  score += (variationCount - 1) * 10

  return parseInt(score)
}

/**
 * 简单实现防抖方法
 *
 * 防抖(debounce)函数在第一次触发给定的函数时，不立即执行函数，而是给出一个期限值(delay)，比如100ms。
 * 如果100ms内再次执行函数，就重新开始计时，直到计时结束后再真正执行函数。
 * 这样做的好处是如果短时间内大量触发同一事件，只会执行一次函数。
 *
 * @param fn 要防抖的函数
 * @param delay 防抖的毫秒数
 * @returns {Function}
 */
export function simpleDebounce(fn, delay = 100) {
  let timer = null
  return function() {
    let args = arguments
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 提取脚本中的代码部分
 * @param script
 * @returns {*}
 */
export function extractScript(script, length) {
  const regex = /\/\*[\s\S]*?\*\/|\/\/.*/g
  let result = script.replace(regex, '')
  return length ? truncateString(result, length) : result
}


export function truncateString(str, maxLength) {
  if (str.length > maxLength) {
    return str.slice(0, maxLength) + '...'
  } else {
    return str
  }
}

export function checkFile(file, size = 10, allowUpload = []) {
  //检查大小
  const sizeCheck = file.size / 1024 / 1024 < size
  if (!sizeCheck) {
    this.$message.error('文件大小不能超过' + size + 'M！')
    return false
  }
  //检查格式
  if (allowUpload.length > 0) {
    let fileExtension = file.name.split('.').pop()
    if (fileExtension) fileExtension = fileExtension.toLowerCase()
    if (allowUpload.indexOf(fileExtension) === -1) {
      this.$message.error('文件格式不合法！')
      return false
    }
  }
  return true
}

export function timeStr(){
  const now = new Date();
  const year = now.getFullYear().toString();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = now.getDate().toString().padStart(2, '0');
  const hours = now.getHours().toString().padStart(2, '0');
  const minutes = now.getMinutes().toString().padStart(2, '0');
  const seconds = now.getSeconds().toString().padStart(2, '0');
  return `${year}${month}${day}${hours}${minutes}${seconds}`;
}

export function uuid(length = 32, radix) {
  const num = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  let result = "";
  for (let i = 0; i < length; i++) {
    result += num.charAt(Math.floor(Math.random() * (radix || num.length)));
  }
  return result;
}

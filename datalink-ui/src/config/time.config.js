const timeUnitMap = {
  MILLIS: '毫秒',
  SECONDS: '秒',
  MINUTES: '分钟',
  HOURS: '小时',
  DAYS: '天',
  WEEKS: '周',
  MONTHS: '月',
  YEARS: '年',
}

function createUnitList() {
  let result = []
  for (let key in timeUnitMap) {
    result.push({
      name: timeUnitMap[key],
      value: key
    })
  }
  return result
}

const timeUnitList = createUnitList()
export {
  timeUnitMap, timeUnitList
}

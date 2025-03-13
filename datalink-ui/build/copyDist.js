
const path = require('path')
const fs = require('fs')

const OriginFilePath = path.join(__dirname, '../dist')
const CopyFilePath = path.join(__dirname, '../../datalink-web/src/main/resources/static/')

deleteFile(CopyFilePath);

if (!fs.existsSync(CopyFilePath)) {
  fs.mkdir(CopyFilePath, err => {
    // console.log(err)
  })
}

getFiles(OriginFilePath, CopyFilePath)

function getFiles(OriginFilePath, CopyFilePath) {
  fs.readdir(OriginFilePath, { withFileTypes: true }, (err, files) => {
    for (let file of files) {
      if (!file.isDirectory()) {
        const OriginFile = path.resolve(OriginFilePath, file.name)
        const CopyFile = path.resolve(CopyFilePath, file.name)
        fs.copyFileSync(OriginFile, CopyFile)
      } else {
        const CopyDirPath = path.resolve(CopyFilePath, file.name)
        const OriginDirPath = path.resolve(OriginFilePath, file.name)
        fs.mkdir(CopyDirPath, (err) => {

        })
        getFiles(OriginDirPath, CopyDirPath)
      }
    }
  })
}

function deleteFile(path) {
  let files = [];
  if(fs.existsSync(path)) {
    files = fs.readdirSync(path);
    files.forEach(function(file, index) {
      let curPath = path + "/" + file;
      if(fs.statSync(curPath).isDirectory()) { // recurse
        deleteFile(curPath);
      } else { // delete file
        fs.unlinkSync(curPath);
      }
    });
    fs.rmdirSync(path);
  }
}

console.log(`\n> Dist Copy complete! \n`)


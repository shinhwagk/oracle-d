const { app, Menu, BrowserWindow, Tray, ipcMain } = require('electron')

const path = require('path')
const url = require('url')

let mainWindow

// const template = [{
//   role: 'window',
//   submenu: [
//     { role: 'minimize' },
//     { role: 'close' }
//   ]
// }]
// const menu = Menu.buildFromTemplate(template)

// let tray = null

function createWindow() {
  // closeWindowEvent()
  // const tray = new Tray('./dist/favicon.ico')
  // const contextMenu = Menu.buildFromTemplate([
  //   { label: 'Item1', type: 'radio' },
  //   { label: 'Item2', type: 'radio' },
  //   { label: 'Item3', type: 'radio', checked: true },
  //   { label: 'Item4', type: 'radio' }
  // ])
  // tray.setToolTip('This is my application.')
  // tray.setContextMenu(contextMenu)

  mainWindow = new BrowserWindow({ width: 860, height: 650
    // , frame: false, 
  })

  mainWindow.webContents.openDevTools()
  // mainWindow.setIgnoreMouseEvents(false)

  mainWindow.loadURL(url.format({
    pathname: path.join(__dirname, 'www/index.html'),
    protocol: 'file:',
    slashes: true
  }))

  mainWindow.on('closed', function () {
    mainWindow = null
  })
}

app.on('ready', createWindow)

// app.setApplicationMenu(menu)

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', function () {
  if (mainWindow === null) {
    createWindow()
  }
})

// function closeWindowEvent() {
//   ipcMain.on('close-window', (event) => {
//     console.log('88')
//   })
// }
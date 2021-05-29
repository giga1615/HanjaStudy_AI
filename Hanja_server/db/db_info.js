module.exports = (function () {
    return {
      local: { // localhost
        host: '',
        port: '',
        user: '',
        password: '',
        database: 'hanja',
        connectionLimit: '100'
      },
      real: { // real server db info
        host: '',
        port: '',
        user: '',
        password: '!',
        database: '',
        connectionLimit: '100'
      },
      dev: { // dev server db info
        host: '',
        port: '',
        user: '',
        password: '',
        database: 'hanja',
        connectionLimit: '100'
      }
    }
  })();
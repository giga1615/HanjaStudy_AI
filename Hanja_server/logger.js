const { createLogger, format, transports } = require('winston');
const { combine, timestamp, label, prettyPrint, splat, simple, colorize, printf, align} = format;

//var winston = require('winston');
require('winston-daily-rotate-file');
//require('date-utils');
var moment = require('moment');

var fs = require('fs');

const logDir = 'log';
if (!fs.existsSync(logDir)) {
  fs.mkdirSync(logDir);
}

module.exports.CreateLogger = function() {
    // var appenders = [];

    // appenders.push(new winston.transports.DailyRotateFile({
    //     datePattern: 'YYYY-MM-DD',
    //     prepend: true,
    //     json: false,
    //     filename : `${logDir}/log`,
    //     timestamp: function () {    //로그 작성시 날짜 포맷
    //         return new Date().toFormat('YYYY-MM-DD HH24:MI:SS')
    //     },
    //     formatter: function (options) {
    //         var str = options.timestamp() + ' ' + options.message;
    //         return str;
    //     },
    //     maxsize:1024*1024*100,
    //     maxFiles:10
    // }));

    // appenders.push(new winston.transports.Console({
    //     json:false,
    //     timestamp: function () {    //로그 작성시 날짜 포맷
    //         return new Date().toFormat('YYYY-MM-DD HH:mm:ss.SSS')
    //     },
    //     formatter: function (options) {
    //         var str = options.timestamp() + ' ' + options.message;
    //         return str;
    //     },
    //     colorize : true,
    //     timestamp : true
    // }));

    //var logger = winston.createLogger({//new winston.Logger({
    var logger = createLogger({
        level: 'info',
        options : {flags : 'a+', encoding: 'utf-8', mode:0644},
        //transports: appenders
        format: combine(
            colorize(),
            align(),
            timestamp({
                format: 'YYYY-MM-DD HH:mm:ss.SSS'
            }),
            printf(info => {
                var str = `[${info.timestamp}][${info.level}]${info.message}`;
                str.slice(0, -1).replace(/\u001b\[[0-9]{1,2}m/g, '')
                return str;//`[${info.timestamp}][${info.level}]${info.message}`
            })
        ),
        transports: [
            new transports.Console(),
            new transports.DailyRotateFile(
                { 
                    filename : `${logDir}/db_was-%DATE%.log`,
                    datePattern : 'YYYY-MM-DD-HH',
                    zippedArchive: true,
                    maxsize:1024*1024*100,
                    maxFiles:5
                }
            )
            //new transports.File({filename : 'test.log'})
        ]
    });

    // const DailyRotateFile = require('winston-daily-rotate-file');
    //  logger.configure({
    //      level: 'info'
    //      transports: [
    //         new DailyRotateFile(opt)
    //      ]
    //  });
    // logger.stream = {
    //     write : function(message, encoding) {
    //         logger.info(message.slice(0, -1).replace(/\u001b\[[0-9]{1,2}m/g, ''));
    //     }
    // }
    return logger;
};

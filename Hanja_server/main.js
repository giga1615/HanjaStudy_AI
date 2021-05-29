var express = require('express');
var http = require('http');
var bodyParser = require('body-parser');
var Logger = require('./logger.js');
var logger = Logger.CreateLogger();
var app = express();
var vision = require('google-vision-api-client');
var requtil = vision.requtil;
var fs = require('fs');
const multer = require('multer');
var rimraf = require('rimraf');
require('date-utils');

// 설정
app.set('port', process.env.PORT || 8000);
app.use(bodyParser.json());

// db
var mysql_dbc = null;
var pool = null;

// json
var front_img_url = null;
var front_img_dir = null;
var google_vision_json_dir = null;

app.get('/', function(req, res) {
    res.send('테스트중!!');
})

// 회원가입/비밀번호수정
app.post('/registration', function(req, res) {
    try {
        if(req.body.userinfo) {
            var cmd = req.body.userinfo.cmd;

            if(cmd == 'adduserinfo') {

                var user = req.body.userinfo.user;
                var pswd = req.body.userinfo.pswd;
                var name = req.body.userinfo.name;
                var nickname = req.body.userinfo.nickname;
                var birthday = req.body.userinfo.birthday;

                logger.info('====================< registration(adduserinfo) start >====================');

                pool.getConnection(function (err, connection) {

                    var check_user_sql = 'SELECT COUNT(*) FROM members where user = ?';
                    var check_user_params = [user];
                    
                    connection.query(check_user_sql, check_user_params, function(err, result) {
                        if(err) {
                            logger.info('check members info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }

                        if(result[0]['COUNT(*)'] != 0){
                            logger.info('[already added user]');
                            logger.info('user : ' + user);
                            res.status(200).send('already added user');
                            connection.release();
                            return ;
                        }

                        var check_nickname_sql = 'SELECT COUNT(*) FROM members WHERE nickname=?';
                        var check_nickname_params = [nickname];
                        
                        connection.query(check_nickname_sql, check_nickname_params, function(err, result) {
                            if(err) {
                                logger.info('check members info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }
                            if(result[0]['COUNT(*)'] != 0){
                                logger.info('[already added nickname]');
                                logger.info('user : ' + user);
                                res.status(200).send('already added nickname');
                                connection.release();
                                return ;
                            }

                            var sql = 'INSERT INTO members(user, pswd, name, nickname, birthday) VALUES(?, ?, ?, ?, ?)';
                            var params = [user, pswd, name, nickname, birthday];

                            connection.query(sql, params, function(err, result) {
                                if(err) {
                                    logger.info('insert members info query error!!');
                                    logger.info('reason : ', err);
                                    res.status(500).send(err);
                                    connection.release();
                                    throw err;
                                }

                                fs.mkdir(front_img_dir + user, 0666, function(err) {
                                    if(err) {
                                        logger.info('make member folder error!!');
                                        logger.info(err);
                                        res.status(500).send(err);
                                        connection.release();
                                        throw err;
                                    }
                                    logger.info('[make member folder success]');

                                    fs.mkdir(front_img_dir + user + '/tmp', 0666, function(err) {
                                        if(err) {
                                            logger.info('make member tmp folder error!!');
                                            logger.info(err);
                                            res.status(500).send(err);
                                            connection.release();
                                            throw err;
                                        }

                                        logger.info('[make member tmp folder success]');
                                        logger.info('user : ' + user);

                                        connection.release();
                                        res.status(200).send('add user info success');
                                    });                                  
                                });
                            });
                        }); 
                    });
                });

                logger.info('====================< registration(adduserinfo) end >====================');

            } else if(cmd == 'updateuserinfo') {  // 비밀번호만 변경

                var user = req.body.userinfo.user;
                var pswd = req.body.userinfo.pswd;

                logger.info('====================< registration(updateuserinfo) start >====================');

                pool.getConnection(function (err, connection) {
                    var check_sql = 'SELECT COUNT(*) FROM members where user = ?';
                    var check_params = [user];
                    
                    connection.query(check_sql, check_params, function(err, result) {
                        if(err) {
                            logger.info('check members info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }

                        if (result[0]['COUNT(*)'] == 0){
                            logger.info('[non-added member]');
                            logger.info('user : ' + user);
                            res.status(200).send('non-added member');
                            connection.release();
                            return ;
                        }

                        var sql = 'UPDATE members SET pswd=? WHERE user=?';
                        var params = [pswd, user];

                        connection.query(sql, params, function(err, result) {
                            if(err) {
                                logger.info('update members info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }						

							logger.info('user : ' + user);

                            connection.release();
                            res.status(200).send('update user info success');                                                        
                        });
                    });
                });

                logger.info('====================< registration(updateuserinfo) end >====================');

            } else {
                res.status(500).send('json object command is null');
                logger.info('json object command is null');
            }
        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 비밀번호 찾기 -> 2학기로
app.post('/pswd', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var name = req.body.userinfo.name;
            var birthday = req.body.userinfo.birthday;

            logger.info('====================< pswd start >====================');

            pool.getConnection(function (err, connection) {

                var check_sql = 'SELECT COUNT(*) FROM members where user=? and name=? and birthday=?';
                var check_params = [user, name, birthday];

                connection.query(check_sql, check_params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0]['COUNT(*)'] == 0) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }
                    // 이메일 전송 코드 //////////////////////////////////////////
                    // 나중에 구현

                    logger.info("user : " + user);

                    connection.release();
                    res.status(200).send('send temp pswd success');
                });
            });

            logger.info('====================< pswd end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 회원탈퇴
app.post('/withdrawal', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var pswd = req.body.userinfo.pswd;

            logger.info('====================< withdrawal start >====================');
            
            pool.getConnection(function (err, connection) {

                var check_sql = 'SELECT * FROM members WHERE user=? AND pswd=?';
                var check_params = [user, pswd];

                connection.query(check_sql, check_params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0] == undefined) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }

                    var family_nickname = result[0].family_nickname;

                    // 여러 테이블 데이터 삭제
                    // [참고] http://blog.naver.com/PostView.nhn?blogId=jaebum85&logNo=110180116945&parentCategoryNo=&categoryNo=4&viewDate=&isShowPopularPosts=true&from=search
                    // var sql = 'delete m, f1, f2, l from members as m left join friends as f1 on m.user = f1.user left join friends as f2 on m.user = f2.friend_user left join learning as l on m.user = l.user where m.user = ?';
                    var delete_friend_learning_sql = 'DELETE m, f1, f2, l ' + 
                                'FROM members AS m ' + 
                                'LEFT JOIN friends AS f1 ON m.user = f1.user ' +            // 탈퇴회원의 친구 삭제
                                'LEFT JOIN friends AS f2 ON m.user = f2.friend_user ' +     // 탈퇴회원의 친구의 친구목록에서 탈퇴회원 삭제
                                'LEFT JOIN learning AS l ON m.user = l.user ' +             // 탈퇴회원의 학습정보 삭제
                                'WHERE m.user = ?';                    
                    var delete_friend_learning_params = [user];

                    connection.query(delete_friend_learning_sql, delete_friend_learning_params, function(err, result) {
                        if(err) {
                            logger.info('delete members info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }

                        logger.info('[delete friend list and learning list success]');
                        
                        // 탈퇴한 회원의 가족의 가족닉네임 초기화
                        var update_family_sql = 'UPDATE members SET family_nickname=NULL WHERE nickname=?';
                        var update_family_params = [family_nickname];

                        connection.query(update_family_sql, update_family_params, function(err, result) {
                            if(err) {
                                logger.info('update family info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }

                            logger.info('[update family info success]');
                            
                            // 회원 학습정보 이미지 폴더 삭제
                            rimraf(front_img_dir + user, function() { 
                                logger.info('[delete user folder success]'); 
                                logger.info("user : " + user);

                                connection.release();
                                res.status(200).send('delete user info success');
                            });       
                        });
                    });
                });
            });

            logger.info('====================< withdrawal end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 회원정보
app.post('/memberinfo', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;

            logger.info('====================< memberinfo start >====================');

            pool.getConnection(function (err, connection) {

                var check_user_sql = 'SELECT * FROM members WHERE user=?';
                var check_user_params = [user];

                connection.query(check_user_sql, check_user_params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0] == undefined) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }

                    var userinfo = new Object();
                    userinfo.userinfo = result[0];

                    var last_week_sql = 'SELECT COUNT(*) FROM learning WHERE user=? AND learning_date ';
                    var this_week_sql = 'SELECT COUNT(*) FROM learning WHERE user=? AND learning_date ';

                    var today = new Date();

                    switch(today.getDay()) {
                        case 0  :  // 일요일
                            last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 13 DAY) AND DATE_SUB(CURDATE(), INTERVAL 7 DAY)';
                            this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND CURDATE()';
                            break;
                        case 1  :  // 월요일
                            last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY)';
                            this_week_sql = this_week_sql + '= CURDATE()';
                            break;
                        case 2  :  // 화요일
                            last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 8 DAY) AND DATE_SUB(CURDATE(), INTERVAL 2 DAY)';
                            this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND CURDATE()';
                            break;
                        case 3  :  // 수요일
                            last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 9 DAY) AND DATE_SUB(CURDATE(), INTERVAL 3 DAY)';
                            this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 2 DAY) AND CURDATE()';
                            break;
                        case 4  :  // 목요일
                            last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 10 DAY) AND DATE_SUB(CURDATE(), INTERVAL 4 DAY)';
                            this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 DAY) AND CURDATE()';
                            break;
                        case 5  :  // 금요일
                            last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 11 DAY) AND DATE_SUB(CURDATE(), INTERVAL 5 DAY)';
                            this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 4 DAY) AND CURDATE()';
                            break;
                        case 6  :  // 토요일
                            last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 DAY) AND DATE_SUB(CURDATE(), INTERVAL 6 DAY)';
                            this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 5 DAY) AND CURDATE()';
                            break;
                    }

                    var sql = last_week_sql + ' UNION ALL ' + this_week_sql;
                    var params = [user, user];

                    connection.query(sql, params, function(err, result) {
                        if(err) {
                            logger.info('check members info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        
                        userinfo.userinfo.last_week_hanja = result[0]['COUNT(*)'];
                        userinfo.userinfo.this_week_hanja = result[1]['COUNT(*)'];

                        logger.info("user : " + user);
    
                        connection.release();
                        res.status(200).json(userinfo);
                    });
                });
            });

            logger.info('====================< memberinfo end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
})

// 로그인
app.post('/login', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var pswd = req.body.userinfo.pswd;

            logger.info('====================< login start >====================');

            pool.getConnection(function (err, connection) {

                var sql = 'SELECT COUNT(*) FROM members WHERE user=? AND pswd=?';
                var params = [user, pswd];

                connection.query(sql, params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0]['COUNT(*)'] == 0) {
                        logger.info('[non-added member]');
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }

                    var userinfo = new Object();
                    userinfo.user = user;
                    var jsonObj_total = new Object();
                    jsonObj_total.userinfo = userinfo;

                    logger.info('user : ' + user);
                    connection.release();
                    res.status(200).json(jsonObj_total);
                });
            });

            logger.info('====================< login end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 로그아웃
app.post('/logout', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;

            logger.info('====================< logout start >====================');

            pool.getConnection(function (err, connection) {
                var sql = 'SELECT COUNT(*) FROM members WHERE user=?';
                var params = [user];

                connection.query(sql, params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0]['COUNT(*)'] == 0) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }

                    logger.info('user : ' + user);
                    connection.release();
                    res.status(200).send('logout success');
                });
            });

            logger.info('====================< logout end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 친구목록
app.post('/friends_list', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            
            logger.info('====================< friends_list start >====================');
            
            pool.getConnection(function (err, connection) {

                var sql = 'SELECT friend_nickname FROM friends WHERE user=?';
                var params = [user];

                connection.query(sql, params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0] == undefined) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }
                    
                    var friends_list = new Object();
                    for(var i=0; i<result.length; i++) {
                        friends_list['f'+ (i+1)] = result[i].friend_nickname;
                    }

                    var jsonObj_total = new Object();
                    jsonObj_total.friends_list = friends_list;

                    logger.info("user : " + user);
                    connection.release();
                    res.status(200).json(jsonObj_total);
                });
            });

            logger.info('====================< friends_list end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 친구검색
app.post('/search_friend', function(req, res) {
    try {
        if(req.body.friendinfo) {
            var nickname = req.body.friendinfo.nickname;
            
            logger.info('====================< search_friend start >====================');

            pool.getConnection(function (err, connection) {
                var sql = 'SELECT COUNT(*) FROM members WHERE nickname=? and search_nickname=\'Y\'';
                var params = [nickname];

                connection.query(sql, params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0]['COUNT(*)'] == 0) {
                        logger.info('[non-added member]');
                        logger.info('nickname : ' + nickname);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }
                    logger.info('nickname : ' + nickname);

                    connection.release();
                    res.status(200).send('nickname exist');
                });
            });

            logger.info('====================< search_friend end >====================');            

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 친구추가
app.post('/add_friend', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var friend_nickname = req.body.userinfo.friend_nickname;
            
            logger.info('====================< add_friend start >====================');

            pool.getConnection(function (err, connection) {

                // user 검색
                var check_user_sql = 'SELECT nickname FROM members WHERE user=?';
                var check_user_params = [user];

                connection.query(check_user_sql, check_user_params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0] == undefined) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }
                    
                    var user_nickname = result[0].nickname;

                    // 존재하는 friend_nickname인지 검색
                    var check_friend_nickname_sql = 'SELECT user FROM members WHERE nickname=?';
                    var check_friend_nickname_params = [friend_nickname];

                    connection.query(check_friend_nickname_sql, check_friend_nickname_params, function(err, result) {
                        if(err) {
                            logger.info('check friend nickname info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        if(result[0] == undefined) {
                            logger.info('[non-added nickname]');
                            logger.info('nickname : ' + friend_nickname);
                            res.status(200).send('non-added friend nickname');
                            connection.release();
                            return ;
                        }

                        var friend_user = result[0].user;

                        // 이미 친구로 등록된 친구인지 검사
                        var check_already_friend_sql = 'SELECT COUNT(*) FROM friends WHERE user=? and friend_nickname=?'
                        var check_already_friend_params = [user, friend_nickname];

                        connection.query(check_already_friend_sql, check_already_friend_params, function(err, result) {
                            if(err) {
                                logger.info('check friend list info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }
                            if(result[0]['COUNT(*)'] != 0) {
                                logger.info('[already added friend]');
                                logger.info('user : ' + user)
                                logger.info('friend_nickname : ' + friend_nickname);
                                res.status(200).send('already added friend');
                                connection.release();
                                return ;
                            }

                            // 친구추가                           
                            var sql = 'INSERT INTO friends(user, friend_user, friend_nickname) VALUES(?, ?, ?), (?, ?, ?)';
                            var params = [user, friend_user, friend_nickname, friend_user, user, user_nickname];

                            connection.query(sql, params, function(err, result) {
                                if(err) {
                                    logger.info('add friends info query error!!');
                                    logger.info('reason : ', err);
                                    res.status(500).send(err);
                                    connection.release();
                                    throw err;
                                }
                                logger.info('user : ' + user);
                                logger.info('friend_user : ' + friend_user);

                                connection.release();
                                res.status(200).send('add friend list success');
                            });
                        });
                    });
                });
            });

            logger.info('====================< add_friend end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 가족등록
app.post('/add_family', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var friend_nickname = req.body.userinfo.friend_nickname;

            logger.info('====================< add_family start >====================');

            pool.getConnection(function (err, connection) {

                var check_user_sql = 'SELECT * FROM members WHERE user=?';
                var check_user_params = [user];

                connection.query(check_user_sql, check_user_params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    // 1. 존재하지 않는 user
                    if(result[0] == undefined) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }
                    // 2. user가 이미 가족이 있음 
                    if(result[0].family_nickname != undefined) {
                        logger.info('[already have family : user]');
                        logger.info('user : ' + user);
                        res.status(200).send('already have family : user');
                        connection.release();
                        return ;
                    }

                    var user_nickname = result[0].nickname;
                    var check_friend_sql = 'SELECT COUNT(*) FROM friends WHERE user=? AND friend_nickname=?';
                    var check_friend_params = [user, friend_nickname];

                    connection.query(check_friend_sql, check_friend_params, function(err, result) {
                        if(err) {
                            logger.info('check friends info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        // 3. 친구 목록에 없음
                        if(result[0]['COUNT(*)'] == 0) {  
                            logger.info('[non-added friend]');
                            logger.info('user : ' + user);
                            logger.info('friend nickname : ' + friend_nickname)
                            res.status(200).send('non-added friend');
                            connection.release();
                            return ;
                        }

                        var check_family_sql = 'SELECT * FROM members WHERE nickname=?';
                        var check_family_params = [friend_nickname];

                        connection.query(check_family_sql, check_family_params, function(err, result) {
                            if(err) {
                                logger.info('check members info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }
                            // 4. friend가 이미 가족 있음
                            if(result[0].family_nickname != undefined) {
                                logger.info('[already have family : friend user]');
                                logger.info('friend user : ' + friend_nickname);
                                res.status(200).send('already have family : friend user');
                                connection.release();
                                return ;
                            }
                            
                            // 5. 가족 등록
                            var family_user = result[0].user;
                            var family_nickname = result[0].nickname;

                            // user, family user 동시에 family_nickname 업데이트
                            // (참고 : https://marobiana.tistory.com/7)
                            var sql = 'UPDATE members ' + 
                                    'SET family_nickname=CASE ' + 
                                    'WHEN user=? THEN ? ' + 
                                    'WHEN user=? THEN ? ' +
                                    'END ' + 
                                    'WHERE user IN (?, ?)';
                            var params = [user, family_nickname, family_user, user_nickname, user, family_user];

                            connection.query(sql, params, function(err, result) {
                                if(err) {
                                    logger.info('add family info query error!!');
                                    logger.info('reason : ', err);
                                    res.status(500).send(err);
                                    connection.release();
                                    throw err;
                                }

                                logger.info('[add family member success]')
                                logger.info('user : ' + user);
                                logger.info('family_nickname : ' + family_nickname);
                                logger.info('user : ' + family_user);
                                logger.info('family_nickname : ' + user_nickname);

                                connection.release();
                                res.status(200).send('add family member success');
                            });
                        });
                    });
                });
            });

            logger.info('====================< add_family end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 가족진도
app.post('/familyinfo', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;

            logger.info('====================< familyinfo start >====================');

            pool.getConnection(function (err, connection) {
                var check_sql = 'SELECT * FROM members WHERE user=?';
                var check_params = [user];

                connection.query(check_sql, check_params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    // 1. 존재하지 않는 user
                    if(result[0] == undefined) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }
                    // 2. 등록된 가족 없음
                    if(result[0].family_nickname == null) {
                        logger.info('[no family info]');
                        logger.info('user : ' + user);
                        res.status(200).send('no family info');
                        connection.release();
                        return ;
                    }

                    var family_nickname = result[0].family_nickname;

                    var get_family_info_sql = 'SELECT * FROM members WHERE nickname=?'
                    var get_family_info_params = [family_nickname];

                    connection.query(get_family_info_sql, get_family_info_params, function(err, result) {
                        if(err) {
                            logger.info('check members info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }

                        var familyinfo = new Object();
                        familyinfo.familyinfo = result[0];

                        var family_user = result[0].user;

                        var last_week_sql = 'SELECT COUNT(*) FROM learning WHERE user=? AND learning_date ';
                        var this_week_sql = 'SELECT COUNT(*) FROM learning WHERE user=? AND learning_date ';
    
                        var today = new Date();
    
                        switch(today.getDay()) {
                            case 0  :  // 일요일
                                last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 13 DAY) AND DATE_SUB(CURDATE(), INTERVAL 7 DAY)';
                                this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND CURDATE()';
                                break;
                            case 1  :  // 월요일
                                last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY)';
                                this_week_sql = this_week_sql + '= CURDATE()';
                                break;
                            case 2  :  // 화요일
                                last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 8 DAY) AND DATE_SUB(CURDATE(), INTERVAL 2 DAY)';
                                this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND CURDATE()';
                                break;
                            case 3  :  // 수요일
                                last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 9 DAY) AND DATE_SUB(CURDATE(), INTERVAL 3 DAY)';
                                this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 2 DAY) AND CURDATE()';
                                break;
                            case 4  :  // 목요일
                                last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 10 DAY) AND DATE_SUB(CURDATE(), INTERVAL 4 DAY)';
                                this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 DAY) AND CURDATE()';
                                break;
                            case 5  :  // 금요일
                                last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 11 DAY) AND DATE_SUB(CURDATE(), INTERVAL 5 DAY)';
                                this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 4 DAY) AND CURDATE()';
                                break;
                            case 6  :  // 토요일
                                last_week_sql = last_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 DAY) AND DATE_SUB(CURDATE(), INTERVAL 6 DAY)';
                                this_week_sql = this_week_sql + 'BETWEEN DATE_SUB(CURDATE(), INTERVAL 5 DAY) AND CURDATE()';
                                break;
                        }
    
                        var sql = last_week_sql + ' UNION ALL ' + this_week_sql;
                        var params = [family_user, family_user];
    
                        connection.query(sql, params, function(err, result) {
                            if(err) {
                                logger.info('check members info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }
                            
                            familyinfo.familyinfo.last_week_hanja = result[0]['COUNT(*)'];
                            familyinfo.familyinfo.this_week_hanja = result[1]['COUNT(*)'];
    
                            logger.info("user : " + user);
                            logger.info("family user : " + family_user);
        
                            connection.release();
                            res.status(200).json(familyinfo);
                        });
                    });
                });
            });

            logger.info('====================< familyinfo end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 순위리스트
app.post('/rank_list', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var order = req.body.userinfo.order;

            if(order == 'hanja') {

                logger.info('====================< rank_list order by hanja start >====================');

                pool.getConnection(function (err, connection) {
                    
                    var check_friends_list_sql = 'SELECT COUNT(*) FROM friends WHERE user=?';
                    var check_friends_list_params = [user];

                    connection.query(check_friends_list_sql, check_friends_list_params, function(err, result) {
                        if(err) {
                            logger.info('check friends list info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        if(result[0]['COUNT(*)'] == 0) {
                            logger.info('[no friend info]');
                            logger.info('user : ' + user);
                            res.status(200).send('no friend info');
                            connection.release();
                            return ;
                        }
                        
                        var sql = 'SELECT m.nickname, m.learning_hanja ' +
                                'FROM members m, friends f ' +
                                'WHERE m.user = f.friend_user AND f.user=? ' +
                                'UNION ' +
                                'SELECT nickname, learning_hanja ' + 
                                'FROM members WHERE user=? ' +
                                'ORDER BY learning_hanja DESC, nickname ASC';
                        var params = [user, user];

                        connection.query(sql, params, function(err, result) {
                            if(err) {
                                logger.info('get rank list query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }

                            var rank_list = new Object();
                            var nickname = new Object();
                            var learning_hanja = new Object();

                            for(var i=0; i<result.length; i++) {
                                nickname[i+1] = result[i].nickname;
                                learning_hanja[i+1] = result[i].learning_hanja;
                            }
                            rank_list.nickname = nickname;
                            rank_list.learning_hanja = learning_hanja;

                            var jsonObj_total = new Object();
                            jsonObj_total.rank_list = rank_list;

                            logger.info('user : ' + user);
                            logger.info('order : ' + order);

                            res.status(200).json(jsonObj_total);
                            connection.release();
                        });
                    });
                });

                logger.info('====================< rank_list order by hanja end >====================');

            } else if(order == 'theme') {

                logger.info('====================< rank_list order by theme start >====================');

                pool.getConnection(function (err, connection) {
                    
                    var check_friends_list_sql = 'SELECT COUNT(*) FROM friends WHERE user=?';
                    var check_friends_list_params = [user];

                    connection.query(check_friends_list_sql, check_friends_list_params, function(err, result) {
                        if(err) {
                            logger.info('check friends list info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        if(result[0]['COUNT(*)'] == 0) {
                            logger.info('[no friend info]');
                            logger.info('user : ' + user);
                            res.status(200).send('no friend info');
                            connection.release();
                            return ;
                        }
                        
                        var sql = 'SELECT m.nickname, m.learning_theme ' +
                                'FROM members m, friends f ' +
                                'WHERE m.user = f.friend_user AND f.user=? ' +
                                'UNION ' +
                                'SELECT nickname, learning_theme ' + 
                                'FROM members WHERE user=? ' +
                                'ORDER BY learning_theme DESC, nickname ASC';
                        var params = [user, user];

                        connection.query(sql, params, function(err, result) {
                            if(err) {
                                logger.info('get rank list query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }

                            var rank_list = new Object();
                            var nickname = new Object();
                            var learning_theme = new Object();

                            for(var i=0; i<result.length; i++) {
                                nickname[i+1] = result[i].nickname;
                                learning_theme[i+1] = result[i].learning_theme;
                            }
                            rank_list.nickname = nickname;
                            rank_list.learning_theme = learning_theme;

                            var jsonObj_total = new Object();
                            jsonObj_total.rank_list = rank_list;

                            logger.info('user : ' + user);
                            logger.info('order : ' + order);

                            res.status(200).json(jsonObj_total);
                            connection.release();
                        });
                    });
                });

                logger.info('====================< rank_list order by theme end >====================');

            } else {
                res.status(500).send('json object order is null');
                logger.info('json object order is null');
            }
        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 사물인식 요청
app.post('/obj_detect', function(req, res) {
    try {
        const upload = multer({
            storage: multer.diskStorage({
                destination: function (req, file, cb) {
                    var cmd = req.body.cmd;
                    var user = req.body.user;
                    
                    if(cmd == 'detect') {
                        cb(null, front_img_dir + user + '/tmp');
                    } else if(cmd == 'save') {
                        cb(null, front_img_dir + user);
                    } else {
                        res.status(500).send('form data cmd is null');
                        logger.info('form data cmd is null');
                    }           
                },
                filename: function (req, file, cb) {
                    var theme = req.body.theme;
                    var word = req.body.word;
                    cb(null, theme + '_' + word + '.jpg');
                }
            }),
            fileFilter: function (req, file, cb) {
                var theme = req.body.theme;
                var word = req.body.word;

                pool.getConnection(function (err, connection) {
                    
                    var get_answer_sql = 'SELECT word_hanja FROM words WHERE theme=? AND word=?';
                    var get_answer_params = [theme, word];

                    connection.query(get_answer_sql, get_answer_params, function(err, result, fields) {
                        if(err) {
                            logger.info('get answer info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        if(result[0] == undefined) {
                            cb(null, false);
                            logger.info('[non-added word]');
                            logger.info('theme : ' + theme);
                            logger.info('word : ' + word);
                            res.status(200).send('non-added word');
                            connection.release();
                            return ;
                        }
                        cb(null, true);
                    });
                });
            }
        }).single('img');

        if(req.body) { 
            upload(req, res, function(err) {
                if(err instanceof multer.MulterError) {  // file fieldname error(file이 있을 때만 걸림)
                    logger.info('file field-name is null or incorrect');
                    logger.info('reason : ', err);
                    res.status(500).send('file field-name is null or incorrect');
                    connection.release();
                    throw err;
                }
                if (!req.file){
                    logger.info('[image is null]');
                    logger.info('user : ' + user);
                    res.status(200).send('image is null');
                    connection.release();
                    return ;
                }

                var cmd = req.body.cmd;
                var user = req.body.user;
                var theme = req.body.theme;
                var word = req.body.word;

                if(cmd == 'detect') {

                    logger.info('================< obj_detect : detect object start >========================');

                    pool.getConnection(function (err, connection) {
                        if(err) {
                            logger.info('connection pool is full in serversinfo!!');
                            logger.info('reason : ', err);
                            res.status(500).send('connection pool is full in serversinfo!!');
                            connection.release();
                            throw err;
                        }
                        // 1. api 초기화
                        // vision.init(jsonfile);

                        // 2. 요청 페이로드 작성  // requtil.createRequest('D:/Dev/js/Hanja/images/1.jpg')
                        var d = requtil.createRequests().addRequest(
                            requtil.createRequest(req.file.path)
                            .withFeature('LABEL_DETECTION', 5)
                            .build());

                        // 3. api 서버로 쿼리 전송
                        vision.query(d, function(err, r, d){
                            if(err) {
                                logger.info('ERROR:', err);
                                logger.info('google vision api error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }

                            var detect_result = new Array();
                            for(var i=0; i<d.responses[0].labelAnnotations.length; i++) {
                                detect_result.push(d.responses[0].labelAnnotations[i].description);
                            }
                            
                            var get_word_sql = 'SELECT word_en FROM words WHERE theme=? AND word=?';
                            var get_word_params = [theme, word];

                            connection.query(get_word_sql, get_word_params, function(err, result, fields) {
                                if(err) {
                                    logger.info('get word info query error!!');
                                    logger.info('reason : ', err);
                                    res.status(500).send(err);
                                    connection.release();
                                    throw err;
                                }
                                if(result[0] == undefined) {
                                    logger.info('[non-added word]');
                                    logger.info('user : ' + user);
                                    res.status(200).send('non-added word');
                                    connection.release();
                                    return ;                           
                                }
                                var split_word = result[0].word_en.split('|');

                                for(var i=0; i<split_word.length; i++) {
                                    for(var j=0; j<detect_result.length; j++) {
                                        if(split_word[i] == detect_result[j]) {
                                            logger.info('[matching]');
                                            logger.info('user : ' + user);
                    
                                            connection.release();
                                            res.status(200).send('matching');
                                            return ;
                                        }
                                    }
                                }
                                logger.info('[no-matching]');
                                logger.info('user : ' + user);
        
                                connection.release();
                                res.status(200).send('no-matching');
                            });
                        });
                    });

                    logger.info('================< obj_detect : detect object end >========================');

                } else if(cmd == 'save') {

                    logger.info('================< obj_detect : save image start >========================');

                    pool.getConnection(function (err, connection) {
                        if(err) {
                            logger.info('connection pool is full in serversinfo!!');
                            logger.info('reason : ', err);
                            res.status(500).send('connection pool is full in serversinfo!!');
                            connection.release();
                            throw err;
                        }
                        var check_sql = 'SELECT COUNT(*) FROM learning WHERE user=? AND theme=? AND word=?';
                        var check_params = [user, theme, word];

                        connection.query(check_sql, check_params, function(err, result, fields) {
                            if(err) {
                                logger.info('add learning info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }
                            // db에 학습정보 이미 있고 이미지만 바꿀 때
                            if(result[0]['COUNT(*)'] != 0) {
                                var today = new Date();
                                var learning_date = today.toFormat('YYYY-MM-DD');

                                var update_date_sql = 'UPDATE learning SET learning_date=? WHERE user=? and theme=? and word=?';
                                var update_date_params = [learning_date, user, theme, word];


                                connection.query(update_date_sql, update_date_params, function(err, result, fields) {
                                    if(err) {
                                        logger.info('update learning date info query error!!');
                                        logger.info('reason : ', err);
                                        res.status(500).send(err);
                                        connection.release();
                                        throw err;
                                    }
                                    logger.info('[update learing info]');
                                    logger.info('user : ' + user);
                                    res.status(200).send('save image success');
                                    connection.release();
                                    return ;    
                                });
                            // db에 학습정보 없어서 새로운 정보 추가할 때
                            } else {
                                var get_answer_sql = 'SELECT word_hanja FROM words WHERE theme=? AND word=?';
                                var get_answer_params = [theme, word];

                                connection.query(get_answer_sql, get_answer_params, function(err, result, fields) {
                                    if(err) {
                                        logger.info('get answer info query error!!');
                                        logger.info('reason : ', err);
                                        res.status(500).send(err);
                                        connection.release();
                                        throw err;
                                    }
    
                                    var answer = result[0].word_hanja;
                                    var img_url = front_img_url + user +'/'+theme+'_'+word+'.jpg';
    
                                    var today = new Date();
                                    var learning_date = today.toFormat('YYYY-MM-DD');
    
                                    var insert_learning_sql = 'INSERT INTO learning(user, theme, word, img_url, answer, learning_date) VALUES(?, ?, ?, ?, ?, ?)';
                                    var insert_learning_params = [user, theme, word, img_url, answer, learning_date];
    
                                    connection.query(insert_learning_sql, insert_learning_params, function(err, result, fields) {
                                        if(err) {
                                            logger.info('add learning info query error!!');
                                            logger.info('reason : ', err);
                                            res.status(500).send(err);
                                            connection.release();
                                            throw err;
                                        }
                
                                        logger.info('[add new learning info]');
                                        // logger.info('user : ' + user);
                
                                        // connection.release();
                                        res.status(200).send('save image success');
    
                                        var update_member_info_sql = 'UPDATE members SET learning_hanja = learning_hanja + 1, coin = coin + 1 WHERE user=?';
                                        var update_member_info_params = [user];
    
                                        connection.query(update_member_info_sql, update_member_info_params, function(err, result, fields) {
                                            if(err) {
                                                logger.info('update member info query error!!');
                                                logger.info('reason : ', err);
                                                res.status(500).send(err);
                                                connection.release();
                                                throw err;
                                            }

                                            logger.info('[update member info (learning_hanja, coin) success]');                                       

                                            //+++
                                            // 지금 학습한 단어가 속한 테마의 학습 한자 수 가져오기 
                                            var get_learning_hanja_cnt_sql = 'SELECT COUNT(*) FROM learning WHERE user=? AND theme=?';
                                            var get_learning_hanja_cnt_params = [user, theme];

                                            connection.query(get_learning_hanja_cnt_sql, get_learning_hanja_cnt_params, function(err, result) {
                                                if(err) {
                                                    logger.info('get theme count info query error!!');
                                                    logger.info('reason : ', err);
                                                    res.status(500).send(err);
                                                    connection.release();
                                                    throw err;
                                                }
                                                if(result[0]['COUNT(*)'] < 10) {
                                                    logger.info('[update learning_theme info pass]');
                                                    logger.info('user : ' + user);
                                                    connection.release();
                                                    return;
                                                }

                                                // learning_theme 업데이트
                                                var update_learning_theme_sql = 'UPDATE members SET learning_theme=learning_theme+1 WHERE user=?'
                                                var update_learning_theme_params = [user];

                                                connection.query(update_learning_theme_sql, update_learning_theme_params, function(err, result) {
                                                    if(err) {
                                                        logger.info('update learning_theme info query error!!');
                                                        logger.info('reason : ', err);
                                                        res.status(500).send(err);
                                                        connection.release();
                                                        throw err;
                                                    }

                                                    logger.info('[update learning_theme info success]');
                                                    logger.info('user : ' + user);
                                                    connection.release();
                                                });
                                            });
                                            
                                            // //---
                                            
                                        });
                                    });
                                });
                            }                          
                        });
                    });

                    logger.info('================< obj_detect : save image end >========================');

                } else {
                    res.status(500).send('form data cmd is null');
                    logger.info('form data cmd is null');
                } 
            });
        } else {
            res.status(500).send('form data is null');
            logger.info('form data is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 단어장 리스트
app.post('/learning_list', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var theme = req.body.userinfo.theme;

            logger.info('====================< learning_list start >====================');

            pool.getConnection(function (err, connection) {

                var check_sql = 'SELECT COUNT(*) FROM members WHERE user=?';
                var check_params = [user];
                
                connection.query(check_sql, check_params, function(err, result) {
                    if(err) {
                        logger.info('check members info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0]['COUNT(*)'] == 0) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }
                    
                    var sql = 'SELECT * FROM learning WHERE user=? AND theme=?';
                    var params = [user, theme];

                    connection.query(sql, params, function(err, result) {
                        if(err) {
                            logger.info('get learing info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        if(result[0] == undefined) {
                            logger.info('[no learning info]');
                            logger.info('user : ' + user);
                            res.status(200).send('no learning info');
                            connection.release();
                            return ;
                        }

                        var learninginfo = new Object();

						for(var i=0; i<10; i++) {
                            learninginfo['img'+ (i+1)] = 'null';
                        }
                        for(var i=0; i<result.length; i++) {
                            for(var j=0; j<10; j++) {
                                if(result[i].word == 'v'+(j+1)) {
                                    learninginfo['img'+ (j+1)] = result[i].img_url;
                                    break;
                                }        
                            }                          
                        }
                        var jsonObj_total = new Object();
                        jsonObj_total.learninginfo = learninginfo;

                        logger.info("user : " + user);

                        connection.release();
                        res.status(200).json(jsonObj_total);
                    });
                });
            });
            
            logger.info('====================< learning_list end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
		res.status(500).send(exception);
        logger.info(exception);
    }
});

// 테마 진행상태
app.post('/themeinfo', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;

            logger.info('====================< themeinfo start >====================');

            pool.getConnection(function (err, connection) {

                // 중복제거해서 theme 개수 카운트
                var check_theme_cnt_sql = 'SELECT DISTINCT theme FROM words';

                connection.query(check_theme_cnt_sql, function(err, result) {
                    if(err) {
                        logger.info('get theme count info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0] == undefined) {
                        logger.info('[no word info]');
                        logger.info('user : ' + user);
                        res.status(200).json(jsonObj);
                        connection.release();
                        return ;
                    }

                    var sql = '';
                    var params = [];
                    
                    for(var i=0; i<result.length; i++) {                        
                        if(i < result.length-1) {
                            sql = sql + 'SELECT theme, COUNT(theme) FROM learning WHERE user = ? AND theme = \'t' + (i+1) + '\' UNION ALL ';
                        } else {
                            sql = sql + 'SELECT theme, COUNT(theme) FROM learning WHERE user = ? AND theme = \'t' + (i+1) + '\'';
                        }
                        params.push(user); 
                    }

                    connection.query(sql, params, function(err, result) {
                        if(err) {
                            logger.info('get theme info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        
                        var themeinfo = new Object();

                        for(var i=0; i<result.length; i++) {
                            themeinfo['t' + (i+1)] = result[i]['COUNT(theme)'];
                        }

                        // userinfo json 오브젝트 만들기
                        var get_member_info_sql = 'SELECT coin, learning_hanja, learning_theme FROM members WHERE user=?';
                        var get_member_info_params = [user];
                        
                        connection.query(get_member_info_sql, get_member_info_params, function(err, result) {
                            if(err) {
                                logger.info('get members info query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }
                            if(result[0] == undefined) {
                                logger.info('[non-added member]');
                                logger.info('user : ' + user);
                                res.status(200).send('non-added member');
                                connection.release();
                                return ;
                            }

                            var userinfo = new Object();

                            userinfo.coin = result[0].coin;
                            userinfo.learning_hanja = result[0].learning_hanja;
                            userinfo.learning_theme = result[0].learning_theme;

                            var jsonObj_total = new Object();

                            jsonObj_total.userinfo = userinfo;
                            jsonObj_total.themeinfo = themeinfo;

                            logger.info("user : " + user);

                            connection.release();
                            res.status(200).json(jsonObj_total);
                        });
                    });
                });
            });

            logger.info('====================< themeinfo end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 퀴즈 리스트
app.post('/quiz_list', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;

            logger.info('====================< quiz list start >====================');

            pool.getConnection(function (err, connection) {

                var check_learning_sql = 'SELECT COUNT(*) FROM learning WHERE user=?';
                var check_learning_params = [user];

                connection.query(check_learning_sql, check_learning_params, function(err, result) {
                    if(err) {
                        logger.info('check learning info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0]['COUNT(*)'] < 10) {
                        logger.info('[not enough learning info]');
                        logger.info('user : ' + user);
                        res.status(200).send('not enough learning info');
                        connection.release();
                        return ;
                    }

                    var sql = 'SELECT img_url, answer FROM learning WHERE user=?';
                    var params = [user];

                    connection.query(sql, params, function(err, result) {
                        if(err) {
                            logger.info('select quiz list query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }
                        
                        // 배열 순서 랜덤으로 섞어서 중복 없이 10개 뽑기
                        var randArr = [];
                        var mkrand_img = function(arr) {
                            var newArr = [];

                            for(var i=0; i<10; i++) {
                                var x = parseInt(Math.random() * arr.length);  // 0 ~ (배열길이-1)
                                newArr[i] = arr[x];
                                arr.splice(x, 1); // x번째 인덱스 삭제
                            } 
                            return newArr;
                        };
                        randArr = mkrand_img(result);

                        var get_word_sql = 'SELECT word_hanja FROM words';

                        connection.query(get_word_sql, function(err, result) {
                            if(err) {
                                logger.info('select quiz list query error!!');
                                logger.info('reason : ', err);
                                res.status(500).send(err);
                                connection.release();
                                throw err;
                            }

                            var wordArr = [];
                            for(var i=0; i<result.length; i++) {
                                wordArr.push(result[i].word_hanja);
                            }
                            
                            var add_ex = function(randArr, arr) {
                                for(var i=0; i<10; i++) {
                                    var tmpArr = wordArr.slice();
                                    var newArr = [];
                                    var answer = randArr[i].answer;

                                    while(newArr.length != 3) {
                                        var x = parseInt(Math.random() * (arr.length- newArr.length));  // 0 ~ (배열길이-1)

                                        if(tmpArr[x] != answer) {
                                            newArr.push(tmpArr[x]);
                                            tmpArr.splice(x, 1); // x번째 인덱스 삭제
                                        } else {
                                            tmpArr.splice(x, 1); // x번째 인덱스 삭제
                                        }
                                        
                                    }
 
                                    randArr[i].ex1 = newArr[0];
                                    randArr[i].ex2 = newArr[1];
                                    randArr[i].ex3 = newArr[2];                                 
                                }
                                return randArr;
                            }
                            var totalArr = add_ex(randArr, result);

                            // json 오브젝트 만들기
                            var quiz_list = new Object();
                            
                            for(var i=0; i<totalArr.length; i++) {
                                quiz_list['q' + (i+1)] = totalArr[i];
                            }

                            var jsonObj_total = new Object();
                            jsonObj_total.quiz_list = quiz_list;

                            logger.info('user : ' + user);

                            connection.release();
                            res.status(200).json(jsonObj_total);
                        });
                    });                    
                });    
            });

            logger.info('====================< quiz list end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 퀴즈 완료 후
app.post('/quiz_end', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var solved_problem = req.body.userinfo.solved_problem;
            var use_hint = req.body.userinfo.use_hint;

            logger.info('====================< quiz_end start >====================');

            var check_user_sql = 'SELECT * FROM members WHERE user=?';
            var check_user_params = [user];

            pool.getConnection(function (err, connection) {
                connection.query(check_user_sql, check_user_params, function(err, result) {
                    if(err) {
                        logger.info('check user info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0] == undefined) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }

                    var coin = null;
                    if(use_hint == 'Y') { 
                        if(solved_problem == 10) {
                            coin = 7;
                        } else {
                            coin = -3;
                        }
                    } else if(use_hint == 'N') {
                        if(solved_problem == 10) {
                            coin = 10;
                        } else {
                            coin = 0;
                        }
                    } else {
                        res.status(500).send('json object is null');
                        logger.info('json object is null');
                    }              

                    var sql = 'UPDATE members SET ' + 
                            'coin = coin + ?, ' + 
                            'quiz_count = quiz_count + 1, ' + 
                            'solved_problem = solved_problem + ?, ' + 
                            'answer_rate = solved_problem / (quiz_count * 10) * 100 ' + 
                            'WHERE user=?';
                    var params = [coin, solved_problem, user];

                    connection.query(sql, params, function(err, result) {
                        if(err) {
                            logger.info('update user quiz info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }

                        logger.info('[update answer_rate info success]');
                        logger.info('user : ' + user);

                        connection.release();
                        res.status(200).send('update answer_rate info success');
                        return ;
                    });
                });
            });

            logger.info('====================< quiz_end end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 닉네임 검색허용
app.post('/set_nickname', function(req, res) {
    try {
        if(req.body.userinfo) {
            var user = req.body.userinfo.user;
            var search_nickname = req.body.userinfo.search_nickname;

            logger.info('====================< set_nickname start >====================');

            pool.getConnection(function (err, connection) {
                var check_sql = 'SELECT COUNT(*) FROM learning WHERE user=?';
                var check_params = [user];

                connection.query(check_sql, check_params, function(err, result) {
                    if(err) {
                        logger.info('check member info query error!!');
                        logger.info('reason : ', err);
                        res.status(500).send(err);
                        connection.release();
                        throw err;
                    }
                    if(result[0]['COUNT(*)'] == 0) {
                        logger.info('[non-added member]');
                        logger.info('user : ' + user);
                        res.status(200).send('non-added member');
                        connection.release();
                        return ;
                    }

                    var sql = 'UPDATE members SET search_nickname=? WHERE user=?';
                    var params = [search_nickname, user];
                    
                    connection.query(sql, params, function(err, result) {
                        if(err) {
                            logger.info('update search_nickname info query error!!');
                            logger.info('reason : ', err);
                            res.status(500).send(err);
                            connection.release();
                            throw err;
                        }

                        logger.info('[update search_nickname setup success]');
                        logger.info('user : ' + user);

                        connection.release();
                        res.status(200).send('update search_nickname setup success');
                    });
                });
            });

            logger.info('====================< set_nickname end >====================');

        } else {
            res.status(500).send('json object is null');
            logger.info('json object is null');
        }
    } catch(exception) {
        res.status(500).send(exception);
        logger.info(exception);
    }
});

// 서버 실행
http.createServer(app).listen(app.get('port'), function() {

    logger.info('server listening on port ' + app.get('port'));
    
    // database processing start
    mysql_dbc = require('./db/db_conn')();
    pool = mysql_dbc.init_pool(); // connection pool 이용

    if (pool)
        logger.info('database init success!!');
    else
        logger.info('database init fail!!');

    // 설정파일 읽기
    var dir_conf = fs.readFileSync(__dirname + '/config/dir_conf.json');
    var jsonObj = JSON.parse(dir_conf);

    // 내 컴퓨터
    // front_img_url = jsonObj.local.img_url;
    // front_img_dir = jsonObj.local.img_dir;
    // google_vision_json_dir = jsonObj.local.google_vision_json_dir;

    // 교수님 컴퓨터
    front_img_url = jsonObj.real.img_url;
    front_img_dir = jsonObj.real.img_dir;
    google_vision_json_dir = jsonObj.real.google_vision_json_dir;

    // google vision api key 받아오기
    vision.init(google_vision_json_dir);
});

process.on('uncaughtException', function(err) {
    logger.error(err.stack);
});
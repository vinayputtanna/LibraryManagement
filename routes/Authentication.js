var mysql = require('mysql');
var session = require('express-session');
var passport = require('passport');
const bcrypt = require('bcrypt');
var expressValidator = require('express-validator');
var db = require('../db');


exports.signup = function(req, res){
	var emailid = req.body.emailid;
	var pwd = req.body.password;	
	var pin = req.body.pin;
	
	bcrypt.hash(pwd, 10, function(err, hash) {
		db.query('SELECT * FROM Login_Master where emailid =?',emailid, function(err, rows, fields){
	    	if(err){
				console.log("error while performing query");
				res.json({ status: 'failure' });
			}   
	    	if (rows.length == 0){
	    		db.query("insert into Login_Master (emailid, password, pin) VALUES (?,?,?)", [emailid, hash, pin], function(err,result){
					if(err){
						console.log("unable to insert "+ hash);
						console.log(err);
						res.json({ status: 'failure' });
						return;
					}else{
						res.json({ status: 'success' });						
					}				
				});	
			}else{
				res.json({ status: 'failure' });
			}
	    });
	});
};


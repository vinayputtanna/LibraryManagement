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
	    	// if email id is unique this if will work
	    	if (rows.length == 0){
	    		console.log('email is unique');
	    		//check if pin is unique or not
	    			db.query('SELECT * FROM Login_Master where pin =?',pin, function(err, rows, fields){
				    	    	if(err){
				    				console.log("error while performing query");
				    				res.json({ status: 'failure' });
				    			}   
	    	    	// if pin is unique this if will work
				    	    	if (rows.length == 0){
				    	    		console.log('pin is unique and status is success');
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
				    	    
				    	    	}
				    	    	else{
				    	    		console.log('status is pinnotunique');
				    	    		res.json({ status: 'pinnotunique' });
				    	    	}
	    			});
			}else{
				console.log('status is emailnotunique');
				res.json({ status: 'emailnotunique' });
			}
	    });
	});
};


exports.login = function(req, res){
	var emailid = req.body.emailid;
	var password = req.body.password;	
	
	db.query('SELECT * FROM Login_Master where emailid =?',emailid, function(err, rows, fields){
		if(err){
				console.log("error while performing query");
			}
		if (rows.length == 0){
				res.json({ status: 'failure' });
			}else{					
				bcrypt.compare(password, rows[0].password, function(err, resp) {
					  if(resp == true) {						 
						  res.json({ status: 'success' });
					  }else{
						  res.json({ status: 'failure' });
					  } 
					});																										
			}
	});
};

exports.verification = function(req, res){
	var emailid = req.body.emailid;
	db.query('UPDATE Login_Master SET verified = TRUE WHERE emailid = ?',emailid, function(err, rows, fields){
		if(err){
			console.log("error while performing query");
		}
		else{
			console.log("status = success sent");
			res.json({ status: 'success' });
		}
	});
	
};


exports.checkifverified = function(req, res){
	console.log("check if verified");
	var emailid = req.body.emailid;
	db.query('SELECT * FROM Login_Master WHERE emailid =?',emailid, function(err, rows, fields){
		if(err){
			console.log("error while performing query");
		}
		if (rows.length == 0){
			console.log("error in logic");
		}
		else{
			console.log(typeof(rows[0].verified));
			res.json({ status: rows[0].verified });
		}
	});
	
};



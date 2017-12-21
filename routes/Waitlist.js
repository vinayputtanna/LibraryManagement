/**
 * http://usejsdoc.org/
 */

var mysql = require('mysql');
var session = require('express-session');
var passport = require('passport');
var expressValidator = require('express-validator');
var dateTime = require('node-datetime');
var db = require('../db');

exports.add_to_waitlist = function(req, res){
	var book_id = req.body.book_id;
	var patron_email_id = req.body.patron_emailid;
	console.log(book_id + " : " + patron_email_id);
	var dt = dateTime.create();
	dt.format('m/d/Y H:M:S');
	var createdate = new Date(dt.now());		
	
	db.query("SELECT * FROM Waitlist Where patron_emailid = ? AND book_id = ?", [patron_email_id,book_id ], function(err, rows, fields){
		if(err){
			console.log("error in waitlist query");			
		}else if(rows.length == 0){
			db.query("insert into Waitlist (patron_emailid, book_id, entry_datetime) VALUES (?,?,?)", [patron_email_id,book_id,createdate], function(err,result){
				if(err){
					console.log("error in query of waitlist insertion");
					console.log(err);
					res.json({ status: 'failure' });
					return;
				}else{			
					res.json({ status: 'success' });						
				}				
			});	
		}
		else if(rows.length >0){
			res.json({ status: 'repeat' });
		} else{
			res.json({ status: 'failure' });
		}
	});
	
}




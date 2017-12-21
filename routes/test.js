/**
 * http://usejsdoc.org/
 */

var mysql = require('mysql');
var session = require('express-session');
var db = require('../db');
var dateTime = require('node-datetime');



exports.notify_book_available = function(req,res){
	console.log("testing");
	var number = req.body.number;
	if(number==25){
		res.json({status: '5'});
		
	}else if(number == 26){
		res.json({status: '4'});
	}else if(number == 27){
		res.json({status: '3'});
	}else if(number == 28){
		res.json({status: '2'});
	}else if(number == 29){
		res.json({status: '1'});
	}
	
};
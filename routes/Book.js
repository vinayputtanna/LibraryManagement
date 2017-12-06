var mysql = require('mysql');
var session = require('express-session');
var passport = require('passport');
var expressValidator = require('express-validator');
var db = require('../db');


exports.addbook = function(req, res){
	
	var book_name = req.body.book_name;
	var author = req.body.author_name;
	var call_number = req.body.call_number;
	var publisher = req.body.publisher;
	var year_of_publication = req.body.year_published;
	var location = req.body.location;
	var number_of_copies = req.body.number_of_copies;
	var current_status = req.body.status;
	var keywords = req.body.keywords;
	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
	 	if(err){
			console.log("error while performing query");
			res.json({ status: 'failure' });
		}   
	    	if (rows.length == 0){
	    		db.query("insert into Book_Master (book_name, author, call_number, publisher, year_of_publication, location, number_of_copies, current_status, keywords) VALUES (?,?,?,?,?,?,?,?,?)", [book_name, author, call_number, publisher, year_of_publication, location, number_of_copies, current_status, keywords], function(err,result){
	    			if(err){
	    				console.log("unable to insert data in book table");
	    				console.log(err);
	    				res.json({ status: 'failure' });
	    				return;
	    			}else{
	    				console.log(book_name +" : " + author+" : " + call_number+" : " + publisher+" : " + year_of_publication+" : " + location+" : " + number_of_copies+" : " + current_status+" : " + keywords)
	    				res.json({ status: 'added' });						
	    			}				
	    		});	
	    	}
	    	else{
	    		var no_of_copies= rows[0].number_of_copies;
	    		var new_copies = +number_of_copies + +no_of_copies;
	    		
	    		db.query("UPDATE Book_Master SET number_of_copies = ? Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [new_copies, book_name, author, publisher, year_of_publication], function(err,result){
	    			if(err){
	    				console.log("error while performing query");
	    				res.json({ status: 'failure' });
	    			}   
	    			else{
	    				res.json({ status: 'update' });		
	    			}
	    		});
	    	}
	});
	
}

exports.updatebook = function(req, res){
	var book_name = req.body.book_name;
	var author = req.body.author_name;
	var call_number = req.body.call_number;
	var publisher = req.body.publisher;
	var year_of_publication = req.body.year_published;
	var location = req.body.location;
	var number_of_copies = req.body.number_of_copies;
	var current_status = req.body.status;
	var keywords = req.body.keywords;
	var id = req.body.id;
	
	db.query("UPDATE Book_Master SET book_name = ? , author= ? , publisher= ? , year_of_publication= ?, location =?, number_of_copies =? , current_status=? , keywords=? WHERE id= ?", [book_name, author, publisher, year_of_publication, location, number_of_copies, current_status, keywords,id ], function(err,result){
		if(err){
			console.log("error while performing query");
			res.json({ status: 'failure' });
		}   
		else{
			res.json({ status: 'success' });		
		}
		
	});
}


exports.deletebook = function(req, res){

	var id = req.body.id;
	db.query("SELECT * FROM Book_Master Where bookid = ?", [id], function(err, rows, fields){
		
		if(err){
			console.log("error while performing query");
			res.json({ status: 'failure' });
		} 
		//book not found
	    	if (rows.length == 0){
	    		res.json({ status: 'booknotfound' });
	    	}else if(rows.length >1){
	    		console.log("There can not be more than 1 book associated to 1 book id");
	    	}
	    	else{
	    		var no_of_copies= rows[0].number_of_copies;
	    		var current_status = rows[0].current_status;
	    		if(no_of_copies == current_status){
	    			res.json({ status: 'success' });
	    		}
	    		else{
	    			res.json({ status: 'borrowed' });
	    		}
	    	}
		
	});
	
}








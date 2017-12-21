var mysql = require('mysql');
var session = require('express-session');
var db = require('../db');

exports.addtocart = function(req,res){
	var patron_emailid = req.body.patron_emailid;
	var book_name = req.body.book_name;
	var author = req.body.author_name;
	var call_number = req.body.call_number;
	var publisher = req.body.publisher;
	var year_of_publication = req.body.year_published;
	var location = req.body.location;
	var number_of_copies = req.body.number_of_copies;
	var current_status = req.body.status;
	var keywords = req.body.keywords;
	
	function getBook_id(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            book_id = rows[0].book_id;
		            resolve(rows[0].book_id);
	    		});
	    });
	}
	
	function check_if_book_is_there_in_reserved_table(book_id, patron_emailid)
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Reserved Where book_id = ? AND patron_emailid= ?", [book_id, patron_emailid], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }		            
		            resolve(rows.length);
	    		});
	    });
	}
	
	
	
	
	
	
	function insert_into_cart(book_id, patron_emailid)
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("insert into Cart (book_id, patron_emailid) VALUES (?,?)", [book_id, patron_emailid], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }		            
		            resolve("inserted");
	    		});
	    });
	}
	
	
	function check_if_book_is_there_in_cart(book_id)
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Cart Where book_id = ?", [book_id], function(err, rows, fields){		            
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }		            
		            resolve(rows.length);
	    		});
	    });
	}
	
	
	
	if(current_status == 0){		
		getBook_id(book_name,author, publisher, year_of_publication ).then(function(book_id){
			check_if_book_is_there_in_reserved_table(book_id, patron_emailid).then(function(length) {
				if(length == 0){
					res.json({ status: 'notavail' });
					return;
				}else if(length >0){
					check_if_book_is_there_in_cart(book_id).then(function(cart_length){
						if(cart_length ==0){
							insert_into_cart(book_id, patron_emailid).then(function(status){
								if(status == "inserted"){
									res.json({ status: 'success_reserved'});
				    					return;
								}
								else{
									res.json({ status: 'failure' });
				    					return;
								}
							});
						}else if(cart_length>0){
							res.json({ status: 'failure_reserved' });
	    						return;
						}
					});
				}			
			});
		});		
	}else if(current_status >0){
		db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
			if(err){
				console.log("error while performing query");
				res.json({ status: 'failure' });
			}  
			else if (rows.length == 0){
				res.json({ status: 'failure' });
	    		}
			else if(rows.length == 1){
				var book_id= rows[0].book_id;
				
				db.query("SELECT * FROM Cart Where patron_emailid = ?", [patron_emailid], function(err, rows, fields){
					if(err){
		    				console.log("error while fetching data from cart");
		    				console.log(err);
		    				res.json({ status: 'failure' });
		    				return;
	    				}
					else if (rows.length >= 3 ){
						res.json({ status: 'limit' });
					}
					else{
						db.query("SELECT * FROM Cart Where book_id = ?", [book_id], function(err, rows, fields){
							console.log(rows.length + " ::::::: !!!!");
							if(err){
				    				console.log("error while fetching data from cart");
				    				console.log(err);
				    				res.json({ status: 'failure' });
				    				return;
		    					}
							else if(rows.length > 0){
								res.json({ status: 'repeat' });
								return;
							}
							else if(rows.length == 0){								
								db.query("SELECT * FROM Issued Where book_id = ? AND patron_emailid = ?", [book_id, patron_emailid], function(err, rows, fields){
									if(err){
										console.log("error while fetching data from cart");
						    				console.log(err);
						    				res.json({ status: 'failure' });
						    				return;
									}else if(rows.length == 0){										
										db.query("insert into Cart (book_id, patron_emailid) VALUES (?,?)", [book_id, patron_emailid], function(err,result){
								    			if(err){
								    				console.log("unable to insert data in book table");
								    				console.log(err);
								    				res.json({ status: 'failure' });
								    				return;
								    			}else{
								    				res.json({ status: 'success' });
								    				return;
								    			}
										});
									}
									else if(rows.length > 0){
										console.log("I am in the right if");
										console.log(patron_emailid + " : "+ book_id);

										res.json({ status: 'issued' });
									}
									else{
										res.json({ status: 'failure' });
									}
										
								});
								
								
								
							}
						});
						
					}
					
					
				});
				
				
				
			}
		});
	}
	
}


exports.viewcart = function(req,res){
	var patron_emailid = req.body.patron_emailid;
	var book_array= [];

	db.query("SELECT * FROM Cart Where patron_emailid = ?", [patron_emailid], function(err, rows, fields){
		if(err){
			console.log("error in SELECT * FROM Cart in view cart");
			console.log(err);
			res.json({ status: 'failure' });
			return;
		}else{
			
			var number_of_books_in_cart = rows.length;
			
			var book_obj;
			for(var i=0; i <number_of_books_in_cart; i++){
			
				var book_id= rows[i].book_id;
				var book_name;
				var author;
				db.query("SELECT * FROM Book_Master Where book_id = ?", [book_id], function(err, rows, fields){
					
					if(err)
						{
						console.log(err);
						}
					
					book_name = rows[0].book_name;
					author = rows[0].author;			
					book_obj = {"book_name":book_name, "author": author};
					book_array.push(book_obj);
					if(book_array.length == number_of_books_in_cart ){
						res.json({result : book_array});
					}
					
				});
				
				
			}
			
			
		}
	});
}

exports.delete_from_cart = function(req,res){
	
	var book_name = req.body.book_name;
	var author = req.body.author;
	var patron_emailid = req.body.patron_emailid;
	var book_id;
	db.query("SELECT * FROM Book_Master Where book_name = ? AND author = ?", [book_name, author], function(err, rows, fields){
		if(err){
			console.log("error in SELECT * FROM Book_Master in delete from cart");
			console.log(err);
			res.json({ status: 'failure' });
			return;
		}
		else if(rows.length > 1){
			res.json({ status: 'failure' });
			return;
		}
		else if(rows.length == 1){
			book_id = rows[0].book_id;
			db.query("DELETE FROM Cart Where book_id = ? AND patron_emailid = ?", [book_id, patron_emailid ], function(err, rows, fields){
				if(err){
					console.log("error in DELETE * FROM CART in delete from cart");
					console.log(err);
					res.json({ status: 'failure' });
					return;
				}else{
					res.json({status: 'success'});
				}
			});
		}
		
		
	});
	
	
	
	
	
	
}







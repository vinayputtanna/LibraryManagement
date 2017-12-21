var mysql = require('mysql');
var session = require('express-session');
var passport = require('passport');
var expressValidator = require('express-validator');
var db = require('../db');
var dateTime = require('node-datetime');








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
	var book_image = req.body.book_image;
	var librarian_emailid = req.body.librarian_emailid;
	
	var number_of_books_in_waitlist;
	var book_id;
	var patron_arr = [];
	
	function check_if_book_exists_in_waitlist(book_id)
	{
	    return new Promise(function(resolve, reject) {
	        
		    	db.query("SELECT * FROM Waitlist Where  book_id = ?", [book_id], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            number_of_books_in_waitlist = rows.length;
		            resolve(rows.length);
	    		});
	    });
	}

	function Get_book_id(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            else{
		            		if(rows.length == 0){
		            			resolve("empty"); 
		            		}else{
		            			book_id = rows[0].book_id;
		    		            resolve(rows[0].book_id);
		            		}
		            }
		            
	    		});
	    });
	}

	function Get_book(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            resolve(rows.length);
	    		});
	    });
	}
	function Get_book_number_of_copies(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            resolve(rows[0].number_of_copies);
	    		});
	    });
	}

	function Get_book_current_status(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            resolve(rows[0].current_status);
	    		});
	    });
	}


	function update_book(new_copies,difference, book_name, author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
			    	db.query("UPDATE Book_Master SET number_of_copies = ?, current_status = ?  Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [new_copies,difference, book_name, author, publisher, year_of_publication], function(err,result){
			    		if(err){
			    			return reject(err);
			            }
			            resolve("updated");
			    	});
	    
	    });
	}


	function update_just_number_of_copies_in_book_master(new_copies, book_name, author, publisher, year_of_publication  )
	{
		return new Promise(function(resolve, reject) {
	    	db.query("UPDATE Book_Master SET number_of_copies = ? Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [new_copies, book_name, author, publisher, year_of_publication], function(err,result){
	    		if(err){
	    			return reject(err);
	            }
	            resolve("updated");
	    	});

	});
	}


	function get_all_patrons_from_waitlist(book_id)
	{
	    return new Promise(function(resolve, reject) {
			    	db.query("SELECT * FROM Waitlist Where book_id = ?", [book_id], function(err, rows, fields){
			    		if(err){
			    			return reject(err);
			        }
			    		for(var i = 0 ; i < rows.length; i ++){
			    			patron_arr.push(rows[i].patron_emailid);
			    		}
			    		
			        resolve(patron_arr);
			    	});
	    
	    });
	}

	function get_x_patrons_from_waitlist(book_id, x)
	{
	    return new Promise(function(resolve, reject) {
			    	db.query("SELECT * FROM Waitlist Where book_id = ? LIMIT ?;", [book_id, +x], function(err, rows, fields){
			    		if(err){
			    			return reject(err);
			        }
			    		for(var i = 0 ; i < rows.length; i ++){
			    			patron_arr.push(rows[i].patron_emailid);
			    		}
			    		
			        resolve(patron_arr);
			    	});
	    
	    });
	}



	function remove_patron_from_waitlist(book_id, patron_emailid)
	{
	    return new Promise(function(resolve, reject) {
		    	db.query("DELETE FROM Waitlist Where book_id = ? AND patron_emailid = ?", [book_id, patron_emailid], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            resolve("deleted");
	    		});
	    
	    });
	}


	function add_patron_to_reserved_table(book_id, patron_emailid)
	{
		return new Promise(function(resolve, reject) {
			var dt = dateTime.create();
		dt.format('m/d/Y H:M:S');
		var createdate = new Date(dt.now());	
			db.query("INSERT into Reserved (patron_emailid, book_id, reservation_date, reservation_expiry_date) VALUES (?,?,?,?)", [patron_emailid, book_id, createdate,createdate], function(err,result){
	            // Call reject on error states,
	            // call resolve with results
	            if (err) {
	                return reject(err);
	            }
	            resolve("inserted");
			});
	});
	}

	function check_if_book_exists_in_Book_master(book_name, author, publisher, year_of_publication)
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            number_of_books_in_waitlist = rows.length;
		            resolve(rows.length);
	    		});
	    });
	}


	function insert_in_Book_master(book_name, author, call_number, publisher, year_of_publication, location, number_of_copies, current_status, keywords, book_image, librarian_emailid)
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("insert into Book_Master (book_name, author, call_number, publisher, year_of_publication, location, number_of_copies, current_status, keywords, book_image, librarian_emailid) VALUES (?,?,?,?,?,?,?,?,?,?,?)", [book_name, author, call_number, publisher, year_of_publication, location, number_of_copies, current_status, keywords, book_image, librarian_emailid], function(err,result){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            
		            resolve("inserted");
	    		});
	    });
	}


	function update_Book_master(new_copies,new_current_status, book_name, author, publisher, year_of_publication)
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("UPDATE Book_Master SET number_of_copies = ?, current_status = ?  Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [new_copies,new_current_status, book_name, author, publisher, year_of_publication], function(err,result){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            
		            resolve("updated");
	    		});
	    });
	}
	
	

	
	Get_book_id(book_name, author, publisher, year_of_publication).then(function(book_id) {
		if( book_id == "empty"){
			insert_in_Book_master(book_name, author, call_number, publisher, year_of_publication, location, number_of_copies, current_status, keywords, book_image, librarian_emailid).then(function(status) {
				if(status == "inserted"){
					res.json({ status: 'added' });
					return;
				}
				else{
					res.json({ status: 'failure' });
					return;
				}
			});
		}
		else{
			check_if_book_exists_in_waitlist(book_id).then(function(result) {
				if(result == 0){
					//book doesn't exist in wait list
					check_if_book_exists_in_Book_master(book_name, author, publisher, year_of_publication).then(function(number) {
						if(number == 0){
							insert_in_Book_master(book_name, author, call_number, publisher, year_of_publication, location, number_of_copies, current_status, keywords, book_image, librarian_emailid).then(function(status) {
								if(status == "inserted"){
									res.json({ status: 'added' });
									return;
								}else{
									res.json({ status: 'failure' });
									return;
								}
							});						
						}
						else if(number > 0){
							Get_book_current_status(book_name,author, publisher, year_of_publication).then(function(status){
					    			var no_of_copies= rows[0].number_of_copies;
						    		var new_copies = +number_of_copies + +no_of_copies;
						    		var new_current_status = +status + +current_status;
						    		update_Book_master(new_copies,new_current_status, book_name, author, publisher, year_of_publication).then(function(status2) {
										if(status2 == "updated"){
											res.json({ status: 'updated' });
											return;
										}else{
											res.json({ status: 'failure' });
											return;
										}
									});					    		
					    		});
						}
						
					});
									
					
				}else{
					//book exists in wait list
					
					
					var difference = number_of_copies - number_of_books_in_waitlist;
					console.log(difference + " ????????");
					if(difference >=0){
						Get_book_number_of_copies(book_name,author, publisher, year_of_publication).then(function(copies){
							var new_copies = +copies + +number_of_copies;
							console.log(new_copies + " : "+ difference);
							update_book(new_copies, difference, book_name, author, publisher, year_of_publication).then(function(status){
								if(status == "updated"){
									console.log("updated book successfully");
									console.log(book_id);
									get_all_patrons_from_waitlist(book_id).then(function(patron_arr){
										console.log(patron_arr);
										
										async.each(patron_arr,
												  // 2nd param is the function that each item is passed to
												  function(item, callback){
												    // Call an asynchronous function, often a save() to DB
														remove_patron_from_waitlist(book_id,item).then(function(){
															add_patron_to_reserved_table(book_id, item).then(function(status){
																if(status == "inserted"){
																	res.json({ status: 'update_with_array', array: patron_arr, book_name: book_name });
																}
															});
														});																								
												  },
												  // 3rd param is the function to call when everything's done
												  function(err){
												    // All tasks are done now
													  res.json({ status: 'update_with_array', array: patron_arr });
													  return;  
												  }
												);
									});
								}
								
							});
						});					
					}				
					else if(difference <0){
						var x = number_of_copies;
						Get_book_id(book_name, author, publisher, year_of_publication).then(function(book_id) {
							Get_book_number_of_copies(book_name,author, publisher, year_of_publication ).then(function(copies){
								var new_copies = +number_of_copies + copies;
								update_just_number_of_copies_in_book_master(new_copies, book_name, author, publisher, year_of_publication).then(function(status){
									get_x_patrons_from_waitlist(book_id, x).then(function(patron_arr) {
										console.log(patron_arr);
										async.each(patron_arr,
												  // 2nd param is the function that each item is passed to
												  function(item, callback){
												    // Call an asynchronous function, often a save() to DB
														remove_patron_from_waitlist(book_id,item).then(function(){
															add_patron_to_reserved_table(book_id, item).then(function(status){
																if(status == "inserted"){
																	res.json({ status: 'update_with_array', array: patron_arr, book_name: book_name });
																}
															});
														});																								
												  },
												  // 3rd param is the function to call when everything's done
												  function(err){
												    // All tasks are done now
													  res.json({ status: 'update_with_array', array: patron_arr });
													  return;  
												  }
												);
										
									});
								});
							});						
							
						});
						
					}				
				}
			});
		}
		
	});
	
}






exports.updatebook = function(req, res){
	console.log("update book api");
	var book_name = req.body.book_name;
	var author = req.body.author_name;
	var call_number = req.body.call_number;
	var publisher = req.body.publisher;
	var year_of_publication = req.body.year_published;
	var location = req.body.location;
	var number_of_copies = req.body.number_of_copies;
	var keywords = req.body.keywords;
	var book_id = req.body.id;
	var current_status = req.body.status;

	var book_image = req.body.book_image;
	
	
	
		
		
	var number_of_books_in_waitlist;
	var patron_arr = [];
	
	function update_entire_book(book_image, book_name, author, publisher, year_of_publication, location, number_of_copies, keywords, book_id)
	{
	    return new Promise(function(resolve, reject) {
	    	db.query("UPDATE Book_Master SET book_image= ?, book_name = ? , author= ? , publisher= ? , year_of_publication= ?, location =?, number_of_copies =? , keywords=? WHERE book_id= ?", [book_image, book_name, author, publisher, year_of_publication, location, number_of_copies, current_status, keywords, book_id ], function(err,result){		    		if(err){
			    			return reject(err);
			            }
			            resolve("updated");
			    	});
	    
	    });
	}
	
	function check_if_book_exists_in_waitlist(book_id)
	{
	    return new Promise(function(resolve, reject) {
	        
		    	db.query("SELECT * FROM Waitlist Where  book_id = ?", [book_id], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            number_of_books_in_waitlist = rows.length;
		            resolve(rows.length);
	    		});
	    });
	}

	function Get_book_id(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            else{
		            		if(rows.length == 0){
		            			resolve("empty"); 
		            		}else{
		            			book_id = rows[0].book_id;
		    		            resolve(rows[0].book_id);
		            		}
		            }
		            
	    		});
	    });
	}

	function Get_book(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            resolve(rows.length);
	    		});
	    });
	}
	function Get_book_number_of_copies(book_name,author, publisher, year_of_publication )
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            resolve(rows[0].number_of_copies);
	    		});
	    });
	}

	




	function update_just_number_of_copies_in_book_master(new_copies, book_name, author, publisher, year_of_publication  )
	{
		return new Promise(function(resolve, reject) {
	    	db.query("UPDATE Book_Master SET number_of_copies = ? Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [new_copies, book_name, author, publisher, year_of_publication], function(err,result){
	    		if(err){
	    			return reject(err);
	            }
	            resolve("updated");
	    	});

	});
	}


	function get_all_patrons_from_waitlist(book_id)
	{
	    return new Promise(function(resolve, reject) {
			    	db.query("SELECT * FROM Waitlist Where book_id = ?", [book_id], function(err, rows, fields){
			    		if(err){
			    			return reject(err);
			        }
			    		for(var i = 0 ; i < rows.length; i ++){
			    			patron_arr.push(rows[i].patron_emailid);
			    		}
			    		
			        resolve(patron_arr);
			    	});
	    
	    });
	}

	function get_x_patrons_from_waitlist(book_id, x)
	{
	    return new Promise(function(resolve, reject) {
			    	db.query("SELECT * FROM Waitlist Where book_id = ? LIMIT ?;", [book_id, +x], function(err, rows, fields){
			    		if(err){
			    			return reject(err);
			        }
			    		for(var i = 0 ; i < rows.length; i ++){
			    			patron_arr.push(rows[i].patron_emailid);
			    		}
			    		
			        resolve(patron_arr);
			    	});
	    
	    });
	}



	function remove_patron_from_waitlist(book_id, patron_emailid)
	{
	    return new Promise(function(resolve, reject) {
		    	db.query("DELETE FROM Waitlist Where book_id = ? AND patron_emailid = ?", [book_id, patron_emailid], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            resolve("deleted");
	    		});
	    
	    });
	}


	function add_patron_to_reserved_table(book_id, patron_emailid)
	{
		return new Promise(function(resolve, reject) {
			var dt = dateTime.create();
		dt.format('m/d/Y H:M:S');
		var createdate = new Date(dt.now());	
			db.query("INSERT into Reserved (patron_emailid, book_id, reservation_date, reservation_expiry_date) VALUES (?,?,?,?)", [patron_emailid, book_id, createdate,createdate], function(err,result){
	            // Call reject on error states,
	            // call resolve with results
	            if (err) {
	                return reject(err);
	            }
	            resolve("inserted");
			});
	});
	}

	function check_if_book_exists_in_Book_master(book_name, author, publisher, year_of_publication)
	{
	    return new Promise(function(resolve, reject) {
	        
	    	db.query("SELECT * FROM Book_Master Where book_name = ? AND author= ? AND publisher= ? AND year_of_publication= ?", [book_name, author, publisher, year_of_publication], function(err, rows, fields){
		            // Call reject on error states,
		            // call resolve with results
		            if (err) {
		                return reject(err);
		            }
		            number_of_books_in_waitlist = rows.length;
		            resolve(rows.length);
	    		});
	    });
	}


		
		
	Get_book_id(book_name, author, publisher, year_of_publication).then(function(book_id) {
		console.log(book_id);
		check_if_book_exists_in_waitlist(book_id).then(function(result) {
			if(result == 0){
				//book doesn't exist in wait list
				update_entire_book(book_image, book_name, author, publisher, year_of_publication, location, number_of_copies, keywords, book_id).then(function(status){
					if(status == "updated"){
						res.json({ status: 'success_no_wait_list' });
						return;
					}
				});
				
								
				
			}else{
				//book exists in wait list
				
				
				var difference = number_of_copies - number_of_books_in_waitlist;
				console.log(difference + " ????????");
				if(difference >=0){
						console.log(difference);
						update_entire_book(book_image, book_name, author, publisher, year_of_publication, location, number_of_copies, keywords, book_id).then(function(status){
							if(status == "updated"){
								console.log("updated book successfully");
								console.log(book_id);
								get_all_patrons_from_waitlist(book_id).then(function(patron_arr){
									console.log(patron_arr);
									
									async.each(patron_arr,
											  // 2nd param is the function that each item is passed to
											  function(item, callback){
											    // Call an asynchronous function, often a save() to DB
													remove_patron_from_waitlist(book_id,item).then(function(){
														add_patron_to_reserved_table(book_id, item).then(function(status){
															if(status == "inserted"){
																res.json({ status: 'update_with_array', array: patron_arr, book_name: book_name });
															}
														});
													});																								
											  },
											  // 3rd param is the function to call when everything's done
											  function(err){
											    // All tasks are done now
												  res.json({ status: 'update_with_array', array: patron_arr });
												  return;  
											  }
											);
								});
							}
							
						});
								
				}				
				else if(difference <0){
					var x = number_of_copies;
					Get_book_id(book_name, author, publisher, year_of_publication).then(function(book_id) {
						update_entire_book(book_image, book_name, author, publisher, year_of_publication, location, number_of_copies, keywords, book_id).then(function(status){
								get_x_patrons_from_waitlist(book_id, x).then(function(patron_arr) {
									console.log(patron_arr);
									async.each(patron_arr,
											  // 2nd param is the function that each item is passed to
											  function(item, callback){
											    // Call an asynchronous function, often a save() to DB
													remove_patron_from_waitlist(book_id,item).then(function(){
														add_patron_to_reserved_table(book_id, item).then(function(status){
															if(status == "inserted"){
																res.json({ status: 'update_with_array', array: patron_arr, book_name: book_name });
															}
														});
													});																								
											  },
											  // 3rd param is the function to call when everything's done
											  function(err){
											    // All tasks are done now
												  res.json({ status: 'update_with_array', array: patron_arr });
												  return;  
											  }
											);
									
								});
							});
						
					});
					
				}				
			}
		});
	});
		
		
		
		
		
		
		
	
		
		
		
		
		
	
}


exports.deletebook = function(req, res){
	console.log("delete book Api");
	var id = req.body.id;
	db.query("SELECT * FROM Book_Master Where book_id = ?", [id], function(err, rows, fields){
		
		if(err){
			console.log("error while performing select query");
			res.json({ status: 'failure' });
		} 
		//book not found
	    	if (rows.length == 0){
	    		res.json({ status: 'booknotfound' });
	    	}else if(rows.length >1){
	    		console.log("There can not be more than 1 book associated to 1 book id");
	    	}
	    	else{
	    		console.log("helllo");
	    		var no_of_copies= rows[0].number_of_copies;
	    		var current_status = rows[0].current_status;
	    		if(no_of_copies == current_status){
	    			db.query("DELETE FROM Book_Master Where book_id = ?", [id], function(err, rows, fields){
	    				if(err){
	    					console.log("error while performing delete query");
	    					res.json({ status: 'failure' });
	    				} 
	    				else{
	    					res.json({ status: 'success' });
	    				}
	    			});
	    			
	    		}
	    		else{
	    			res.json({ status: 'borrowed' });
	    		}
	    	}
		
	});
	
}









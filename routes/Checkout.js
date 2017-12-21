var mysql = require('mysql');
var session = require('express-session');
var db = require('../db');
var dateTime = require('node-datetime');

exports.checkout = function(req,res){
	
	var patron_emailid = req.body.patron_emailid;
	
	var number_of_already_issued_books;
	var number_of_books_in_cart;

	db.query("SELECT * FROM Issued Where patron_emailid = ?", [patron_emailid], function(err, rows, fields){
		if(err){
			console.log("error in query in checkout issued");
			res.json({status: "failure"});
		}
		else{
			number_of_already_issued_books = rows.length;
			db.query("SELECT * FROM Cart Where patron_emailid = ?", [patron_emailid], function(err, rows, fields){
				if(err){
					console.log("error in query in checkout cart");
					res.json({status: "failure"});
				}
				else{
					number_of_books_in_cart = rows.length;
					var added = +number_of_books_in_cart + +number_of_already_issued_books;
					if(added >9){
						var n = +added - 9;
						res.json({status: "limit", exceeded : n})
					}
					else if(added <=9){
						for(var j =0; j < number_of_books_in_cart; j++){
							var book_id = rows[j].book_id;
							var dt = dateTime.create();
							dt.format('m/d/Y H:M:S');
							var createdate = new Date(dt.now());							
							db.query("INSERT into Issued (patron_emailid, book_id, due_date, create_date) VALUES (?,?,?,?)", [patron_emailid, book_id, createdate,createdate], function(err,result){
								if(err){
									console.log(err);
									res.json({status: "failure"});
								}
								else{
									res.json({status:"success"});
									
								}
							});
						}
						for(var k =0; k<number_of_books_in_cart; k++){
							var book_id = rows[k].book_id;
							console.log("deleted book with book_id = "+ book_id );
							db.query("DELETE FROM Cart Where book_id = ? AND patron_emailid = ?", [book_id, patron_emailid], function(err, rows, fields){
								if(err){
									console.log("couldnt delete book from cart on checkout");
									res.json("failure");											
								}								
							});
						}
						var current_status_arr = [];
						for(var l =0; l<number_of_books_in_cart; l++){
							var book_id = rows[l].book_id;
							console.log("book _ id  : "+book_id);
							db.query("SELECT * from Book_Master WHERE book_id = ?", [book_id], function(err, rows, fields){
								if(err){
									console.log("Error in query");																
								}
								else{
									var current_status_temp = rows[0].current_status;	
									var current_status = +current_status_temp -1;
									if(current_status <0){
										current_status = 0;
									}
									current_status_arr.push(current_status);									
								}
							});	
						}
						setTimeout(function() {
						    console.log(current_status_arr);
						    for(var l =0; l<number_of_books_in_cart; l++){
								var book_id = rows[l].book_id;
								var current_status = current_status_arr[l];
								console.log("current status : "+ current_status);
								db.query("UPDATE Book_Master SET current_status = ? WHERE book_id = ?",[current_status, book_id], function(err, rows, fields){
									if(err){
										console.log("error in query");
									}
									else{
										console.log("updated");
									}
									
								});
							}
						}, 3000);
						
						
						
						
					}
						
				}
				
				
			});
		}
	});
}

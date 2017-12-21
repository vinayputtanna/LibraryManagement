var db = require('../db');
async = require("async");
var dateTime = require('node-datetime');



exports.getBorrowedBooks = function(req, res){
    console.log("getBorrowedBooks api");
    var emailid = req.body.emailid;
    console.log(emailid);

    var sql = "SELECT Book_Master.book_id, book_name, author, call_number, publisher, year_of_publication, location, due_date "+
                "FROM Book_Master JOIN Issued on Book_Master.book_id=Issued.book_id WHERE Issued.patron_emailid = ?";

    db.query(sql, [emailid], function(err, rows, fields){
        if(err){
            res.send({ result: 'error' });
        }
        if (rows.length > 0){
        		console.log(rows);
            res.send({ result: rows });
        }else{
            res.send({ result: '0' });
        }
    });
};


function delete_from_issue_table(book_id, emailid)
{
    return new Promise(function(resolve, reject) {
        
	    	var sql = "DELETE FROM Issued WHERE patron_emailid = ? AND book_id = ?";
	    	db.query(sql, [emailid, book_id], function(err, result){
	            // Call reject on error states,
	            // call resolve with results
	            if (err) {
	                return reject(err);
	            }
	            resolve(result);
	        });
    });
}

function number_of_people_waiting_for_book(book_id)
{
    return new Promise(function(resolve, reject) {
        
    		db.query("SELECT * FROM Waitlist Where  book_id = ?", [book_id], function(err, rows, fields){
	            // Call reject on error states,
	            // call resolve with results
	            if (err) {
	                return reject(err);
	            }
	            resolve(rows.length);
    		});
    });
}




function get_current_status(book_id)
{
    return new Promise(function(resolve, reject) {
        
    		db.query("SELECT * from Book_Master WHERE book_id = ?", [book_id], function(err, rows, fields){
	            // Call reject on error states,
	            // call resolve with results
	            if (err) {
	                return reject(err);
	            }
	            var current_status_temp = rows[0].current_status;	
	            current_status_temp = +current_status_temp +1;
	            resolve(current_status_temp);
    		});
    });
}


function Update_current_status_in_book_master(book_id, current_status)
{
    return new Promise(function(resolve, reject) {
        
    		db.query("UPDATE Book_Master SET current_status = ? WHERE book_id = ?",[current_status, book_id], function(err, rows, fields){
	            // Call reject on error states,
	            // call resolve with results
	            if (err) {
	                return reject(err);
	            }
	            
	            resolve("success");
    		});
    });
}


function first_person_in_waitlist(book_id)
{
    return new Promise(function(resolve, reject) {
        
    		db.query("SELECT * FROM Waitlist Where  book_id = ?", [book_id], function(err, rows, fields){
	            // Call reject on error states,
	            // call resolve with results
	            if (err) {
	                return reject(err);
	            }
	            resolve(rows[0].patron_emailid);
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
	            resolve(patron_emailid);
    		});
    });
}

function insert_patron_into_reserved_table(book_id, patron_emailid)
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

exports.returnBooks = function(req, res){
    console.log("returnBooks api");
    
    var emailid = req.body.emailid;
    var bookIds= req.body.bookIds;
    var current_status;
    var book_array = bookIds.split(',');
    for(var i=0; i<book_array.length; i++){
	    	
	    	var book_id= book_array[i];
	    	delete_from_issue_table(book_id, emailid);
		            		
    		}
    
		async.each(book_array,
				  // 2nd param is the function that each item is passed to
				  function(item, callback){
				    // Call an asynchronous function, often a save() to DB
						number_of_people_waiting_for_book(item).then(function(number){
							if(number>0){
								console.log("Book : "+ item+" is there in wait list");
								first_person_in_waitlist(item).then(function(patron_emailid) {
									remove_patron_from_waitlist(item, patron_emailid).then(function(patron_emailid){
										insert_patron_into_reserved_table(item, patron_emailid)
									});
								});
								callback();
							}
							else if (number ==0){
								console.log("Book : "+ item+" is not there in wait list");
								get_current_status(item).then(function(current_status){
									Update_current_status_in_book_master(item,current_status).then(function(status) {
										if(status == "success"){
											res.json({status:"success"});
										}
									});
								});
								
								callback();
							}
						});
				  },
				  // 3rd param is the function to call when everything's done
				  function(err){
				    // All tasks are done now
				    
				  }
				);
  
};





exports.renewBook = function(req, res){
    console.log("renewBook api");
    var emailId = req.body.emailId;
    var bookId = req.body.bookId;
    var dueDate;
    var renewedCount=0;

    var waitlistSql = "SELECT patron_emailid, book_id FROM Waitlist WHERE patron_emailid=? AND book_id=?";

    db.query(waitlistSql, [emailId, bookId], function(err, rows, fields){
        if(err){
            res.send({ "status": "error" });
        }
        else if (rows.length > 0){
            res.send({ "status": "waitlisted" });
        }else{
            var issuedSql="SELECT due_date, renewed_count, DATE(due_date)>=DATE(Now()) AS result FROM Issued WHERE patron_emailid=? AND book_id=?";
            db.query(issuedSql, [emailId, bookId], function(err, rows, fields) {
                if (err) {
                    res.send({"status": "error"});
                }
                else if(rows.length>0){
                    dueDate=rows[0].due_date;
                    renewedCount=rows[0].renewed_count;
                    if(rows[0].result===0){
                        res.send({"status":"overdue"});
                    }
                    else if(rows[0].renewed_count>=2) {
                        res.send({"status": "non-renewable"});
                    }else{
                        var updateIssuedSql="UPDATE Issued SET due_date=DATE_ADD(?, INTERVAL 30 DAY), renewed_count=? WHERE patron_emailid=? AND book_id=?";
                        db.query(updateIssuedSql, [dueDate, renewedCount+1, emailId, bookId], function(err, rows, fields) {
                            if (err) {
                                res.send({"status": "error"});
                            }
                            else if(rows.affectedRows>0) {
                                res.send({"status":"renewed"});
                            }
                        });
                    }
                }
            });
        }
    });
};





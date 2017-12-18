var db = require('../db');

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

exports.returnBooks = function(req, res){
    console.log("returnBooks api");
    var emailid = req.body.emailid;
    var bookIds= req.body.bookIds;
    var book_array = bookIds.split(',');
    console.log(emailid + " : "+ book_array);
    for(var i=0; i<book_array.length; i++){
	    	var sql = "DELETE FROM Issued WHERE patron_emailid = ? AND book_id = ?";
	    	var book_id= book_array[i];
		    	db.query(sql, [emailid, book_id], function(err, result){
			        if(err){
			        	console.log(err);
			            res.send({ status: 'failure' });
			        }			        
			    });		        		
    		}
    res.send({ status: "success"  });
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
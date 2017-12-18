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
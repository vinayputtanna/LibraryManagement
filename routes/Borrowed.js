var db = require('../db');

exports.getBorrowedBooks = function(req, res){
    console.log("getBorrowedBooks api");
    var emailid = req.body.emailid;

    var sql = "SELECT book_name, author, call_number, publisher, year_of_publication, location, due_date "+
                "FROM Book_Master JOIN Issued on Book_Master.book_id=Issued.book_id WHERE Issued.patron_id = "+
                "(Select patron_id from Patron where emailid=?)";

    db.query(sql, [emailid], function(err, rows, fields){
        if(err){
            res.send({ result: 'error' });
        }
        if (rows.length > 0){
            res.send({ result: rows });
        }else{
            res.send({ result: '0' });
        }
    });
};
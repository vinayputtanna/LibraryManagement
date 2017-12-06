var db = require('../db');

exports.searchByBookName = function(req, res){
    console.log("searchByBookName api");
    var keyword = req.body.keyword;

    var sql = "SELECT * FROM Book_Master WHERE book_name like " + db.escape(keyword+'%');

    db.query(sql, function(err, rows, fields){
        if(err){
            res.send({ status: 'failure' });
        }
        if (rows.length > 0){
            res.send(rows);
        }else{
            res.send({ status: 'failure' });
        }
    });
};

exports.searchByAuthor = function(req, res){
    console.log("searchByAuthor api");
    var keyword = req.body.keyword;

    var sql = "SELECT * FROM Book_Master WHERE keywords like " + db.escape(keyword+'%');

    db.query(sql, function(err, rows, fields){
        if(err){
            res.send({ status: 'failure' });
        }
        if (rows.length > 0){
            res.send(rows);
        }else{
            res.send({ status: 'failure' });
        }
    });
};

exports.searchByKeyword = function(req, res){
    console.log("searchByKeyword api");
    var keyword = req.body.keyword;

    var sql = "SELECT * FROM Book_Master WHERE keywords like " + db.escape(keyword+'%');

    db.query(sql, function(err, rows, fields){
        if(err){
            res.send({ status: 'failure' });
        }
        if (rows.length > 0){
            res.send(rows);
        }else{
            res.send({ status: 'failure' });
        }
    });
};
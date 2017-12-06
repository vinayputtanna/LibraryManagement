var db = require('../db');

exports.searchByBookName = function(req, res){
    console.log("searchByBookName api");
    var book_name = req.body.book_name;

    var sql = "SELECT * FROM Book_Master WHERE book_name like " + db.escape('%' + book_name +'%');

    db.query(sql, function(err, rows, fields){
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

exports.searchByAuthor = function(req, res){
    console.log("searchByAuthor api");
    var author = req.body.author;

    var sql = "SELECT * FROM Book_Master WHERE author like " + db.escape('%' + author + '%');

    db.query(sql, function(err, rows, fields){
        if(err){
            res.send({ result: 'error' });
        }
        if (rows.length > 0){
            res.send({ result : rows });
        }else{
            res.send({ result: '0' });
        }
    });
};

exports.searchByKeyword = function(req, res){
    console.log("searchByKeyword api");
    var keyword = req.body.keyword;

    var sql = "SELECT * FROM Book_Master WHERE keywords like " + db.escape(keyword + '%') + " or keywords like " +
        db.escape('%,' + keyword + '%') + " or keywords like " + db.escape('%, ' + keyword + '%');

    db.query(sql, function(err, rows, fields){
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
var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : '35.197.104.4',
    user     : 'root',
    password : 'sqladmin',
    database : 'librarydb'
});

connection.connect(function(err) {
    if (err) throw err;
});

module.exports = connection;
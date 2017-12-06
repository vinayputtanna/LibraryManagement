
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , user = require('./routes/user')
  , http = require('http')
  , path = require('path');
var authentication = require('./routes/Authentication');
var book = require('./routes/Book');
var Search = require('./routes/Search');

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', routes.index);
app.get('/users', user.list);
app.post('/signup',authentication.signup);
app.post('/login', authentication.login);
app.post('/verified',authentication.verification);
app.post('/checkifverified', authentication.checkifverified);
app.post('/addbook',book.addbook);

app.post('/searchbybookname', Search.searchByBookName);
app.post('/searchbyauthor', Search.searchByAuthor);
app.post('/searchbykeyword', Search.searchByKeyword);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});

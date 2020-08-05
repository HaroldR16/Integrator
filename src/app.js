//Imports

const   express         = require('express'),
        routes          = require('./routes/index'),
        path            = require('path'),
        session         = require('express-session'),
        assert          = require('assert'),
        { MongoClient } = require('mongodb')
//const readline = require('@serialport/parser-readline');

//Database config

const   DB_URI          =   "mongodb+srv://dbUser:dbSuperPassword@integrativesmartcar.u5vqm.mongodb.net/SmartCarDB?retryWrites=true&w=majority",
        client          =   new MongoClient(DB_URI, { useNewUrlParser: true }),
        DB_NAME         =   'SmartCarDB',
        DB_UCOLLECTION  =   'Users';

client.connect(function(err) {
    assert.equal(null, err);
    console.log("Connected successfully to server");
          
    const db = client.db(DB_NAME);
          
    client.close();
});

module.exports = client;

//Config

const app = express();
app.set('port', process.env.PORT || 3000);
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, '/views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());

app.use(session({
    secret: '.ZR3XQ\f*eT$@LAPLwddfr#{dgd^6-g2ExKykB5X.<%',
    resave: true,
    saveUninitialized: true
}))

app.use(function (req, res, next) {
    res.locals.user = req.session.user;
    next()
  })

//routes
app.use('/', routes);

app.listen(app.get('port'), ()=>{
    console.log(`Connected to port: ${app.get('port')}`)
});

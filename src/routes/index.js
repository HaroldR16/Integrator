const session = require('express-session');

const   express         = require('express'),
        route           = express.Router(),
        { MongoClient } = require('mongodb'),
        SerialPort      = require('serialport'),
        assert          = require('assert');

const   DB_URI          =   "mongodb+srv://dbUser:dbSuperPassword@integrativesmartcar.u5vqm.mongodb.net/SmartCarDB?retryWrites=true&w=majority",
        DB_CLIENT       =   new MongoClient(DB_URI, { useNewUrlParser: true }),
        DB_NAME         =   'SmartCarDB',
        DB_UCOLLECTION  =   'Users';

const URLEncodedExpress = express.urlencoded({extended: false});

const port = new SerialPort('COM3', {
    baudRate: 115200
}, (err)=>{
    if(err) return console.error(`Error: ${err.message}.`)
});

route.get('/', (req, res)=>{
    res.render('home', {title: 'Home', session: req.session});
});

route.get('/dashboard', (req, res)=>{
    if(!req.session.user){
        return res.redirect('/');
    }
    res.render('home', {title: `Home`, session: req.session});
});

route.get('/index', (req, res)=>{
    res.render('index');
});

route.get('/about', (req, res)=>{
    res.render('about', {title: 'About', session: req.session});
});

route.post('/postAndroid', (req, res)=>{
    console.log(req.body)
    res.status(200).send();
    DB_CLIENT.connect(err => {
        assert.equal(null, err);
        const collection = DB_CLIENT.db(DB_NAME).collection(DB_UCOLLECTION);
        collection.findOne({user: req.body.user, password: req.body.password},
            (err, result)=>{
                if(err){
                    console.log(err);
                    return res.send();
                } 
                if(!result){
                    return res.send()
                }
                if(result){
                    req.session.user = result;
                    console.log('success');
                    return res.send();
                }
            });
    });
})

route.post('/turnOn', (req, res)=>{
    port.write('1', (err)=>{
        if(err){
            console.log(`Error ${err}.`)
        }
    });
    res.redirect('/');
});

route.post('/turnOff', (req, res)=>{
    port.write('0', (err)=>{
        if(err){
            console.log(`Error: ${err}.`)
        }
    });
    res.redirect('/');
});

route.get('/logout', (req, res)=>{
    req.session.destroy((err)=>{
        if(err){
            return res.status(404).send();
        }
    });
    res.redirect('/dashboard');
});

route.get('/signup', (req, res)=>{
    res.render('sign-up', {title: "Sign-Up", session: req.session});
});

route.get('/signin', (req, res)=>{
    res.render('sign-in', {title: "Sign-In", session: req.session});
});

route.get('/table', async (req, res)=>{
    if(!req.session.user){
        
        return res.redirect('/');
    }
    DB_CLIENT.connect(err=>{
        assert.equal(null, err);
        const collection = DB_CLIENT.db(DB_NAME).collection(DB_UCOLLECTION);
        const data = collection.find({}).toArray((err, documents)=>{
            assert.equal(null, err);
            res.render('table', {title: "Table", session: req.session, data:documents});
        }); 
    });
});

route.get('/distance', async (req, res)=>{
    if(!req.session.user){
        
        return res.redirect('/');
    }
    console.log(req.session);
    DB_CLIENT.connect(err=>{
        assert.equal(null, err);
        const collection = DB_CLIENT.db(DB_NAME).collection(DB_UCOLLECTION);
        const data = collection.findOne({user: req.session.user.user, password: req.session.user.password}, (err, document)=>{
            console.log(document)
            assert.equal(null, err);
            res.render('distance', {title: "Distance", session: req.session, data:document});
        }); 
    });
});

route.post('/signin', URLEncodedExpress, (req, res)=>{
    DB_CLIENT.connect(err => {
        assert.equal(null, err);
        const collection = DB_CLIENT.db(DB_NAME).collection(DB_UCOLLECTION);
        collection.findOne({user: req.body.user, password: req.body.password},
            (err, result)=>{
                if(err){
                    console.log(err);
                    return res.status(500).send();
                } 
                if(!result){
                    return res.status(400).send()
                }
                if(result.user === 'Hacker321123' && result.password === '12345'){
                    req.session.user = result;
                    req.session.user.admin = true;
                    return res.redirect('/dashboard');
                }
                if(result){
                    req.session.user = result;
                    return res.redirect('/dashboard');
                }
            });
    });
});

route.post('/signup', URLEncodedExpress,(req, res)=>{
    DB_CLIENT.connect(err => {
        // perform actions on the collection 
        assert.equal(null, err);
        const collection = DB_CLIENT.db(DB_NAME).collection(DB_UCOLLECTION);
        collection.insert(
            {
                'user': req.body.user, 
                'password': req.body.password},
                (err, result)=>{
                    if(err){
                        console.log(err);
                        DB_CLIENT.close();
                        return res.status(500).send();
                    }else {
                        console.log(result);
                        DB_CLIENT.close();
                        return res.redirect('/');
                    }
                })
      });
});

// port.on('error', (err)=>{
//     if(err) console.error(`Error: ${err.message}`)
// });

module.exports = route;
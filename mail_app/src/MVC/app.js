const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mongoose = require('mongoose');
const path = require('path');
const fs = require('fs');


var app = express();


// Define path for profile image uploads. If does not exist, create one
const uploadsDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}


// require mongoose port config, and connection
require('custom-env').env(process.env.NODE_ENV, './config');
mongoose.connect(process.env.CONNECTION_STRING);


// connect express app
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));
app.use(cors());
app.use(bodyParser.urlencoded({extended : true}));
app.use(express.json());
const labelRoutes = require('./routes/labels')
const userRoutes = require('./routes/users')
const mailRoutes = require('./routes/mails')
const tokenRoutes = require('./routes/tokens')
const blacklistRoutes = require('./routes/blacklist')

app.use('/api/labels', labelRoutes)
app.use('/api/labels/mail', labelRoutes)
app.use('/api/users', userRoutes)
app.use('/api/mails', mailRoutes)
app.use('/api/tokens', tokenRoutes)
app.use('/api/blacklist', blacklistRoutes)

console.log("PORT:", process.env.PORT);
console.log("DB:", process.env.CONNECTION_STRING);


app.listen(8080)
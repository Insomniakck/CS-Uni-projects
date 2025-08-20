const { createLabel } = require('./labels');
const User = require('../models/users');

// create new Gmail user
const createUser = async ( firstName, lastName, birthday, username, password, profileImage ) => {
    // if username is taken return null
    const existingUser = await User.findOne({ username });
    if (existingUser) {
        return null;
    }

    const [day, month, year] = birthday.split("/");
    const formattedDate = new Date(`${year}-${month}-${day}`);

    // Create default labels
    const defaultNames = ['inbox', 'sent', 'starred', 'draft', 'spam', 'trash', 'read'];
    const labelIds = await Promise.all(defaultNames.map(n => createLabel(username, n)));
    const defaultLabels = Object.fromEntries(
        defaultNames.map((name, i) => [name, labelIds[i]])
    );

    
    const newUser = new User({
    firstName: firstName,
    lastName: lastName,
    birthday: formattedDate,
    username: username,
    password: password,
    defaultLabels: defaultLabels,
    profileImage: profileImage
    });
    


    await newUser.save();
    return newUser;
}

// Get User default labels list
const getUserDefaultLabels = async ( username ) => {
    const user = await User.findOne({ username }, 'defaultLabels').lean();
    return user ? user.defaultLabels : null;
}

// Get User specific default label by name
const getUserDefaultLabelsByName = async ( username, labelName ) => {
    const user = await User.findOne({ username }).lean();
    if (!user || !user.defaultLabels || !user.defaultLabels[labelName]) {
        return null;
    }
    return user.defaultLabels[labelName];
}

// get User non sensitive information with id
const getUser = async ( id ) => {
    const user = await User.findOne({ _id: id });
    if ( !user ){ 
        return null;
    }
    return {
        username: user.username,
        firstName: user.firstName,
        lastName: user.lastName,
        profileImage: user.profileImage
    }
}

// get User non sensitive information with username
const getUserInfo = async ( username ) => {
    const user = await User.findOne({username: username });
    if ( !user ){
        return null;
    }
    return {
        userName: user.username,
        firstName: user.firstName,
        lastName: user.lastName,
        profileImage: user.profileImage,
    }
}



const isUserRegistered = async ( id ) => {
    const user = await User.findOne({ _id: id });
    if (!user){
        return false;
    }
    return true;
}

const isUsernameExist = async (username) => {
    const user = await User.findOne({ username: username});
    if (!user){
        return false;
    }
    return true;
}


// get user ID with username and password
const getID = async ( username, password ) => {
    const user = await User.findOne({username: username});
    // if username doesn't exist return -1
    if (! user){
        return -1
    }
    // if username and password are correct return the user id
    if(user.password === password){
        return user.id
    }
    // if username exists but the password is incorrect return 0
    return 0
}

// get User non sensitive information with id
const getUsernameByID = async ( id ) => {
    const user = await User.findOne({ _id: id });
    if (!user){
        return null
    }
    return user.username  
}

module.exports = {
    createUser,
    getUser,
    getID,
    isUserRegistered,
    getUsernameByID,
    isUsernameExist,
    getUserDefaultLabels,
    getUserDefaultLabelsByName,
    getUserInfo
}
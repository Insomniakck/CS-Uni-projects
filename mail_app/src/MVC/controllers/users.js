const User = require('../services/users')


exports.createUser = async (req, res) => {

    const { firstName, lastName, birthday, username, password } = req.body


    if (!firstName || !lastName || !birthday || !username || !password) {
        return res.status(400).json({ error: 'One or more fields are missing.' })
    }


    let { profileImage}= req.body

    if (profileImage===undefined||profileImage===null){
         profileImage = req.file ? req.file.filename : null;
        
    }

    // Username must be unique
    if (await User.isUsernameExist(username)) {
        return res.status(400).json({ error: 'Email address already taken.' })
    }

    // Username is the email address. must match email format.
    const usernameRegex = /^[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)+$/;
    if (!usernameRegex.test(username)) {
        return res.status(400).json({ error: 'Invalid email address' })
    }

    // Birthday must be in format DD/MM/YYYY
    const birthdayRegex = /^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[0-2])\/\d{4}$/;

    if (!birthdayRegex.test(birthday)) {
        return res.status(400).json({ error: 'Birthday must be in format DD/MM/YYYY' })
    }

    // Password must be at least 8 characters
    if (password.length < 8) {
        return res.status(400).json({ error: 'Password must be al least 8 characters.' })
    }

    // Password must contain both letters and numbers
    const hasLetter = /[a-zA-Z]/.test(password);
    const hasNumber = /\d/.test(password);
    if (!hasLetter || !hasNumber) {
        return res.status(400).json({ error: 'Password must contain both letters and numbers.' });
    }

    const newUser = await User.createUser(firstName, lastName, birthday, username, password, profileImage);
    return res.status(201).location(`/api/users/${newUser._id}`).end();
}

exports.getUserByID = async (req, res) => {
    const user = await User.getUser(req.params.id)
    if (!user) {
        return res.status(404).json({ error: 'User not found' })
    }
    return res.json(user)
}

exports.getUserByPassword = async (req, res) => {
    const { username, password } = req.body
    if (!username || !password) {
        return res.status(400).json({ error: 'Username or Password missing.' })
    }
    const id = await User.getID(username, password)
    // if username doesn't exist id == -1
    if (id === -1) {
        return res.status(404).json({ error: 'Incorrect Username.' })
        // if password is incorrect id == 0
    } else if (id === 0) {
        return res.status(404).json({ error: 'Incorrect Password.' })
    }
    return res.json(id)
}

exports.getUserInfo = async (req, res) => {
    const user = await User.getUserInfo(req.params.username)
    if (!user) {
        return res.status(404).json({ error: 'User not found' })
    }
    return res.json(user)
}

exports.getUserDefaultLabels = async (req, res) => {
    const userID = req.header('X-User-Id');
    if (!  await User.isUserRegistered(userID)) {
        return res.status(404).json({ error: 'User not registered' });
    }

    const user = await User.getUser(userID);
    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    const defaultLabels = await User.getUserDefaultLabels(username);
    if (defaultLabels === 0) {
        return res.status(404).json({ error: 'Incorrect Label Name.' })
    }
    return res.json(defaultLabels)
}

exports.getUserDefaultLabelsByName = async (req, res) => {
    const userID = req.header('X-User-Id');
    if (! await User.isUserRegistered(userID)) {
        return res.status(404).json({ error: 'User not registered' });
    }

    const user = await User.getUser(userID);
    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    const { labelName } = req.body;

    const labelID = await User.getUserDefaultLabelsByName(username, labelName);
    if (labelID === 0) {
        return res.status(404).json({ error: 'Incorrect Label Name.' })
    }
    return res.json(labelID)
}




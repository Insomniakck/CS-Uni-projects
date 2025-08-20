const Label = require('../services/labels')
const { isUserRegistered , getUser} = require('../services/users')
const { getMail } = require('../services/mails')

exports.getAllLabels = async (req, res) => {
    const userID = req.header('X-User-Id')
    if (! await isUserRegistered(userID)) {
        res.status(404).json({error: 'User not registered'})
    }
    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;
    return res.json(await Label.getAllLabels( username))
}

exports.getLabel = async (req, res) => {
    const userID = req.header('X-User-Id')
    if (! await isUserRegistered(userID)) {
        return res.status(404).json({error: 'User not registered'})
    }
    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    //getLabel gets the userID and labelID
    const label = await Label.getLabel( username, req.params.labelID)

    if (!label)
        return res.status(404).json({ error: 'Label not found' })
    return res.json(label)
}

exports.createLabel = async (req, res) => {
    const userID =req.header('X-User-Id')
    if (! await isUserRegistered(userID)) {
        return res.status(404).json({error: 'User not registered'})
    }

    const { labelName } = req.body
    if (!labelName)
        return res.status(400).json({ error: 'Name is required' })

    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    const newLabelID = await Label.createLabel(username, labelName)
    return res.status(201).location(`/api/labels/${newLabelID}`).send(newLabelID)
}

exports.editLabel = async (req, res) => {
    const userID = req.header('X-User-Id')
    if (! await isUserRegistered(userID)) {
        return res.status(404).json({error: 'User not registered'})
    }

    const labelID = req.params.labelID;
    const { labelName } = req.body
    if (!labelName) {
        return res.status(400).json({ error: 'Name is required' })
    }

    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    const updated = await Label.editLabel(labelID, labelName, username)
    if (!updated) {
        return res.status(404).json({ error: 'Label not found' })
    }
    return res.status(204).end()
}

exports.deleteLabel = async (req, res) => {
    const userID = req.header('X-User-Id')
    if (! await isUserRegistered(userID)) {
        return res.status(404).json({error: 'User not registered'})
    }

    const labelID = req.params.labelID;
    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    const deleted = await Label.deleteLabel(labelID, username)

    if (!deleted) {
        return res.status(404).json({ error: 'Label not found' })
    }
    return res.status(204).end() 
}

exports.addMailToLabel = async (req, res) => {
    const userID = req.header('X-User-Id');
    if (! await isUserRegistered(userID)) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const labelID = req.body.labelID
    const mailID = req.body.mailID
    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    const addedMail = await Label.addMailToLabel(username, labelID, mailID);

    if (!addedMail) {
        return res.status(404).json({ error: 'Label not found' });
    }

    return res.status(200).json(addedMail);
};

exports.removeMailFromLabel = async (req, res) => {
    const userID = req.header('X-User-Id');
    if (! await isUserRegistered(userID)) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const labelID = req.body.labelID
    const mailID = req.body.mailID
    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;

    const removed = await Label.removeMailFromLabel(username, labelID, mailID);

    if (!removed) {
        return res.status(404).json({ error: 'Label not found' });
    }

    return res.status(200).json(removed);
};

exports.getMailsForLabel = async (req, res) => {
    const userID = req.header('X-User-Id');
    if (! await isUserRegistered(userID)) {
        return res.status(404).json({ error: 'User not registered' });
    }

    const user = await getUser(userID);
    if(!user){
        return res.status(404).json({ error: 'User not found' });
    }
    const username = user.username;
    const labelID = req.params.labelID;

    const mailIDs = await Label.getMailsForLabel(username, labelID);
    const mails = await Promise.all(mailIDs.map(id => getMail(username, id)));

    return res.json({ mails });
};


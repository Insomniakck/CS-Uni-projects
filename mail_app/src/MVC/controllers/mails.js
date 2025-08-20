const Mail = require('../services/mails')
const { isUsernameExist } = require('../services/users')
const { getUsernameByID } = require('../services/users')

exports.getAllUserMails = async (req, res) => {
    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    return res.json( await Mail.getAllUserMails(username));
}

exports.getMail = async (req, res) => {
    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const mail = await Mail.getMail(username, req.params.mailID);
    if (!mail)
        return res.status(404).json({ error: 'Mail not found' });
    return res.json(mail);
}

exports.createMail = async (req, res) => {
    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const { receiver, subject, content } = req.body;

    // Validate required field: content
    if (!content || content.trim() === "")
        return res.status(400).json({ error: 'Content is required' });

    //reciepient isn't registered
    if (! await isUsernameExist(receiver))
        return res.status(400).json({ error: "Receipient's email address incorrect." });

    const newMail = await Mail.createMail(username, receiver, subject, content);
    if (!newMail) {
        return res.status(400).json({ error: 'Failed to update mail.' })
    }
    return res.status(201).location(`/api/mails/${newMail.id}`).json(newMail);

}

exports.editMail = async (req, res) => {
    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const mailID = req.params.mailID;
    const { receiver, subject, content } = req.body;
    if (!content || content.trim() === "") {
        return res.status(400).json({ error: 'Content is required' });
    }
    const updated = await Mail.editMail(mailID, username, receiver, subject, content);
    if (updated == -1) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    if (!updated) {
        return res.status(400).json({ error: 'Failed to update mail.' });
    }
    return res.status(204).end();
}

exports.deleteMail = async (req, res) => {
    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const mailID = req.params.mailID;
    const deleted = await Mail.deleteMail(mailID, username);
    if (!deleted) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    return res.status(204).end();
}

exports.searchString = async (req, res) => {
    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const query = req.params.query || '';
    const results = await Mail.searchString(query, username);
    return res.status(200).json(results);
}

exports.createDraft = async (req, res) => {
    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const { receiver = '', subject = '', content = '' } = req.body;

    const newMail = await Mail.createDraft(username, receiver, subject, content) ;
    if (!newMail){

        return res.status(400).json({ error: 'Mail creation failed.' });
    }
    return res.status(200).json(newMail);

}

exports.editDraft = async (req, res) => {

    const userID = req.header('X-User-Id');
    const username = await getUsernameByID(userID);
    if (!username) {
        return res.status(404).json({ error: 'User not registered' });
    }
    const mailID = req.params.mailID;
    const { receiver, subject, content } = req.body;

    const updatedDraft = await Mail.editDraft(mailID, username, receiver, subject, content);
    if (updatedDraft == -1) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    if (!updatedDraft) {
        return res.status(400).json({ error: 'Failed to update mail.' });
    }
    return res.status(200).json(updatedDraft);

}
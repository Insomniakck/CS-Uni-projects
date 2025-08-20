const client = require('../blacklistClient.js');
const { sendToBlacklistServer } = require('../blacklistClient.js')
const { getUserDefaultLabelsByName } = require('./users');
const { addMailToLabel, removeMailFromLabel, getMailsForLabel } = require('./labels.js')
const Mail = require('../models/mails.js');
const Label = require('../models/labels');


// Return 50 most recent mails belongs to userID
const  getAllUserMails = async (username) => {
    return await Mail.find({
      $or: [{ sender: username }, { receiver: username }]
    })
    .sort({ date: -1 })
    .lean();
}

// Return mail by id
const getMail = async (username, mailID) => {
     return await Mail.findOne({
    _id: mailID,
    $or: [{ sender: username }, { receiver: username }]
  }).lean();
}
   

// Create new mail
const createMail = async (sender, receiver, subject, content) => {

    // Split subject and content into a words array by chars like space, tab, enter (/\s+/)
    const contentWords = content.trim().split(/\s+/)
    const subjectWords = subject.trim().split(/\s+/)
    const allWords = [...contentWords, ...subjectWords];

    let blacklisted = false;
    // Check if the mail contains a blacklisted URL
    for (const word of allWords) {
        const response = await sendToBlacklistServer('GET', word);
        if (response.includes("true true")) {
            blacklisted = true;
            break;
        }
    }
    const newMail = new Mail({sender, receiver,  subject, content});
    await newMail.save();

    // Send mail to receiver's spam or inbox
    const senderOutbox = await getUserDefaultLabelsByName(sender, 'sent');
    await addMailToLabel(sender, senderOutbox, newMail.id);

    const receiverInbox = await getUserDefaultLabelsByName(receiver, 'inbox');
    await addMailToLabel(receiver, receiverInbox, newMail.id);
    if(blacklisted) {
        const receiverSpam = await getUserDefaultLabelsByName(receiver, 'spam');
        await addMailToLabel(receiver, receiverSpam, newMail.id);
    }
    return newMail;
}

const createDraft = async (sender, receiver, subject, content) => {
    // Cannot create empty draft
    if( !receiver && !subject && !content ) {
        return null;
    }

    const newMail = new Mail({sender, receiver,  subject, content});
    await newMail.save();

    // Add mail to sender's draft label
    const senderDrafts = await getUserDefaultLabelsByName(sender, 'draft');
    await addMailToLabel(sender, senderDrafts, newMail.id);

    return newMail;
}

const editDraft = async (mailID, sender, receiver = '', subject = '', content = '') => {
        
    const draftLabelId = await getUserDefaultLabelsByName(sender, 'draft');
    const draftMails   = await getMailsForLabel(sender, draftLabelId);
    if (!draftMails.includes(mailID)) return null;

    
    const allEmpty = [receiver, subject, content].every(
        v => typeof v === 'string' && v.trim() === ''
    );

    if (allEmpty) {
        await Label.updateMany(
        { username: sender, mails: mailID },  // labels that belong to sender *and* contain the ID
        { $pull: { mails: mailID } }
        );
        return null;
    }

    return Mail.findOneAndUpdate(
        { _id: mailID, sender },
        { receiver, subject, content, date: new Date() },
        { new: true }
    );
};


// Edit mail by id
const editMail = async (mailID, sender, receiver, subject, content) => {

    // Only mail's sender can edit the mail, if it's labels as a draft
    const senderDrafts = await getUserDefaultLabelsByName(sender, 'draft');
    const draftMails = await getMailsForLabel(sender, senderDrafts);

    // Make sure the mail belongs to the sender and is in drafts
    if (!draftMails.includes(mailID)) return null;


    // Change the mail fields
    const mail = await Mail.findOneAndUpdate(
        { _id: mailID, sender },
        { receiver, subject, content, date: new Date() },
        { new: true }
    );
    if (!mail) return null;

    // Split subject and content into a words array by chars like space, tab, enter (/\s+/)
    const contentWords = content.trim().split(/\s+/)
    const subjectWords = subject.trim().split(/\s+/)

    // Search blacklisted URL in content words, if a blacklisted URL was found, return null
    const allWords = [...contentWords, ...subjectWords];

    let blacklisted = false;
    // Check if the mail contains a blacklisted URL
    for (const word of allWords) {
        const response = await sendToBlacklistServer('GET', word);
        if (response.includes("true true")) {
            blacklisted = true;
            break;
        }
    }


    // Send mail to receiver's spam or inbox
    const senderOutbox = await getUserDefaultLabelsByName(sender, 'sent');
    await addMailToLabel(sender, senderOutbox, mail.id);

    const receiverInbox = await getUserDefaultLabelsByName(receiver, 'inbox');
    await addMailToLabel(receiver, receiverInbox, mail.id);
    if(blacklisted) {
        const receiverSpam = await getUserDefaultLabelsByName(receiver, 'spam');
        await addMailToLabel(receiver, receiverSpam, mail.id);
    }

    // Remove mail from sender drafts
    await removeMailFromLabel(sender, senderDrafts, mail.id);

    return true
}

// Delete mail by id
const deleteMail = async (mailID, username) => {

    // Check if mail if exist
    const result = await Mail.deleteOne({_id: mailID, sender: username});
    
    // Returns true if the mail was deleted
    return result.deletedCount > 0;
}

// const searchString = async (query = '', username) => {
//     console.log(username+"//////////////////////////////////////////////////////////////////////////////////////////")
//     //get all mails from the user
//     const userMails = { $or: [{ sender: username }, { receiver: username }] };

//     // If query is empty return the user’s mails
//     if (!query.trim()) {
//         return Mail.find(userMails).sort({ date: -1 }).lean();
//     }

//     const regex = new RegExp(query, 'i');

//     const result = await Mail.find({
//         ...userMails,
//         $or: [
//             { sender:   regex },
//             { receiver: regex },
//             { subject:  regex },
//             { content:  regex },
//         ],
//     })
//     .sort({ date: -1 })
//     .lean(); 

//     return result;

// }

const searchString = async (query = '', username) => {
    if (!query.trim()) {
        return Mail.find({
            $or: [
                { sender: username },
                { receiver: username }
            ]
        }).sort({ date: -1 }).lean();
    }

    const regex = new RegExp(query, 'i');

    const result = await Mail.find({
        $and: [
            {
                $or: [
                    { sender: username },
                    { receiver: username }
                ]
            },
            {
                $or: [
                    { sender:   regex },
                    { receiver: regex },
                    { subject:  regex },
                    { content:  regex }
                ]
            }
        ]
    })
    .sort({ date: -1 })
    .lean();

    return result;
};


module.exports = {
    getAllUserMails,
    getMail,
    createMail,
    editMail,
    deleteMail,
    searchString,
    createDraft,
    editDraft
}
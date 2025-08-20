const Label = require('../models/labels');

// Return all labels of a specific user
const getAllLabels = async (username) => {
    return await Label.find({ username });
}

// Return label by id
const getLabel = async (username, labelID) => {
    return await Label.findOne({ _id: labelID, username: username });
}

// Create new label
const createLabel = async (username, labelName) => {
    const label = new Label({labelName: labelName, username: username, mails: [] })
    await label.save();
    return label._id
}

// Edit label by id
const editLabel = async (labelID, labelName, username) => {
    const label = await getLabel(username, labelID);
    if (!label) return false;
    label.labelName = labelName;
    await label.save();
    return true;
}

// Delete label by id
const deleteLabel = async (labelID, username) => {
    const result = await Label.deleteOne({ _id: labelID, username: username });

    // Returns true if the label was deleted
    return result.deletedCount > 0;
}

// Add mail to label
const addMailToLabel = async (username, labelID, mailID) => {
    // Add to the mails variable if the username and labelID are correct
    const res = await Label.updateOne(
        { _id: labelID, username: username },
        { $addToSet: { mails: mailID } }
    );
    return res.modifiedCount > 0;
}


// Remove mail from label
const removeMailFromLabel = async (username, labelID, mailID) => {
    // Remove from the mails variable if the username and labelID are correct
    const res = await Label.updateOne(
        { _id: labelID, username: username },
        { $pull: { mails: mailID } }
    );
    return res.modifiedCount > 0;
}

// Get all mails for a specific label
const getMailsForLabel = async (username, labelID) => {
    const label = await getLabel(username, labelID);
    if (!label) return [];
    return label.mails;
}

module.exports = {
    getAllLabels,
    getLabel,
    createLabel,
    editLabel,
    deleteLabel,
    addMailToLabel,
    removeMailFromLabel,
    getMailsForLabel
}
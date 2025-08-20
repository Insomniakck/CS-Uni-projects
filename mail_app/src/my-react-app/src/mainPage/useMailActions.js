import * as mailAPI from "../APIfunctions/APIfunctions";

export function useMailActions(userID, setMails, starredIDs, setStarredIDs, readIDs, setReadIDs, setSelectedIDs, folder) {
    const handleStar = async (mailID) => {
        // address of the fetch request
        const isStarred = starredIDs.has(mailID) ? 'remove' : '';

        isStarred ? await mailAPI.removeMailToLabel(userID, mailID, 'starred') : await mailAPI.addMailToLabel(userID, mailID, 'starred');

        setStarredIDs((prev) => {
            const newSet = new Set(prev);
            if (newSet.has(mailID)) {
                // Unstar mail
                newSet.delete(mailID);
            } else {
                // Star mail
                newSet.add(mailID);
            }
            return newSet;
        });
    };

    const handleRead = async (mailID) => {
        // address of the fetch request
        const isRead = readIDs.has(mailID) ? 'remove' : '';

        isRead ? await mailAPI.removeMailToLabel(userID, mailID, 'read') : await mailAPI.addMailToLabel(userID, mailID, 'read');

        setReadIDs((prev) => {
            const newSet = new Set(prev);
            if (newSet.has(mailID)) {
                // Unread mail
                newSet.delete(mailID);
            } else {
                // Read mail
                newSet.add(mailID);
            }
            return newSet;
        });
    }

    const handleSpam = async (mailID) => {
        console.log("Reporting mail:", mailID);
        const spamMails = await mailAPI.getMailsFromLabel(userID, 'spam');
        const inSpam = spamMails.some(mail => mail._id === mailID);
        if (inSpam) {
            await mailAPI.deleteFromBlacklist(mailID, userID);
            await mailAPI.removeMailToLabel(userID, mailID, 'spam');
        } else {
            await mailAPI.addToBlacklist(mailID, userID);
            await mailAPI.addMailToLabel(userID, mailID, 'spam');
        }
        setMails((prevMails) => prevMails.filter((mail) => mail._id !== mailID));

    };

    const handleTrash = async (mailID) => {
        console.log("Deleting mail:", mailID);
        const deletedMails = await mailAPI.getMailsFromLabel(userID, 'trash');
        const inTrash = deletedMails.some(mail => mail._id === mailID);

        if (inTrash) {
            await mailAPI.removeMailToLabel(userID, mailID, 'trash');
        } else {
            await mailAPI.addMailToLabel(userID, mailID, 'trash');
        }
        setMails((prevMails) => prevMails.filter((mail) => mail._id !== mailID));

    };

    const handleSelect = async (mailID) => {
        setSelectedIDs((prev) => {
            const next = new Set(prev);
            next.has(mailID) ? next.delete(mailID) : next.add(mailID);
            return next;
        });
    }

    return { handleStar, handleSpam, handleTrash, handleRead, handleSelect }
}
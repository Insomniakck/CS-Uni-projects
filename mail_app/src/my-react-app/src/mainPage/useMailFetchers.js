import * as mailAPI from "../APIfunctions/APIfunctions";
import * as labelAPI from "../leftMenu/LeftMenuFunctions";
import { useEffect, useCallback } from "react";


export function useMailFetchers(userID, folder, setMails, setStarredIDs, setSenderInfo,
    setReadIDs, setLabels, setSelectedMail, setDrafts) {

    

    const fetchMails = useCallback(async () => {
        const isLabel = window.location.pathname.startsWith('/mails/label/');

        const labels = await mailAPI.getUserDefaultLabels(userID);
        console.log("labels", labels)
        const draft = await mailAPI.getMailsFromLabel(userID, null, labels.draft);
        const trash = await mailAPI.getMailsFromLabel(userID, null, labels.trash);
        const spam = await mailAPI.getMailsFromLabel(userID, null, labels.spam);

        setDrafts(draft);

        let mails = isLabel
            ? await mailAPI.getMailsFromLabel(userID, null, folder)
            : await mailAPI.getMailsFromLabel(userID, folder);

        if (!isLabel && folder === 'spam') {
            mails = mails.filter(mail => !trash.some(t => t._id === mail._id));
        } else if (isLabel || (!isLabel && folder !== 'trash')) {
            mails = mails.filter(mail =>
                !trash.some(t => t._id === mail._id) &&
                !spam.some(s => s._id === mail._id)
            );
        }

        setMails(mails);
        const users = await Promise.all(
            mails.map(mail => mailAPI.getUserInfo(userID, mail.sender))
        );
        setSenderInfo(users);
    }, [userID, folder, setMails, setSenderInfo, setDrafts]);

    const fetchStarred = useCallback(async () => {
        const mails = await mailAPI.getMailsFromLabel(userID, 'starred');
        setStarredIDs(new Set(mails.map((m) => m._id)));
    }, [userID, setStarredIDs]);

    const fetchRead = useCallback(async () => {
        const mails = await mailAPI.getMailsFromLabel(userID, 'read');
        if (!mails) return;
        setReadIDs(new Set(mails.map((m) => m._id)));
    }, [userID, setReadIDs]);

    useEffect(() => {
        if (userID && folder) {
            setSelectedMail(null);
            fetchStarred();
            fetchRead();
            fetchMails();
        }
    }, [userID, folder, fetchMails, fetchStarred, fetchRead, setSelectedMail]);

    const fetchLabels = async () => {
        const fetched = await labelAPI.getUserLabels(userID);
        setLabels(fetched);
    };

    const fetchSenderInfo = async (mails) => {
        const users = await Promise.all(
            mails.map(mail => mailAPI.getUserInfo(userID, mail.sender))
        );
        setSenderInfo(users);
    };

    return { fetchMails, fetchStarred, fetchRead, fetchLabels, fetchSenderInfo };
}

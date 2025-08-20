export async function addMailToLabel(userID, mailID, labelName, labelID = null) {
    try {
        if (labelID == null) {
            const res = await fetch(`http://localhost:8080/api/users/folderID`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-User-Id": userID.toString(),
                },
                body: JSON.stringify({ labelName: labelName }),
            });

            labelID = await res.json();
        }
        await fetch(`http://localhost:8080/api/labels/mail`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
            body: JSON.stringify({ labelID: labelID, mailID: mailID }),
        });

    } catch (err) {
        console.error("Failed to add mail to label", err);
    }
}

export async function removeMailToLabel(userID, mailID, labelName, labelID = null) {
    try {
        if (labelID == null) {
            const res = await fetch(`http://localhost:8080/api/users/folderID`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-User-Id": userID.toString(),
                },
                body: JSON.stringify({ labelName: labelName }),
            });

            labelID = await res.json();
        }


        await fetch(`http://localhost:8080/api/labels/mail/remove`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
            body: JSON.stringify({ labelID: labelID, mailID: mailID }),
        });

    } catch (err) {
        console.error("Failed to remove mail from label", err);
    }
}

export async function getMailsFromLabel(userID, labelName, labelID = null) {

    try {   
            if (labelID == null) {

                const res = await fetch(`http://localhost:8080/api/users/folderID`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "X-User-Id": userID.toString(),
                    },
                    body: JSON.stringify({ labelName: labelName }),
                });

                labelID = await res.json();
                
            }   

            const res1 = await fetch(`http://localhost:8080/api/labels/mail/${encodeURIComponent(labelID)}`,{
                    headers: {
                        "Content-Type": "application/json",
                        "X-User-Id": userID.toString(),
                    },
                }
            );

            const data = await res1.json();
            return data.mails;


    } catch (err) {
        console.error("Failed to fetch mails from label", err);
    }
}

export async function getUsernameByID(userID) {
    try {
        const res1 = await fetch(`http://localhost:8080/api/users/${encodeURIComponent(userID)}`, {
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
        }
        );

        const data = await res1.json();
        return data.username;

    } catch (err) {
        console.error("Failed to fetch username", err);
    }
}

export async function getMailByID(mailID, userID) {
    try {
        const res1 = await fetch(`http://localhost:8080/api/mails/${encodeURIComponent(mailID)}`, {
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
        }
        );

        const mail = await res1.json();
        return mail;

    } catch (err) {
        console.error("Failed to fetch mails", err);
    }
}

export async function addToBlacklist(mailID, userID) {
    const mail = await getMailByID(mailID, userID);
    const contentWords = mail.content.trim().split(/\s+/)
    const subjectWords = mail.subject.trim().split(/\s+/)
    const allWords = [...contentWords, ...subjectWords];
    for (const word of allWords) {
        try {
            await fetch(`http://localhost:8080/api/blacklist`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-User-Id": userID.toString(),
                },
                body: JSON.stringify({ url: word }),
            });

        } catch (err) {
            console.error("Failed to add mail content to blacklist", err);
        }
    }
}

export async function deleteFromBlacklist(mailID, userID) {
    const mail = await getMailByID(mailID, userID);
    const contentWords = mail.content.trim().split(/\s+/)
    const subjectWords = mail.subject.trim().split(/\s+/)
    const allWords = [...contentWords, ...subjectWords];
    for (const word of allWords) {
        try {
            const encodedWord = encodeURIComponent(word);
            await fetch(`http://localhost:8080/api/blacklist/${encodedWord}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    "X-User-Id": userID.toString(),
                },
                body: JSON.stringify({ url: word }),
            });

        } catch (err) {
            console.error("Failed to add mail content to blacklist", err);
        }
    }
}

// Get all labels belonging to user
export async function getLabelList(userID) {
    try {
        const res = await fetch(`http://localhost:8080/api/labels`, {
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
        });

        const allLabels = await res.json();
        return allLabels;

    } catch (err) {
        console.error("Failed to add mail content to blacklist", err);
    }
}

// Get labelID by labelName that belong to user
export async function getLabeID(userID, labelName) {
    try {
        const res = await fetch(`http://localhost:8080/api/users/folderID`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
            body: JSON.stringify({ labelName: labelName }),
        });

        const labelID = await res.json();
        return labelID;

    } catch (err) {
        console.error("Failed to add mail content to blacklist", err);
    }
}


export async function getLabeName(userID, labelID) {
    try {
        const res = await fetch(`http://localhost:8080/api/labels/${encodeURIComponent(labelID)}`, {
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
        });

        const label = await res.json();
        return label.labelName;

    } catch (err) {
        console.error("Failed to add mail content to blacklist", err);
    }
}


// Get User default label names and ID's
export async function getUserDefaultLabels(userID) {
    try {
        const res = await fetch(`http://localhost:8080/api/users/folder`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
        });

        const defaultLabels = await res.json();
        return defaultLabels;

    } catch (err) {
        console.error("Failed to get default labels", err);
    }

}

// Get User's firstName, lastName, username and profile image
export async function getUserInfo(userID, username) {
    try {
        const res = await fetch(`http://localhost:8080/api/users/username/${encodeURIComponent(username)}`, {
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userID.toString(),
            },
        });

        const user = await res.json();
        return user;

    } catch (err) {
        console.error("Failed to fetch user Info", err);
    }
}

export const searchString = async (userID, query) => {
    const res = await fetch(`http://localhost:8080/api/mails/search/${encodeURIComponent(query)}`, {
        headers: {
            "X-User-Id": userID,
            Accept: "application/json",
        },
    });
    if (!res.ok) throw new Error("Search failed");
    return res.json();
};



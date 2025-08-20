import React, { useState } from "react";

function ComposeMail({ onClose, userID, refreshMails, draft = null, darkMode }) {
  const [receiver, setReceiver] = useState(draft?.receiver || "");
  const [subject, setSubject] = useState(draft?.subject || "");
  const [content, setContent] = useState(draft?.content || "");
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);


  const createDraft = async () => {

    // Don't create empty drafts
    if (!receiver && !subject && !content) {
      return onClose();
    }

    try {
      // The message exists as a draft, edit it (instead of creating a new message)
      const method = draft ? "PATCH" : "POST";
      const mailID = draft ? draft?._id : "";
      console.log(method)

      await fetch(`http://localhost:8080/api/mails/draft/${encodeURIComponent(mailID)}`, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          "X-User-Id": userID.toString(),
        },
        body: JSON.stringify({ receiver, subject, content }),
      });


    } catch (err) {
      console.error("Failed to create draft:", err);
    } finally {
      // Close after trying to draft
      await refreshMails();
      onClose();
    }
  };


  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    try {

      // The message exists as a draft, edit it (instead of creating a new message)
      const method = draft ? "PATCH" : "POST";
      const mailID = draft ? draft?._id : "";


      const response = await fetch(`http://localhost:8080/api/mails/${encodeURIComponent(mailID)}`, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          "X-User-Id": userID.toString(),
        },
        body: JSON.stringify({ receiver, subject, content }),
      });

      if (response.status === 201 || response.status === 204) {
        setSuccess(true);
        setReceiver("");
        setSubject("");
        setContent("");

        await refreshMails();
        setTimeout(() => {
          onClose();
        }, 1000);

      } else {
        const result = await response.json();
        setError(result.error || "Failed to send mail");
      }
    } catch (err) {
      console.error("Send failed:", err);
      setError("Network error");
    }
  };

  return (
    <div
      className="card shadow"
      style={{
        position: "fixed",
        bottom: "2vh",
        right: "2vw",
        width: "35vw",
        height: "75vh",
        zIndex: 1050,
        borderRadius: "1rem",
        backgroundColor: darkMode ? "#2a2a2a" : "white",
        minWidth: "300px",
        minHeight: "300px",
      }}
    >
      <div id="composeBox" className={darkMode ? "dark-mode" : ""}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <strong id="newMessage">New Message</strong>
          <button onClick={createDraft} className="btn btn-sm btn-close" />
        </div>
        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">Mail sent successfully!</div>}

        <form id="composeForm" className="card-body" onSubmit={handleSubmit}>
          <input
            type="email"
            className="form-control mb-2"
            placeholder="To"
            id="composeTo"
            value={receiver}
            onChange={(e) => setReceiver(e.target.value)}
            required
          />
          <input
            type="text"
            className="form-control mb-2"
            placeholder="Subject"
            id="composeSubject"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
          />
          <textarea
            className="form-control mb-2"
            rows="8"
            placeholder="Write your message..."
            id="composeBody"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
          />
          <button id="sendButton" className="btn btn-primary" type="submit">
            Send
          </button>
        </form>
        </div>
    </div>
  );
}


export default ComposeMail;


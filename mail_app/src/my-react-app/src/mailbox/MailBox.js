function MailBox({ drafts, mails, onMailClick, senderInfo, onStar, onSpam, onTrash, starredIDs, onToggleRead,
  readIDs, selectedIDs, onSelect, folder, showToast, darkMode, setDarkMode }) {
  const mailsReversed = [...mails].reverse();
  console.log("mails",mails)
  const sendersReversed = [...senderInfo].reverse();
  return (
    <div className="list-group">
      {mailsReversed.map((mail, index) => {
        const sender = sendersReversed[index];
        return (
          <div
            key={index}
            className={`mailbox-wrapper list-group-item mail-item d-flex align-items-center ${readIDs.has(mail._id)
               ? "mail-read" : "mail-unread"}`}
            onClick={() => onMailClick(mail)}
          >
            {/* checkbox */}
            <label 
                className="custom-checkbox-wrapper me-2"
                onClick={(e) => e.stopPropagation()}
            >
              <input
                type="checkbox"
                checked={selectedIDs?.has(mail?._id)}
                onChange={() => onSelect(mail?._id)}
              />
              <span className="custom-checkmark"></span>
            </label>
            {/* star button */}
            <button
              className="btn btn-sm"
              title="Star"
              onClick={(e) => {
                e.stopPropagation();
                onStar(mail._id);
                starredIDs.has(mail._id) ? showToast("Unstar mail") : showToast("Star mail");
              }}>
              <i className={`bi ${starredIDs.has(mail?._id) ? "bi-star-fill" : "bi-star"}`}
                style={{
                    fontSize: "1.1rem",
                    fontWeight: "bold",
                    color: readIDs.has(mail._id)
                      ? (darkMode ? "#1faf6c" : "#05805b")
                      : (darkMode ? "#1faf6c" : "#05805b")
                  }} >
              </i>
            </button>

            {/* Sender wondow */}
            <div className="sender-tooltip-wrapper position-relative"
              style={{ flexBasis: "15%", flexShrink: 0, overflow: "visible" }}>
              <span
                style={{
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  fontWeight: readIDs.has(mail._id) ? "" : "bold",
                  color: darkMode
                    ? readIDs.has(mail._id) ? "#aaa" : "#e1e1e1" // dark mode
                    : readIDs.has(mail._id) ? "#246c45" : "#143b26", // light mode
                }}
              >
                {sender?.firstName} {sender?.lastName}
              </span>

              <div className=" sender-tooltip custom-tooltip p-2 rounded d-flex align-items-start gap-2">
                <img
                  src={sender?.profileImage ? `http://localhost:8080/uploads/${sender.profileImage}` : "/pictures/defaultProfile.png"}
                  alt="User"
                  width="40"
                  height="40"
                  className="rounded-circle"
                />
                <div>
                  <div className="fw-bold">
                    {sender?.firstName} {sender?.lastName}
                  </div>
                  <div className="small">{sender?.userName}</div>
                </div>
              </div>

            </div>

            {/* Mail content */}
            <div
              className="text"
              style={{
                flex: 1,
                minWidth: 0,
                display: "flex",
                overflow: "hidden",
                alignItems: "center",
                whiteSpace: "nowrap",
              }}
            >
              <span
                style={{
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  flexShrink: 1,
                  fontWeight: readIDs.has(mail._id) ? "" : "bold",
                  color: darkMode
                    ? readIDs.has(mail._id) ? "#aaa" : "#e1e1e1" // dark mode
                    : readIDs.has(mail._id) ? "#246c45" : "#143b26", // light mode
                }}
              >
                {drafts.find(m => m._id === mail._id)? "DRAFT  - " : ""}
                {mail.subject || "(No Subject)"}
              </span>
              <span style={{ flexShrink: 0, padding: "0 4px", color: "#888" }}>-</span>
              <span
                style={{
                  color: darkMode
                    ? readIDs.has(mail._id) ? "#aaa" : "#e1e1e1" // dark mode
                    : readIDs.has(mail._id) ? "#246c45" : "#143b26", // light mode
                  fontSize: "0.88rem",
                  overflow: "hidden",
                  padding: "0 4px",
                  marginTop: "2px",
                  textOverflow: "ellipsis",
                  flexShrink: 10,
                }}
              >
                {mail.content || ""}
              </span>
            </div>

            {/* Right-side actions */}
            <div
              className="mail-right-container d-flex align-items-center justify-content-end"
              style={{ minWidth: "100px" }}
            >

              {/* mail time */}
              <div
                className="mail-date text-muted text-end flex-shrink-0"
                id="mailDate"
                style={{ fontSize: "0.8rem" }}
              >

                {(() => {
                  const mailDate = new Date(mail.date);
                  const now = new Date();
                  const isToday =
                    mailDate.getDate() === now.getDate() &&
                    mailDate.getMonth() === now.getMonth() &&
                    mailDate.getFullYear() === now.getFullYear();
                  return isToday
                    ? mailDate.toLocaleTimeString([], { hour: "numeric", minute: "2-digit" })
                    : mailDate.toLocaleDateString([], { month: "short", day: "numeric" });
                })()}
              </div>
              <div className="mail-actions d-flex gap-2 flex-shrink-0">
                {/* spam */}
                <button
                  className="btn btn-sm"
                  title="Report"
                  onClick={(e) => {
                    e.stopPropagation();
                    onSpam(mail._id);
                    folder === 'spam' ? showToast("Removed mail from Spam") : showToast("Moved to Spam");

                  }}>
                  <i
                    className="bi bi-exclamation-circle-fill"
                    style={{
                      fontSize: "1.2rem",
                      fontWeight: "bold",
                      color: darkMode ? "#41ce93" : "#115c38"
                    }}>
                  </i>
                </button>
                {/* trash */}
                <button
                  className="btn btn-sm"
                  title="Delete"
                  onClick={(e) => {
                    e.stopPropagation();
                    onTrash(mail._id);
                    folder === 'trash' ? showToast("Removed mail from Trash") : showToast("Moved to Trash");
                  }}>
                  <i
                    className="bi bi-trash3-fill"
                    style={{
                      fontSize: "1.2rem",
                      fontWeight: "bold",
                      color: darkMode ? "#41ce93" : "#115c38"
                    }}>
                  </i>
                </button>
                {/* read */}
                <button
                  className="btn btn-sm"
                  title={readIDs.has(mail._id) ? "Mark as Unread" : "Mark as Read"}
                  onClick={(e) => {
                    e.stopPropagation();
                    onToggleRead(mail._id);
                    readIDs.has(mail._id) ? showToast("Marked mail as Unread") : showToast("Marked mail as Read");
                  }}
                >
                  <i
                    className={`bi ${readIDs.has(mail._id) ? "bi-envelope-open-fill" : "bi-envelope-fill"}`}
                    style={{
                      fontSize: "1.2rem",
                      fontWeight: "bold",
                      color: darkMode ? "#41ce93" : "#115c38"
                    }}>
                  </i>
                </button>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default MailBox;

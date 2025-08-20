import SingleMailActionBar from "./SingleMailActionBar";

function SingleMail({ userID, mail, sender, onBack, actionHandlers, labelUI, isRead, onRefresh, fetchLabels, starredIDs, selectedIDs, showToast, folder}) {
    if (!mail) return null;

    function timeAgo(dateString) {
        const now = new Date();
        const then = new Date(dateString);
        const diffMs = now - then; // difference in ms

        const seconds = Math.floor(diffMs / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (days > 0) return `${days} day${days > 1 ? 's' : ''} ago`;
        if (hours > 0) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
        if (minutes > 0) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
        return 'just now';
    }

    return (
    <div className="p-3">
      {/* action bar (no refresh) */}
      <SingleMailActionBar
        userID={userID}
        mailID={mail._id}
        onBack={onBack}
        isRead={isRead}
        onSpam={actionHandlers.handleSpam}
        onTrash={actionHandlers.handleTrash}
        onStar={actionHandlers.handleStar}
        starredIDs={starredIDs}
        onToggleRead={actionHandlers.handleRead}
        labelUI={labelUI}
        onRefresh={onRefresh}
        fetchLabels={fetchLabels}
        selectedIDs={selectedIDs}
        showToast={showToast}
        folder={folder}
      />

        <div className="p-3">
        {/* Subject big at top */}
            <h2 id="mailSubject" >{mail.subject}</h2>

            {/* Sender info + Time info on same horizontal line */}
            <div className="d-flex justify-content-between align-items-center mb-3">
                {/* Sender info */}
                <div className="d-flex align-items-center">
                    <img
                        src={sender?.profileImage ? `http://localhost:8080/uploads/${sender?.profileImage}` : "/pictures/defaultProfile.png"}
                        alt="User"
                        width="40"
                        height="40"
                        className="rounded-circle me-2"
                    />
                    <div>
                        <small id="senderInfo" className="text-muted d-block">
                            {[sender?.firstName, sender?.lastName].filter(Boolean).join(" ")}
                            {[" <", sender?.userName, ">"].filter(Boolean).join("")}
                        </small>
                        <small id="toRecipient" className="text-muted d-block">
                            {["To:", mail.receiver].filter(Boolean).join(" ")}
                        </small>
                    </div>

                </div>
                <div className="d-flex justify-content-between align-items-center mb-3 px-2">
                {/* Left: Time Info */}
                <div id="timeInfo" className="d-flex align-items-center gap-2 text-muted" style={{ fontSize: "0.9rem" }}>
                    <span>{new Date(mail.date).toLocaleString()} ({timeAgo(mail.date)})</span>
                </div>

                
                </div>

            </div>

                {/* Mail content */}
                <p id="mailContent" style={{ whiteSpace: "pre-wrap" }}>{mail.content}</p>
            </div>

    </div>


  );

}
export default SingleMail;

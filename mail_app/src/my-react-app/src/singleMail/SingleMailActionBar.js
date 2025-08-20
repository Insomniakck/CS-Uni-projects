import LabelDropdown from "../mainPage/LabelDropdown";
import { useRef } from "react";


/** Actions for a *single* open mail (no Refresh button) */
export default function SingleMailActionBar({
  userID,
  mailID,
  onBack,
  isRead,
  onSpam,
  onTrash,
  onStar,
  starredIDs,
  onToggleRead,
  labelUI,
  onRefresh,
  fetchLabels,
  selectedIDs,
  showToast,
  folder,
}) {

  const labelButtonRef = useRef(null);
    
  return (
    
    <div className="bulk-action-bar single-action-bar d-flex align-items-center gap-4 px-3 py-2 border-bottom position-relative">
      {/* back arrow */}
      <button className="btn btn-sm" 
            title="Back" 
            onClick={() => {
                if (!isRead) onToggleRead(mailID);
                onBack();
        }}
      >
        <i className="bi bi-arrow-left back-icon fs-5 fw-bold"></i>
      </button>
      {/* star */}
      <button 
        className="btn btn-sm" 
        title="Star" 
        onClick={(e) => { 
          e.stopPropagation(); 
          onStar( mailID) 
          starredIDs.has(mailID) ? showToast("Unstarred mail") : showToast("Starred mail");
          }}>
          <i className={`bi ${starredIDs.has(mailID) ? "bi-star-fill " : "bi-star"}`}
              style={{ fontSize: "1.1rem", fontWeight: "bold" }}>
          </i>
      </button>
      {/* spam */}
      <button 
        className="btn btn-sm" 
        title="Report" 
        onClick={() => {
          onSpam(mailID);  
          folder === 'spam'? showToast("Removed mail from Spam") : showToast("Moved mail to Spam");
          onBack();}}>
        <i className="bi bi-exclamation-circle-fill fs-5 fw-bold" />
      </button>
      {/* trash */}
      <button 
        className="btn btn-sm" 
        title="Delete" 
        onClick={() => {
          onTrash(mailID);
          folder === 'trash'? showToast("Removed mail from Trash") : showToast("Moved mail to Trash");
          onBack();}}>
        <i className="bi bi-trash3-fill fs-5 fw-bold" />
      </button>
      {/* read */}
      <button
        className="btn btn-sm"
        title={"Mark as Unread"}
        onClick={() => {
            if (isRead) onToggleRead(mailID);
            onBack();
        }}
      >
        <i className={`bi ${isRead ? "bi-envelope-open-fill" : "bi-envelope-fill"} fs-5 fw-bold`} />
      </button>

      {/* label toggle */}
      <div className="position-relative">
        <button className="btn btn-sm" title="Label" onClick={labelUI.toggle} ref={labelButtonRef}>
          <i className="bi bi-bookmark-fill fs-5 fw-bold" />
        </button>

        {mailID !== undefined && labelUI.dropdownProps.show && (
          <LabelDropdown
            show={true}
            labels={labelUI.dropdownProps.labels}
            labelStates={labelUI.dropdownProps.labelStates.states}
            selectedIDs={selectedIDs}
            userID={userID}
            onApplyChangesDone={() => {
                onRefresh();
                fetchLabels();
                showToast("Labels updated");
            }}
            onCreateLabel={labelUI.dropdownProps.onCreateLabel}
            onBack={onBack}
            onClose={() => {labelUI.dropdownProps.onClose(); onBack();}}
            labelButtonRef={labelButtonRef}
            showToast={showToast}
            onLabelCreated={() => {
              fetchLabels();
              showToast?.("New label created");
            }}
            fetchLabels={fetchLabels}
          />

        )}
      </div>
    </div>
  );
}

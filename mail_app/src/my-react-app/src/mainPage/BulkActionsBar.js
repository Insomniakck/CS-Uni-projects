import LabelDropdown from "./LabelDropdown";

import { useState, useRef, useEffect } from "react";

import * as labelAPI from "../leftMenu/LeftMenuFunctions";


export default function BulkActionsBar({
  userID,
  fetchLabels,
  selectedIDs,
  allSelectedAreRead,
  onSpam,
  onTrash,
  onMarkRead,
  onRefresh,
  labelUI,
  folder,
  showToast,
  refreshLabels,
  setRefreshLabels,
}) {
  const disabled = selectedIDs.size === 0;
  const labelButtonRef = useRef(null);
  const [labels, setLabels] = useState([]);
  console.log(labels)
  useEffect(() => {
          async function fetchLabels() {
              const fetched = await labelAPI.getUserLabels(userID);
              setLabels(fetched);
          }
          fetchLabels();
      }, [userID, refreshLabels, setLabels]);
  
  return (
    <div className="list-group">
      <div
        className="bulk-action-bar bulk-actions-green d-flex align-items-center gap-3 px-3 py-2 border-bottom position-relative"
        style={{ opacity: disabled ? 0.4 : 1, pointerEvents: disabled ? "none" : "auto" }}
      >
        {/* spam button*/}
        <button 
          className="btn btn-sm" 
          title= {folder==='spam'? "Report": "Retreive"} 
          onClick={() => {
            onSpam();
            folder === 'spam'? showToast("Removed mails from Spam") : showToast("Moved mails to Spam");
            }}>
          <i className="bi bi-exclamation-circle-fill fs-5 fw-bold" />
        </button>
        {/* trash button */}
        <button 
          className="btn btn-sm" 
          title={folder==='trash'? "Delete": "Retreive"} 
          onClick={() => {
            onTrash();
            folder === 'trash'? showToast("Removed mails from Trash") : showToast("Moved mails to Trash");
            }}>
          <i className="bi bi-trash3-fill fs-5 fw-bold" />
        </button>
        {/* read button */}
        <button
          className="btn btn-sm"
          title={allSelectedAreRead ? "Mark as Unread" : "Mark as Read"}
          onClick={() => {
            onMarkRead();
            allSelectedAreRead ? showToast("Marked mails as Unread") : showToast("Marked mails as Read");
            
          }}
        >
          <i
            className={`bi ${
              allSelectedAreRead ? "bi-envelope-open-fill" : "bi-envelope-fill"
            } fs-5 fw-bold`}
          />
        </button>

        {/* Label toggle */}
        <div className="position-relative">
          <button className="btn btn-sm" title="Label" onClick={labelUI.toggle} ref={labelButtonRef}>
            <i className="bi bi-bookmark-fill fs-5 fw-bold" />
          </button>
          {labelUI.dropdownProps.show && (
          <LabelDropdown
            show={true}
            labels={labelUI.dropdownProps.labels}
            labelStates={labelUI.dropdownProps.labelStates.states}
            selectedIDs={selectedIDs}
            userID={userID}
            onApplyChangesDone={async () => {
              await onRefresh();
              await fetchLabels();
              showToast("Labels updated");
            }}
            onClose={() => {labelUI.dropdownProps.onClose(); }}
            labelButtonRef={labelButtonRef}
            oonCreateLabel={() => {
              labelUI.dropdownProps.onCreateLabel();
              setRefreshLabels(prev => prev + 1); // popup opens – optional
            }}
            onLabelCreated={() => {               // fires after user saves
              fetchLabels();                      // update central labels
              setRefreshLabels(prev => prev + 1); // triggers LeftMenu useEffect
              showToast?.("New label created");
            }}
            showToast={showToast}
            fetchLabels={fetchLabels}
          />
          )}
        </div>


        {/* Refresh on far right */}
        <button className="btn btn-sm" title="Refresh" onClick={onRefresh}>
          <i className="bi bi-arrow-clockwise fs-5 fw-bold" />
        </button>

      </div>
    </div>
  );
}

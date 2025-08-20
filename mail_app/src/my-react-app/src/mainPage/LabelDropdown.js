import { useState, useEffect, useRef } from "react";
import * as mailAPI from "../APIfunctions/APIfunctions";
import CreateLabelPopup from "../createLabel/CreateLabel";

export default function LabelDropdown({
  show,
  labels,
  labelStates,
  selectedIDs,
  userID,
  onApplyChangesDone,
  onCreateLabel,
  onClose,
  labelButtonRef = null,
  showToast,
  fetchLabels,
}) {
  const boxRef = useRef(null);
  const [draft, setDraft] = useState({});
  const [showCreate, setShowCreate] = useState(false);

  /* -------- helper functions -------- */
  const cycle = (cur, init) =>
    init === "some"
      ? cur === "some"
        ? "all"
        : cur === "all"
        ? "none"
        : "some"
      : cur === "all"
      ? "none"
      : "all";

  const toggleLabel = (id) =>
    setDraft((p) => {
      const init = labelStates[id] ?? "none";
      return { ...p, [id]: cycle(p[id] ?? init, init) };
    });

  const hasChanges = Object.keys(draft).some(
    (id) => draft[id] !== (labelStates[id] ?? "none")
  );

  const applyChanges = async () => {

    for (const [labelID, newState] of Object.entries(draft)) {
      const oldState = labelStates[labelID] ?? "none";
      if (newState === oldState || newState === "some") continue;

      selectedIDs.forEach((mailID) => {
        newState === "all"
          ? mailAPI.addMailToLabel(userID, mailID, null, labelID)
          : mailAPI.removeMailToLabel(userID, mailID, null, labelID);
      });
    }
    onApplyChangesDone();
    onClose(); // hide dropdown
  };

  /* -------- outside‑click logic -------- */
  useEffect(() => {
    if (!show) return;

    const handleClick = (e) => {
      if (showCreate) return; // keep open while popup is showing

      if (
        boxRef.current &&
        !boxRef.current.contains(e.target) &&
        !(labelButtonRef?.current?.contains(e.target))
      ) {
        onClose();
      }
    };

    document.addEventListener("click", handleClick);
    return () => document.removeEventListener("click", handleClick);
  }, [show, showCreate, onClose, labelButtonRef]);

  if (!show) return null;

  return (
    <div
      ref={boxRef}
      className="dropdown-label-asign-menu show p-2 dropdown-label-asign-menu-end"
      style={{ minWidth: 220, zIndex: 20 }}
    >
      <div className="fw-semibold mb-2">Labels</div>

      {labels.map((lbl) => {
        const state = draft[lbl._id] ?? labelStates[lbl._id] ?? "none";
        return (
          <button
            type="button"
            key={lbl._id}
            className="dropdown-item d-flex align-items-center gap-1 label-row-spacing"
            onMouseDown={(e) => e.preventDefault()}
            
          >
            <label className="custom-checkbox-wrapper me-2">
              <input
                type="checkbox"
                checked={state === "all"}
                onChange={() => toggleLabel(lbl._id)}
                ref={(el) => el && (el.indeterminate = state === "some")}
              />
              <span className="custom-checkmark"></span>
            </label>
            <span className="text-truncate-label-dropdown" title={lbl.labelName}>
              {lbl.labelName}
            </span>
          </button>

        );
      })}

      <div className="dropdown-divider" />

      {/* create new label */}
      <button
        type="button"
        className="dropdown-item label-dropdown-label-asign-menu"
        onClick={() => setShowCreate(true)}
      >
        + Create new label
      </button>

      {hasChanges && (
        <>
          <div className="dropdown-divider" />
            <button
              type="button"
              className="dropdown-item label-dropdown-label-asign-menu w-100"
              onClick={applyChanges}
            >
              Apply changes
            </button>
        </>
      )}

      {/* popup */}
      {showCreate && (
        <CreateLabelPopup
          userID={userID}
          onClose={() => setShowCreate(false)}
          editLabel={null}
          onCreate={() =>{
            fetchLabels();
            setShowCreate(false);
            showToast?.("New label created");
            onClose();
          }}
          showToast={showToast}
        />
      )}
    </div>
  );
}

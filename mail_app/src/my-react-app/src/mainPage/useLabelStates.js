import { useState, useEffect } from "react";
import * as mailAPI from "../APIfunctions/APIfunctions";

/**
 * Returns a record { [labelID]: "none" | "some" | "all" }
 * so the UI can show □ — ✓ for each label.
 */
export default function useLabelStates({
  userID,
  labels,
  mails,
  selectedIDs = new Set(),
  active, // only compute when dropdown is open
}) {

  const [labelStates, setLabelStates] = useState({});

  useEffect(() => {
    if (!active || selectedIDs.size === 0) {
      setLabelStates({});
      return;
    }

    let cancelled = false;

    (async () => {
      const newStates = {};

      for (const lbl of labels) {
        const mailsWithLabel = await mailAPI.getMailsFromLabel(userID, null, lbl._id);

        let count = 0;
        selectedIDs.forEach(id => {
          if (mailsWithLabel.find(m => m._id === id)) count += 1;
        });

        newStates[lbl._id] =
          count === 0
            ? "none"
            : count === selectedIDs.size
            ? "all"
            : "some";

      }

      if (!cancelled) setLabelStates(newStates);
    })();
    return () => (cancelled = true);
  }, [userID, labels, mails, selectedIDs, active]);

  const isLabelAppliedToAll = (labelID) => {
    for (const mail of mails) {
      if (selectedIDs.has(mail._id)) {
        const mailLabelIDs = mail.labels || [];
        if (!mailLabelIDs.includes(labelID)) {
          return false;
        }
      }
    }
    return true;
  };

  return { states: labelStates, isLabelAppliedToAll };
}

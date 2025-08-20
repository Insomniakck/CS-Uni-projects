/**
 * Bulk‑action helpers (spam, trash, mark‑read, checkbox toggle).
 * Single‑mail handlers come in through apiHandlers.
 */
export default function useMailBulk({
  readIDsState,
  selectedIDsState,
  apiHandlers,
}) {
  const [readIDs] = readIDsState;
  const [selectedIDs, setSelectedIDs] = selectedIDsState;

  const { handleSpam, handleTrash, handleRead } = apiHandlers;

  /* Toggle one checkbox in MailBox */
  const toggleSelect = (id) => {
    setSelectedIDs((prev) => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  };

  /* Bulk Spam / Trash */
  const bulkSpam = () => { selectedIDs.forEach((id) => handleSpam(id)); setSelectedIDs(new Set()); };
  const bulkTrash = () => { selectedIDs.forEach((id) => handleTrash(id)); setSelectedIDs(new Set()); };

  /* Bulk Read / Unread (handleRead toggles) */
  const bulkMarkRead = () => {
    const ids = [...selectedIDs]; // Convert Set to Array
    const allAreRead = ids.every(mailID => readIDs.has(mailID));

    if (allAreRead) {
      // All are read - mark all as unread
      ids.forEach(mailID => handleRead(mailID));
    } else {
      // Some are unread - mark only the unread ones as read
      ids
        .filter(mailID => !readIDs.has(mailID))
        .forEach(mailID => handleRead(mailID));
    }

    setSelectedIDs(new Set()); // Clear selection
  };

  return { toggleSelect, bulkSpam, bulkTrash, bulkMarkRead };
}

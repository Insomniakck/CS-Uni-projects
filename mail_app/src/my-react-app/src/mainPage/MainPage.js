import { useState, useEffect } from "react";
import { useLocation, useParams } from "react-router-dom";
import { useMailActions } from "./useMailActions";
import { useMailFetchers } from "./useMailFetchers";
import useLabelStates from "./useLabelStates";
import useMailBulk from "./useMailBulk";
import * as mailAPI from "../APIfunctions/APIfunctions";
import NavBar from "../navBar/NavBar";
import LeftMenu from "../leftMenu/LeftMenu";
import MailBox from "../mailbox/MailBox";
import ComposeMail from "../composeMail/ComposeMail";
import SingleMail from "../singleMail/SingleMail";
import CreateLabelPopup from "../createLabel/CreateLabel";
import BulkActionsBar from "./BulkActionsBar";
import Toast from "../ToastMessage/Toast";
import "../App.css";


function MainPage() {
  const location = useLocation();
  const { folder } = useParams();
  const userID = useLocation().state?.userID || localStorage.getItem("userID");
  console.log("userid from mainpage", userID);
  const [toast, setToast] = useState(null);

    const showToast = (message) => {
        setToast(message);
    };

  /* UI state */
  const [selectedMail, setSelectedMail] = useState(null);
  const [showCompose, setShowCompose] = useState(false);
  const [editingDraft, setEditingDraft] = useState(null);
  const [showLabelMenu, setShowLabelMenu] = useState(false);
  const [editingLabel, setEditingLabel] = useState(null);

  /* Data state */
  const [senderInfo, setSenderInfo] = useState([]);
  const [mails, setMails] = useState([]);
  const [drafts, setDrafts] = useState([]);
  const [labels, setLabels] = useState([]);
  const [starredIDs, setStarredIDs] = useState(new Set());
  const [selectedIDs, setSelectedIDs] = useState(new Set());
  const [readIDs, setReadIDs] = useState(new Set());
  const [refreshLabels, setRefreshLabels] = useState(0);


  /* Fetch helpers */
  const { fetchMails, fetchLabels, fetchSenderInfo } = useMailFetchers(
    userID, folder, setMails, setStarredIDs, setSenderInfo, setReadIDs, setLabels, setSelectedMail, setDrafts
  );

  /* Single‑mail handlers */
  const {
    handleStar,
    handleSpam,
    handleTrash,
    handleRead,
  } = useMailActions(
    userID,
    setMails,
    starredIDs,
    setStarredIDs,
    readIDs,
    setReadIDs,
    setSelectedIDs,
    folder
  );

  /* Bulk handlers */
  const bulkHandlers = useMailBulk({
    readIDsState: [readIDs, setReadIDs],
    selectedIDsState: [selectedIDs, setSelectedIDs],
    apiHandlers: { handleSpam, handleTrash, handleRead }
  });

  /* Label checkbox states */
  const labelStates = useLabelStates({
    userID,
    labels,
    mails,
    selectedIDs: selectedIDs,
    active: showLabelMenu,
  });

  /* Build label UI bundle for BulkActionsBar */
  const labelUI = {
    toggle: () => {
      setShowLabelMenu((prev) => !prev);
      fetchLabels();
    },
    dropdownProps: {
      show: showLabelMenu,
      labels,
      labelStates,
      onSelectLabel: async (labelID) => {
        await Promise.all(
          [...selectedIDs].map(id => mailAPI.addMailToLabel(userID, id, null, labelID))
        );
        setShowLabelMenu(false);

        await fetchMails();
        await fetchLabels();
      },
      onCreateLabel: () => {
        setShowLabelMenu(false);
        setEditingLabel({ labelID: null, labelName: "" });
      },
      onClose: () => setShowLabelMenu(false),
    },
  };

  const allSelectedAreRead = [...selectedIDs].every((id) => readIDs.has(id));

  // Dark mode
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem("darkMode") === "true";
  });

  useEffect(() => {
    const isAuthPage = location.pathname === "/login" || location.pathname === "/signup";

    if (!isAuthPage && darkMode) {
      document.body.classList.add("dark-mode");
    } else {
      document.body.classList.remove("dark-mode");
    }

    localStorage.setItem("darkMode", darkMode);
  }, [darkMode, location.pathname]);

  /* ─── render ─────────────────────────────────────── */
  return (
    <div>
      <NavBar 
        userId={userID} 
        setMails={setMails} 
        darkMode={darkMode} 
        setDarkMode={setDarkMode}
        fetchSenderInfo={fetchSenderInfo} 
        />

      <div className="container-fluid" style={{ paddingTop: "100px" }}>
        <div className="row">
          <LeftMenu
            onComposeClick={() => setShowCompose(true)}
            userID={userID}
            refreshTrigger={() => {refreshLabels()}}
            showToast={showToast}
             />


          <div className="col-10">
            {selectedMail ? (
              <SingleMail 
                  userID = {userID}
                  mail={selectedMail}
                  sender={senderInfo[mails.findIndex(mail => mail._id === selectedMail._id)]}
                  onBack={() => {setSelectedMail(null); setSelectedIDs(new Set());}}
                  actionHandlers= {{handleSpam, handleTrash, handleRead, handleStar}}
                  labelUI = {labelUI}
                  isRead={readIDs.has(selectedMail?._id)}
                  onRefresh={fetchMails}
                  fetchLabels= {fetchLabels}
                  starredIDs={starredIDs}
                  selectedIDs={selectedIDs}
                  showToast={showToast}
                  folder={folder}
                  refreshLabels={refreshLabels}
              />
            ) : (
              <>
                <BulkActionsBar
                  userID = {userID}
                  fetchLabels = {fetchLabels}
                  selectedIDs={selectedIDs}
                  allSelectedAreRead={allSelectedAreRead}
                  onSpam={bulkHandlers.bulkSpam}
                  onTrash={bulkHandlers.bulkTrash}
                  onMarkRead={bulkHandlers.bulkMarkRead}
                  onRefresh={() => {fetchMails(); setSelectedIDs(new Set());}}
                  labelUI={labelUI}
                  folder={folder}
                  showToast={showToast}
                  refreshLabels={refreshLabels}
                  setRefreshLabels={setRefreshLabels}
                />

                <MailBox
                  drafts = {drafts}
                  mails={mails}
                  senderInfo={senderInfo}
                  starredIDs={starredIDs}
                  readIDs={readIDs}
                  selectedIDs={selectedIDs}
                  onMailClick={(mail) => {
                    if (drafts.find(m => m._id === mail._id)) {
                      setEditingDraft(mail);
                      setShowCompose(true);
                    } else {
                      setSelectedMail(mail);
                      setSelectedIDs(new Set([mail._id]));
                    }
                  }}
                  onStar={handleStar}
                  onSpam={handleSpam}
                  onTrash={handleTrash}
                  onToggleRead={handleRead}
                  onSelect={bulkHandlers.toggleSelect}
                  folder={folder}
                  showToast={showToast}
                  darkMode={darkMode}
                  setDarkMode={setDarkMode}
                />
              </>
            )}
          </div>
        </div>
      </div>

      {/* Compose mail */}
      {showCompose && (
        <ComposeMail
          onClose={() => {
            setShowCompose(false);
            setEditingDraft(null);
          }}
          userID={userID}
          refreshMails={fetchMails}
          draft={editingDraft}
          darkMode={darkMode}
        />
      )}

      {/* Create / edit label popup */}
      {editingLabel && (
        <CreateLabelPopup
          userID={userID}
          editLabel={editingLabel._id ? editingLabel : null}
          onClose={() => setEditingLabel(null)}
          onCreate={() => {
            fetchLabels();
            setRefreshLabels(prev => prev + 1);
            setEditingLabel(null);
          }}
          darkMode={darkMode}
        />
      )}
      {/* Toast message */}
      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  );
}

export default MainPage;
import { useState, useEffect } from 'react';
import { NavLink } from "react-router-dom";
import * as labelAPI from "./LeftMenuFunctions";
import CreateLabelPopup from "../createLabel/CreateLabel";

function LeftMenu({ onComposeClick, userID, refreshTrigger , showToast}) {
    const linkBase = "list-group-item list-group-item-action left-menu rounded-label"; 
    const [labels, setLabels] = useState([]);
    const [localToggle, setLocalToggle] = useState(false); //  for delete/edit only
    const [openMenuLabel, setOpenMenuLabel] = useState(null);
    const [editingLabel, setEditingLabel] = useState(null);

    useEffect(() => {

        async function fetchLabels() {
            const fetched = await labelAPI.getUserLabels(userID);
            setLabels(fetched);
        }
        fetchLabels();
    }, [userID, refreshTrigger, localToggle]);

    function handleEditLabel(label) {
        setOpenMenuLabel(null);
        setEditingLabel(label); // open popup with label info
    }

    function handleDeleteLabel(labelID, labelName) {
        setOpenMenuLabel(null);
        if (window.confirm(`Are you sure you want to delete label "${labelName}"?`)) {
            labelAPI.deleteLabel(labelID, userID).then(() => {
                setLocalToggle(prev => !prev);

            });
        }
    }

    return (
        <div className="col-2 vh-100">
            <ul className="list-group  text-label-hover">

                <div className="p-2 pb-3">
                    <li className="bg-transparent border-0 list-unstyled">
                        <button
                            id='composeButton'
                            className="rounded-label btn btn-primary w-100 fw-bold"
                            onClick={() => onComposeClick(userID)}
                        >
                            <i className="bi bi-pencil me-2"></i>
                            Compose
                        </button>
                    </li>
                </div>

                <NavLink
                    to="/mails/inbox"
                    state={{ userID }}
                    className={({ isActive }) =>
                        `${linkBase} ${isActive ? "rounded-label active text-white fw-bold" : ""}`
                    }
                >
                    {({ isActive }) => (
                        <>
                        <i
                            className={isActive ? "bi bi-inbox-fill" : "bi bi-inbox"}
                        />
                        <span className="w-100 m-3">Inbox</span>
                        </>
                    )}
                </NavLink>
                <NavLink
                    to="/mails/starred"
                    state={{ userID }}
                    className={({ isActive }) =>
                        `${linkBase} ${isActive ? "active text-white fw-bold" : ""}`
                    }
                >
                    {({ isActive }) => (
                        <>
                        <i
                            className={isActive ? "bi bi-star-fill" : "bi bi-star"}
                        />
                        <span className="w-100 m-3">Starred</span>
                        </>
                    )}
                </NavLink>
                <NavLink
                    to="/mails/sent"
                    state={{ userID }}
                    className={({ isActive }) =>
                        `${linkBase} ${isActive ? "active text-white fw-bold" : ""}`
                    }
                >
                    {({ isActive }) => (
                        <>
                        <i
                            className={isActive ? "bi bi-send-fill" : "bi bi-send"}
                        />
                        <span className="w-100 m-3">Sent</span>
                        </>
                    )}
                </NavLink>
                <NavLink
                    to="/mails/draft"
                    state={{ userID }}
                    className={({ isActive }) =>
                        `${linkBase} ${isActive ? "active text-white fw-bold" : ""}`
                    }
                >
                    {({ isActive }) => (
                        <>
                        <i
                            className={isActive ? "bi bi-sticky-fill" : "bi bi-sticky"}
                        />
                        <span className="w-100 m-3">Drafts</span>
                        </>
                    )}
                </NavLink>

                <NavLink
                    to="/mails/spam"
                    state={{ userID }}
                    className={({ isActive }) =>
                        `${linkBase} ${isActive ? "active text-white fw-bold" : ""}`
                    }
                >
                    {({ isActive }) => (
                        <>
                        <i
                            className={isActive ? "bi bi-exclamation-circle-fill" : "bi bi-exclamation-circle"}
                        />
                        <span className="w-100 m-3">Spam</span>
                        </>
                    )}
                </NavLink>

                <NavLink
                    to="/mails/trash"
                    state={{ userID }}
                    className={({ isActive }) =>
                        ` ${linkBase} ${isActive ? "active text-white fw-bold" : ""}`
                    }
                >
                    {({ isActive }) => (
                        <>
                        <i
                            className={isActive ? "bi bi-trash3-fill" : "bi bi-trash3"}
                        />
                        <span className="w-100 m-3">Trash</span>
                        </>
                    )}
                </NavLink>

                {/* Labels header */}
                <li className="rounded-label left-menu list-group-item text-label-hover d-flex justify-content-between align-items-center">
                    <div>
                        <i className="bi bi-bookmark"></i>
                        <span className="m-3">Labels</span>
                    </div>
                    <button
                        type="button"
                        className="btn fw-bold add-label-btn"
                        onClick={() => {setEditingLabel({ labelID: null, labelName: "" });}}
                        style={{
                            border: "none",
                            background: "transparent",
                            padding: 0,
                            boxShadow: "none"
                        }}
                        aria-label="Add Label"
                    >
                        <i className="bi bi-plus-lg"></i>
                    </button>
                </li>

                {/* Custom labels */}
                {labels.map((label, idx) => (
                    <NavLink
                        key={idx}
                        to={`/mails/label/${encodeURIComponent(label._id)}`}
                        state={{ userID }}
                        className={({ isActive }) =>
                            `rounded-label left-menu list-group-item list-group-item-action d-flex justify-content-between align-items-center ps-4 ${isActive ? "active fw-bold text-white" : ""}`
                            }
                        style={{ minWidth: 0 }}
                    >
                        {({ isActive }) => (
                        <>
                            <div className="d-flex align-items-center overflow-hidden">
                            <i
                                className={isActive ? "bi bi-tag-fill me-2 flex-shrink-0" : "bi bi-tag me-2 flex-shrink-0"}
                            />
                            <span
                                className="text-truncate-label ms-1"
                                title={label.labelName}
                                style={{
                                whiteSpace: "nowrap",
                                overflow: "hidden",
                                textOverflow: "ellipsis"
                                }}
                            >
                                {label.labelName}
                            </span>
                            </div>
                            {/* Three dots */}
                            <button
                            className="btn p-0 three-dot-button flex-shrink-0"
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                setOpenMenuLabel(openMenuLabel === label ? null : label);
                            }}
                            style={{
                                border: "none",
                                background: "transparent",
                                marginLeft: "8px"
                            }}
                            aria-label="Label options"
                            >
                            <i className="bi bi-three-dots-vertical"></i>
                            </button>
                            {/* Three dotes menu */}
                            {openMenuLabel === label && (
                            <div
                                className="dropdown-menu show p-1 label-dropdown-menu"
                                style={{
                                    position: "absolute",
                                    left: "100%",
                                    top: "-100%",
                                    zIndex: 10
                                }}
                                onMouseLeave={() => setOpenMenuLabel(null)}
                                >

                                <button className="dropdown-item" onClick={() => handleEditLabel(label)}>Edit</button>
                                <button
                                className="dropdown-item"
                                onClick={() => {
                                    showToast(`Label "${label.labelName}" deleted.`);
                                    handleDeleteLabel(label._id, label.labelName);
                                }}
                                >
                                Delete
                                </button>
                            </div>
                            )}
                        </>
                        )}
                    </NavLink>
                    ))}


            </ul>

            {/* Label creation popup */}
            {editingLabel && (
                <CreateLabelPopup
                    onClose={() => setEditingLabel(null)}
                    onCreate={() => {
                        setLocalToggle(prev => !prev);
                        setEditingLabel(null);

                        const wasEdit = editingLabel?._id != null;
                        showToast(wasEdit ? "Label updated" : "New label created");
                    }}
                    userID={userID}
                    editLabel={editingLabel._id ? editingLabel : null}
                />
            )}

        </div>
    );
}

export default LeftMenu;

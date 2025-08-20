import { useState, useEffect } from 'react';

function CreateLabelPopup({ onClose, onCreate, userID, editLabel = null , showToast = null, darkMode}) {
    const [labelName, setLabelName] = useState(editLabel?.labelName || "");
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        if (editLabel) {
            setLabelName(editLabel.labelName);
        }
    }, [editLabel]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);

        if (!labelName.trim()) {
            setError("Label name cannot be empty");
            return;
        }

        try {
            const endpoint = editLabel
                ? `http://localhost:8080/api/labels/${editLabel._id}`
                : "http://localhost:8080/api/labels";

            const method = editLabel ? "PATCH" : "POST";

            const response = await fetch(endpoint, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    "X-User-Id": userID.toString(),
                },
                body: JSON.stringify({ labelName: labelName.trim() }),
            });


            if (response.ok) {
                setSuccess(true);

                onCreate(); // tell parent to refresh
                console.log("showToast", showToast)
                showToast?.("New label created");
                setTimeout(() => onClose(), 800);
            } else {
                const result = await response.json();
                setError(result.error || "Label operation failed");
            }
        } catch (err) {
            console.error("Label operation failed:", err);
            setError("Network error");
        }
    };

    return (
        <div
            id='createLabelWindow'
            className="card shadow"
            style={{
                position: "fixed",
                top: "50%",
                left: "50%",
                transform: "translate(-50%, -50%)",
                width: "30vw",
                minWidth: "300px",
                padding: "1.5rem",
                zIndex: 1050,
                borderRadius: "1rem",
            }}
        >
            <div className="card-header d-flex justify-content-between align-items-center">
                <strong>{editLabel ? "Edit Label" : "New Label"}</strong>
                <button onClick={onClose} className="btn btn-sm btn-close" />
            </div>
            <form className="card-body" onSubmit={handleSubmit}>
                {error && <div className="alert alert-danger">{error}</div>}
                {success && (
                    <div className="alert alert-success">
                        {editLabel ? "Label updated!" : "Label created!"}
                    </div>
                )}

                <input
                    type="text"
                    className="form-control mb-2"
                    placeholder="Label name"
                    value={labelName}
                    onChange={(e) => setLabelName(e.target.value)}
                    required
                />
                <button id='createLabelButton' className="btn btn-primary w-100" type="submit">
                    {editLabel ? "Update" : "Create"}
                </button>
            </form>
        </div>
    );
}

export default CreateLabelPopup;

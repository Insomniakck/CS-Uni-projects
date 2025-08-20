import { useState } from "react";

export default function PasswordField({ id, label, value, onChange }) {
  const [visible, setVisible] = useState(false);

  return (
    <div className="mb-3 position-relative">
      <label htmlFor={id} className="form-label">{label}</label>
      <div className="position-relative">
        {/* Eye icon */}
        <i
          className={`bi ${visible ? "bi-eye" : "bi-eye-slash"}`}
          onClick={() => setVisible((v) => !v)}
          role="button"
          style={{
            position: "absolute",
            top: "50%",
            right: "10px",
            transform: "translateY(-50%)",
            cursor: "pointer",
            zIndex: 2,
            color: "#6c757d",
          }}
        />

        <input
          id={id}
          type={visible ? "text" : "password"}
          className="form-control pe-5"
          value={value}
          onChange={onChange}
        />
      </div>
    </div>
  );
}

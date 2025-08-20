// Toast.js
import { useEffect } from "react";

function Toast({ message, onClose, duration =5000 }) {
  useEffect(() => {
    const timer = setTimeout(onClose, duration);
    return () => clearTimeout(timer);
  }, [onClose, duration]);

  return (
    <div style={{
      position: "fixed",
      bottom: "20px",
      right: "20px",
      backgroundColor: "#175e3f",
      color: "#ffffff",
      padding: "10px 16px",
      borderRadius: "8px",
      zIndex: 9999,
      boxShadow: "0 0 8px rgba(0,0,0,0.3)",
      fontSize: "0.9rem",
    }}>
      {message}
    </div>

  );
}

export default Toast;

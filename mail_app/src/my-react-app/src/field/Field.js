function Field({ id, label, type = "text", value, onChange, placeholder }) {
    return (
        <div className="form-floating mb-2">
            <input
                type={type}
                className="form-control"
                id={id}
                placeholder={placeholder || label}
                value={value}
                onChange={onChange}
            />
            <label htmlFor={id}>{label}</label>
        </div>
    );
}

export default Field;

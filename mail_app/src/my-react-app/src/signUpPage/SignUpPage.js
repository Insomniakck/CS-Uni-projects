import { useState } from "react";
import Field from "../field/Field";
import PasswordField from "./PasswordField"
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

function SignUpPage() {
    const [imageSrc, setImageSrc] = useState(null);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [birthday, setBirthday] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");

    const navigate = useNavigate();

    const handleImageChange = (event) => {
        const [file] = event.target.files;
        if (file) {
            setImageSrc(URL.createObjectURL(file));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

       if (!/^\p{L}+$/u.test(firstName)) {
            setError("First name must contain only letters.");
            return;
        }

        if (!/^\p{L}+$/u.test(lastName)) {
            setError("First name must contain only letters.");
            return;
        }
        

        if (password !== confirmPassword) {
            setError("Passwords do not match.");
            return;
        }
        const formData = new FormData();
        formData.append("firstName", firstName);
        formData.append("lastName", lastName);
        formData.append("birthday", birthday);
        formData.append("username", username);
        formData.append("password", password);

        const fileInput = document.getElementById("formFile");
        if (fileInput.files[0]) {
            formData.append("profileImage", fileInput.files[0]);
        }

        try {
            const res = await fetch("http://localhost:8080/api/users", {
                method: "POST",
                body: formData,
            });


            if (!res.ok) {
                try {
                    const errorData = await res.json();
                    setError(errorData.error || "Signup failed.");
                } catch {
                    const text = await res.text();
                    setError(text || "Signup failed.");
                }
                return;
            }

            let data = null;
            const contentLength = res.headers.get("Content-Length");
            if (contentLength && parseInt(contentLength) > 0) {
                data = await res.json();
            }

            console.log("User created successfully", data);
            setError("User created successfully");

            setTimeout(() => {
                navigate('/login');
            }, 1000);
        } catch (err) {
            console.error("Fetch error:", err);
            setError("Server error. Please try again later.");
        }
    };

    return (
        <div className="light-signup-page d-flex align-items-center py-4" 
            data-bs-theme="light"
            style={{ minHeight: "100vh" }}>
            <main className="form-signin w-100 m-auto text-center" 
                style={{ 
                    maxWidth: "400px", 
                    padding: "2rem"
                     }}>
                <i className="bi bi-person-circle" 
                    style={{ fontSize: "2rem" }}></i>
                <h1 className="h3 mb-3 fw-normal">Create an account</h1>

                <form onSubmit={handleSubmit}>
                    <Field
                        id="firstName"
                        label="First name"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                    />

                    <Field
                        id="lastName"
                        label="Last name"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                    />

                    <small className="form-text text-muted d-block text-start ms-0 mb-2">
                        Please enter format DD/MM/YYYY
                    </small>

                    <Field
                        id="birthday"
                        label="Birthdate"
                        value={birthday}
                        onChange={(e) => setBirthday(e.target.value)}
                    />

                    <Field
                        id="email"
                        label="Email"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />

                    <small className="form-text text-muted d-block text-start ms-0 mb-2">
                        Password must contain at least 8 characters, both letters and numbers
                    </small>

                    <PasswordField
                    id="password"
                    label="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    />

                    <PasswordField
                    id="confirmPassword"
                    label="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    />

                    <div className="mb-2">
                        <input
                            className="form-control"
                            type="file"
                            id="formFile"
                            accept="image/*"
                            onChange={handleImageChange}
                        />
                    </div>

                    <div className="mb-2 text-center">
                        <div className="preview-wrapper">
                            {imageSrc && (
                                <img
                                    src={imageSrc}
                                    alt="Profile preview"
                                    className="rounded-circle"

                                    style={{ width: "130px", height: "130px", objectFit: "cover" }}
                                />
                            )}
                        </div>
                    </div>

                    {error && (
                        <div className={`alert ${error.includes("successfully") ? "alert-success" : "alert-danger"}`} role="alert">
                            {error}
                        </div>
                    )}

                    <button className="w-100 btn btn-lg btn-primary" type="submit">
                        Sign up
                    </button>
                </form>

                <p className="text-center text-muted mt-5 mb-0">
                    Already have an account?{" "}
                    <Link to="/login" className="fw-bold text-body">
                        <u>Login here</u>
                    </Link>
                </p>

            </main>
        </div>
    );
}

export default SignUpPage;

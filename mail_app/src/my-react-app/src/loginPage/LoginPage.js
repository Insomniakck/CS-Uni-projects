import { useState } from "react";
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault(); // prevent form reload
        setError("");

        try {
            const res = await fetch("http://localhost:8080/api/tokens", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username: email, password }),
            });

            const data = await res.json();

            if (!res.ok) {
                setError(data.error);
            } else {
                // Login successful
                const id = data;
                console.log("Logged in with ID:", id);
                //save userID to persist across refreshes and reloads
                localStorage.setItem("userID", id);

                // redirect to /api/mails
                navigate('/mails/inbox', { state: { userID: id } });


            }
        } catch (err) {
            setError("Server error. Please try again later.");
        }
    };

    return (
        <div
            className="d-flex align-items-center py-4 light-login-page"
            data-bs-theme="light"
            style={{ minHeight: "100vh" }}
        >
            <main
                className="form-signin w-100 m-auto text-center"
                style={{ maxWidth: "400px", padding: "2rem" }}
            >
                <img className="mb-4" src="/pictures/greenLogo.png" alt="gmail logo" width="120" />
                <h1 className="h3 mb-3 fw-normal">Please sign in</h1>

                <form onSubmit={handleSubmit}>
                    <div className="form-floating mb-2">
                        <input
                            type="email"
                            className="form-control"
                            id="floatingInput"
                            placeholder="name@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                        <label htmlFor="floatingInput">Email address</label>
                    </div>
                    <div className="form-floating mb-2">
                        <input
                            type="password"
                            className="form-control"
                            id="floatingPassword"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <label htmlFor="floatingPassword">Password</label>
                    </div>

                    {error && <div className="text-danger mb-2">{error}</div>}

                    <button className="w-100 btn btn-lg btn-primary" type="submit">
                        Sign in
                    </button>
                </form>

                <p className="mt-5 mb-3">
                    <Link to="/signup">New to Gmail? Sign up</Link>
                </p>
            </main>
        </div>
    );
}

export default LoginPage;
function AppLogo() {
    return (
        <div className="navbar-brand d-flex align-items-center">
            <img
                src="/pictures/greenLogo.png"
                alt="Logo"
                width="55"
                className="me-2"
            />
            <span id="appName" className="mb-0 h1" style={{ fontSize: "1.8rem" }}>
                Gmail
            </span>
        </div>
    )
}

export default AppLogo
import { useNavigate } from "react-router-dom";

function UserCard({ userImage, firstName, username }) {
    const navigate = useNavigate();
    const handleLogout = () => {
        navigate("/login");
    };

    return (
        <div className={`card shadow user-card-dark`} style={{ width: '300px', height: '300px' } }>

            <div className="card-body text-center">
                <div className="profile-ring mb-2">
                    <img
                        src={userImage}
                        className="rounded-circle"
                        alt="User"
                        width="150"
                        height="150"
                    />
                </div>
                <h5 className="card-title mb-0">Hi, {firstName}!</h5>
                <h6 className="card-title mb-0">{username}</h6>

                <button
                    id="logoutButton"
                    className="btn btn-primary btn mt-2"
                    onClick={handleLogout}
                >
                    Log out
                </button>
            </div>
        </div>
    );
}

export default UserCard;

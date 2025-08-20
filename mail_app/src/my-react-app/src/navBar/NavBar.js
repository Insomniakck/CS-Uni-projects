import Search from "../search/Search";
import UserCard from "../userCard/UserCard";
import AppLogo from "../appLogo/AppLogo";
import { useEffect, useState, useRef } from "react";


function NavBar({ userId, setMails, darkMode, setDarkMode, fetchSenderInfo }) {
    const [user, setUser] = useState(null);
    const [showUserCard, setShowUserCard] = useState(false);
    const userCardClick = () => setShowUserCard(prev => !prev);
    const userCardButtonRef = useRef(null);
    const userCardRef = useRef(null);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const res = await fetch(`http://localhost:8080/api/users/${userId}`, {
                    headers: { Accept: "application/json" },
                });
                if (!res.ok) throw new Error("Failed to fetch user");
                const data = await res.json();
                setUser(data);
            } catch (err) {
                console.error("Error fetching user:", err);
            }
        };

        if (userId) {
            fetchUser();
        }
    }, [userId]);


    // Close user card on outside click
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (
                userCardButtonRef.current &&
                userCardRef.current &&
                !userCardButtonRef.current.contains(event.target) &&
                !userCardRef.current.contains(event.target)
            ) {
                setShowUserCard(false);
            }
        };

        document.addEventListener("click", handleClickOutside);
        return () => document.removeEventListener("click", handleClickOutside);

    }, []);

    return (
        <nav className="navbar fixed-top bg-body-tertiary" style={{ height: '100px' }}>
            <div className="container-fluid h-100 align-items-center d-flex">

                {/* Logo */}
                <AppLogo />

                {/* Search */}
                <div className="flex-grow-1 d-flex justify-content-center">
                    <div style={{ width: '60%' }}>
                        <Search
                            userId={userId}
                            onSearchResults={(results) => {
                                setMails(results);
                                fetchSenderInfo(results);
                            }} />
                    </div>
                </div>

                {/* Night mode */}
                <div className="d-flex align-items-center gap-3 position-relative flex-shrink-0">
                    <i
                        id="nightModeMoon"
                        className= { darkMode ? "bi bi-brightness-low-fill fs-4" : "bi bi-moon-fill fs-4"}
                        style={{
                            transform: darkMode ? "scale(1.4)" : "scale(1)",
                        }}
                        role="button"
                        onClick={() => setDarkMode(prev => !prev)}
                        title="Night Mode"
                    ></i>

                    {/* profile image */}

                    <div ref={userCardButtonRef} className="profile-ring">
                        <img
                            src={user?.profileImage ? `http://localhost:8080/uploads/${user.profileImage}` : "/pictures/defaultProfile.png"}
                            alt="User"
                            width="50"
                            height="50"
                            className="rounded-circle"
                            role="button"
                            onClick={userCardClick}

                        />
                    </div>

                    {showUserCard && (
                        <div
                            ref={userCardRef}
                            className="position-absolute top-100 end-0 mt-2"
                        >
                            <UserCard
                                userImage={user?.profileImage ? `http://localhost:8080/uploads/${user.profileImage}` : "/pictures/defaultProfile.png"}
                                firstName={user.firstName}
                                username={user.userName}
                            />
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default NavBar;

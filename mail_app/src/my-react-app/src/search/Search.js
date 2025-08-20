import { useState } from "react";
import { searchString } from "../APIfunctions/APIfunctions";

function Search({ userId, onSearchResults }) {
    const [query, setQuery] = useState("");
    const [error, setError] = useState("");
    console.log(error)
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!query.trim()) return;

        try {
            const results = await searchString(userId, query);
            onSearchResults(results);  // update the mail list
        } catch (err) {
            setError("Search failed.");
            console.error("Search error:", err);
        }
    };

    return (
        <form className="d-flex" role="search" onSubmit={handleSubmit}>
            <input
                id="searchBox"
                className="form-control me-2"
                type="search"
                placeholder="Search"
                aria-label="Search"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
            />
        </form>
    );
}

export default Search;
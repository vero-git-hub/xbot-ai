import React, { useState } from "react";
import "../styles/page/SocialAccountPage.css";
import API from "../api.ts";
import { useNavigate } from "react-router-dom";

const SocialAccountPage: React.FC = () => {
    const [username, setUsername] = useState("");
    const [userId, setUserId] = useState("");
    const [apiKey, setApiKey] = useState("");
    const [apiSecretKey, setApiSecretKey] = useState("");
    const [accessToken, setAccessToken] = useState("");
    const [accessTokenSecret, setAccessTokenSecret] = useState("");
    const [message, setMessage] = useState<string | null>(null);

    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const token = localStorage.getItem("token");
        if (!token) {
            navigate("/login");
            return;
        }

        const config = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        const data = {
            username,
            userId,
            apiKey,
            apiSecretKey,
            accessToken,
            accessTokenSecret,
        };

        try {
            const response = await API.post("/social-account/save", data, config);
            setMessage("Settings saved successfully!");
            console.log(response.data);
        } catch (error: unknown) {
            let errorMessage = "Unknown error";
            if (error instanceof Error) {
                errorMessage = error.message;
            }
            console.error("Error saving settings:", errorMessage);
            setMessage("Error saving settings. Please try again.");
        }
    };

    return (
        <div className="page-container">
            <header>
                <h1>Social Account Settings</h1>
                <p>Connect your X Developer Platform account to enable bot functionality</p>
                <p>You can find these credentials in your X Developer Platform dashboard</p>
            </header>
            <main>
                <form onSubmit={handleSubmit} className="form-container">
                    <div className="form-group">
                        <input
                            id="username"
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Username"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <input
                            id="userId"
                            type="text"
                            value={userId}
                            onChange={(e) => setUserId(e.target.value)}
                            placeholder="User ID"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <input
                            id="apiKey"
                            type="text"
                            value={apiKey}
                            onChange={(e) => setApiKey(e.target.value)}
                            placeholder="API Key"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <input
                            id="apiSecretKey"
                            type="text"
                            value={apiSecretKey}
                            onChange={(e) => setApiSecretKey(e.target.value)}
                            placeholder="API Secret"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <input
                            id="accessToken"
                            type="text"
                            value={accessToken}
                            onChange={(e) => setAccessToken(e.target.value)}
                            placeholder="Access Token"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <input
                            id="accessTokenSecret"
                            type="text"
                            value={accessTokenSecret}
                            onChange={(e) => setAccessTokenSecret(e.target.value)}
                            placeholder="Access Token Secret"
                            required
                        />
                    </div>
                    <div className="button-group">
                        <button type="submit" className="submit-button">Save Settings</button>
                        <button type="button" onClick={() => navigate("/")}>⬅ Back to Dashboard</button>
                    </div>
                </form>
                {message && <p className={message.includes("successfully") ? "success" : "error"}>{message}</p>}
            </main>
        </div>
    );
};

export default SocialAccountPage;

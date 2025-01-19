import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import {useAuth} from "./AuthContext";

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const { login } = useAuth();

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("http://localhost:8000/login", {
                email,
                password,
            });
            console.log("Login Successful", response.data);
            localStorage.setItem("token", response.data.token);

            login()

            navigate("/dashboard");
        } catch (error) {
            console.error("Login Failed", error.response?.data || error.message);
            setError("Invalid email or password.");
        }
    };

    return (
           <div style={{ maxWidth: "400px", margin: "auto", padding: "1rem" }}>
          <h2>Login</h2>
           {error && <p style={{ color: "red" }}>{error}</p>}
          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: "1rem" }}>
              <label>Email:</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                style={{ width: "100%", padding: "0.5rem" }}
              />
            </div>
            <div style={{ marginBottom: "1rem" }}>
              <label>Password:</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                style={{ width: "100%", padding: "0.5rem" }}
              />
            </div>
            <button type="submit" style={{ padding: "0.5rem", width: "100%" }}>
              Login
            </button>
          </form>
        </div>
    );
};

export default Login;
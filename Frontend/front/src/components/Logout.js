import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {useAuth} from "./AuthContext";

const Logout = () => {
  const navigate = useNavigate();
  const { logout } = useAuth(); // Access the logout function
  const handleLogout = async () => {
    try {
      await axios.post(
        `http://localhost:8000/logout`,
        {"token": localStorage.getItem("token")},
      );

      logout()

      localStorage.removeItem("token")

      navigate("/login");
    } catch (error) {
      console.error("Error logging out:", error);
    }
  };

  return <div>
    <button onClick={handleLogout}>
      Logout
    </button>
  </div>;
};

export default Logout;

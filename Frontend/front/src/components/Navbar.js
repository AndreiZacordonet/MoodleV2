import React, { useEffect, useState } from "react";
import { Link, NavLink } from "react-router-dom";
import {useAuth} from "./AuthContext";
import Logout from "./Logout";

const Navbar = () => {
  const { isAuthenticated, login, logout } = useAuth();

  return (
    <nav className="navbar">
      <ul className="navbar-links">
          {isAuthenticated && (
            <li>
                <Logout/>
            </li>
          )}
      </ul>
    </nav>
  );
};

export default Navbar;

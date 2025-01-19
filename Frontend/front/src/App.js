import logo from './logo.svg';
import './App.css';
import PrivateRoute from "./components/PrivateRoute";
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import Login from "./components/Login";
import Navbar from "./components/Navbar";
import Logout from "./components/Logout";
import {AuthProvider} from "./components/AuthContext";
import Dashboard from "./components/Dashboard";

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/logout" element={<Logout />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;

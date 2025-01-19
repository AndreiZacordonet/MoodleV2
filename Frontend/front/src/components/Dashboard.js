import React, {useEffect, useState} from 'react';
import { useAuth } from './AuthContext'; // Use AuthContext to check if the user is authenticated
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const Dashboard = () => {
  const { isAuthenticated } = useAuth(); // Access authentication status from context
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem("token"); // Get token from localStorage
        const response = await axios.get('http://localhost:8080/api/academia/links', {
          headers: {
            Authorization: `Bearer ${token}`, // Include the token in the Authorization header
          },
        });

        setData(response.data); // Set the data from the API response
        setLoading(false); // Stop the loading state
      } catch (err) {
        setError(err); // Handle any errors
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  // Show error state
  if (error) {
    return <div>Error: {error.message}</div>;
  }

  if (!isAuthenticated) {
    navigate("/login");
    return null;
  }

  return (
    <div className="dashboard">
      <h1>Welcome to Your Dashboard</h1>

      <div className="dashboard-content">
        <h2>Your Available links</h2>
        <ul>
          <h3>From Academia:</h3>
          {data.map((link) => (
              <li key={link.uri}><b>{link.method}</b>: {link.uri}</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Dashboard;

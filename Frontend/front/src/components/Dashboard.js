import React, {useEffect, useState} from 'react';
import { useAuth } from './AuthContext'; // Use AuthContext to check if the user is authenticated
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const Dashboard = () => {
  const { isAuthenticated } = useAuth(); // Access authentication status from context
  const navigate = useNavigate();
  const [academiaLinks, setAcademiaLinks] = useState(null);
  const [materialsLinks, setMaterialsLinks] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchUserData = async (email, token) => {
      try {
      // Send requests to both APIs
      const response1 = await axios.get(`http://localhost:8080/api/academia/studenti?email=${email}`,{
          headers: {
            Authorization: `Bearer ${token}`, // Include the token in the Authorization header
          },});
      const response2 = await axios.get(`http://localhost:8080/api/academia/profesori?email=${email}`,{
          headers: {
            Authorization: `Bearer ${token}`, // Include the token in the Authorization header
          },});

      // Check which API returned a valid (non-empty) response
      if (response1.data.length > response2.data.length) {
        return response1.data;  // Return the valid result from API 1
      } else
        return response2.data;  // Return the valid result from API 2
    } catch (error) {
      console.error("Error fetching data:", error);
      throw error;  // Re-throw the error to be handled by the caller
    }
  }

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem("token"); // Get token from localStorage
        var response = await axios.get('http://localhost:8080/api/academia/links', {
          headers: {
            Authorization: `Bearer ${token}`, // Include the token in the Authorization header
          },
        });

        setAcademiaLinks(response.data); // Set the data from the API response
        localStorage.setItem("academia_links", JSON.stringify(response.data, null, 2))

        response = await axios.get('http://localhost:8001/api/materials/links', {
          headers: {
            Authorization: `Bearer ${token}`, // Include the token in the Authorization header
          },
        });

        const modifiedLinks = response.data.map(item => {
          return {
            ...item,
            path: item.path
              .replace("[^/]+", '{code}') // Replacing with {code}
                .replace("$", "")
                .replace("^", "")
              .replace("\\d+", '{course_number}') // Replacing with {course_number}
          };
        });

        setMaterialsLinks(modifiedLinks)
        localStorage.setItem("materials_links", JSON.stringify(modifiedLinks, null, 2))

        const email = localStorage.getItem("email")

        const userData = JSON.stringify(await fetchUserData(email, token), null, 2)

        localStorage.setItem("userData", userData)

        setLoading(false);
      } catch (err) {
        setError(err);
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
          {academiaLinks.map((link) => (
              <li key={link.uri}><b>{link.method}</b>: {link.uri}</li>
          ))}
        </ul>
        <ul>
          <h3>From Materials:</h3>
          {materialsLinks.map((link) => (
              <li key={link.path}><b>{link.method}</b>: {link.path}</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Dashboard;

import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import { getUser, logout } from "../services/auth.js";
import StatCard from "../components/StatCard.jsx";

export default function StudentDashboard() {
  const navigate = useNavigate();
  const user = getUser();
  const [stats, setStats] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/dashboard/stats")
      .then((res) => setStats(res.data))
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <>
      <div className="page-head">
        <div>
          <h1>Welcome, {user.name}</h1>
          <p className="muted">Here is a summary of your complaints.</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="stat-grid">
        <StatCard label="Total complaints" value={stats?.totalComplaints} />
        <StatCard label="Open" value={stats?.open} tone="open" />
        <StatCard label="Assigned" value={stats?.assigned} tone="assigned" />
        <StatCard label="In progress" value={stats?.inProgress} tone="progress" />
        <StatCard label="Resolved" value={stats?.resolved} tone="resolved" />
      </div>

      <div className="btn-row">
        <Link to="/student/submit" className="btn btn-primary">Submit Complaint</Link>
        <Link to="/student/complaints" className="btn btn-outline">My Complaints</Link>
        <Link to="/student/profile" className="btn btn-outline">Profile</Link>
        <button className="btn btn-danger" onClick={handleLogout}>Logout</button>
      </div>
    </>
  );
}

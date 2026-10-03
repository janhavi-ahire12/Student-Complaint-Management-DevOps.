import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import StatCard from "../components/StatCard.jsx";

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/dashboard/stats")
      .then((res) => setStats(res.data))
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  return (
    <>
      <div className="page-head">
        <div>
          <h1>Admin dashboard</h1>
          <p className="muted">Overview of students and complaints.</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="stat-grid">
        <StatCard label="Total students" value={stats?.totalStudents} />
        <StatCard label="Total complaints" value={stats?.totalComplaints} />
        <StatCard label="Open complaints" value={stats?.open} tone="open" />
        <StatCard label="In progress" value={stats?.inProgress} tone="progress" />
        <StatCard label="Resolved complaints" value={stats?.resolved} tone="resolved" />
      </div>

      <div className="btn-row">
        <Link to="/admin/complaints" className="btn btn-primary">View all complaints</Link>
        <Link to="/admin/students" className="btn btn-outline">View students</Link>
      </div>
    </>
  );
}

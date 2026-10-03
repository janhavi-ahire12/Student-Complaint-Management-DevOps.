import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import StatCard from "../components/StatCard.jsx";
import StatusBadge from "../components/StatusBadge.jsx";
import { formatDate } from "../services/utils.js";

export default function StaffDashboard() {
  const [stats, setStats] = useState(null);
  const [complaints, setComplaints] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([api.get("/dashboard/stats"), api.get("/complaints")])
      .then(([s, c]) => {
        setStats(s.data);
        setComplaints(c.data);
      })
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  return (
    <>
      <div className="page-head">
        <div>
          <h1>Staff dashboard</h1>
          <p className="muted">Complaints assigned to you.</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="stat-grid">
        <StatCard label="Assigned complaints" value={stats?.totalComplaints} />
        <StatCard label="Open assigned" value={stats?.assigned} tone="assigned" />
        <StatCard label="In progress" value={stats?.inProgress} tone="progress" />
        <StatCard label="Resolved" value={stats?.resolved} tone="resolved" />
      </div>

      <div className="card table-wrap">
        <h3>My assigned complaints</h3>
        {complaints.length === 0 ? (
          <p className="muted">Nothing is assigned to you right now.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Title</th><th>Student</th><th>Priority</th><th>Status</th><th>Submitted</th><th></th>
              </tr>
            </thead>
            <tbody>
              {complaints.map((c) => (
                <tr key={c.id}>
                  <td>#{c.id}</td>
                  <td>{c.title}</td>
                  <td>{c.studentName}</td>
                  <td><span className={`prio prio-${c.priority}`}>{c.priority}</span></td>
                  <td><StatusBadge status={c.status} /></td>
                  <td>{formatDate(c.createdAt)}</td>
                  <td><Link to={`/staff/complaints/${c.id}`} className="btn btn-outline btn-sm">Open</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}

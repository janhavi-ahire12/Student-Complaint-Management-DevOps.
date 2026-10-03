import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { getErrorMessage } from "../services/api.js";
import StatusBadge from "../components/StatusBadge.jsx";
import { STATUSES, formatDate } from "../services/utils.js";

export default function AdminComplaints() {
  const [complaints, setComplaints] = useState([]);
  const [staffList, setStaffList] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const load = () => {
    Promise.all([api.get("/complaints"), api.get("/staff")])
      .then(([c, s]) => {
        setComplaints(c.data);
        setStaffList(s.data);
      })
      .catch((err) => setError(getErrorMessage(err)));
  };

  useEffect(load, []);

  const handleAssign = async (complaint, staffId) => {
    if (!staffId) return;
    setError("");
    setMessage("");
    try {
      await api.put(`/complaints/${complaint.id}/assign/${staffId}`);
      setMessage(`Complaint #${complaint.id} assigned.`);
    } catch (err) {
      setError(getErrorMessage(err));
    }
    load();
  };

  const handleStatus = async (complaint, newStatus) => {
    if (newStatus === complaint.status) return;
    setError("");
    setMessage("");

    let remarks = "";
    if (newStatus === "RESOLVED" || newStatus === "REJECTED") {
      remarks = window.prompt(`Enter remarks for ${newStatus} (required):`);
      if (remarks === null) {
        load();
        return;
      }
    }
    try {
      await api.put(`/complaints/${complaint.id}/status`, { status: newStatus, remarks });
      setMessage(`Complaint #${complaint.id} status changed to ${newStatus}.`);
    } catch (err) {
      setError(getErrorMessage(err));
    }
    load();
  };

  const shown = filter === "ALL" ? complaints : complaints.filter((c) => c.status === filter);

  return (
    <>
      <div className="page-head">
        <h1>All complaints</h1>
        <select value={filter} onChange={(e) => setFilter(e.target.value)}>
          <option value="ALL">All statuses</option>
          {STATUSES.map((s) => (<option key={s} value={s}>{s}</option>))}
        </select>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {message && <div className="alert alert-success">{message}</div>}

      <div className="card table-wrap">
        {shown.length === 0 ? (
          <p className="muted">No complaints found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Title</th><th>Student</th><th>Priority</th><th>Status</th>
                <th>Assign staff</th><th>Change status</th><th>Date</th><th></th>
              </tr>
            </thead>
            <tbody>
              {shown.map((c) => {
                const closed = c.status === "RESOLVED" || c.status === "REJECTED";
                return (
                  <tr key={c.id}>
                    <td>#{c.id}</td>
                    <td>{c.title}</td>
                    <td>{c.studentName}</td>
                    <td><span className={`prio prio-${c.priority}`}>{c.priority}</span></td>
                    <td><StatusBadge status={c.status} /></td>
                    <td>
                      <select
                        value={c.assignedToId || ""}
                        disabled={closed}
                        onChange={(e) => handleAssign(c, e.target.value)}
                      >
                        <option value="">-- select staff --</option>
                        {staffList.map((s) => (
                          <option key={s.id} value={s.id}>{s.name} ({s.department})</option>
                        ))}
                      </select>
                    </td>
                    <td>
                      <select value={c.status} onChange={(e) => handleStatus(c, e.target.value)}>
                        {STATUSES.map((s) => (<option key={s} value={s}>{s}</option>))}
                      </select>
                    </td>
                    <td>{formatDate(c.createdAt)}</td>
                    <td><Link to={`/admin/complaints/${c.id}`} className="btn btn-outline btn-sm">View</Link></td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
